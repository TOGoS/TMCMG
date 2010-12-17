package togos.minecraft.mapgen.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import togos.genfs.GenFSUtil;
import togos.genfs.Stat;
import togos.genfs.err.DoesNotExistError;
import togos.genfs.err.FSError;
import togos.genfs.err.InvalidOperationError;
import togos.genfs.err.ServerError;
import togos.genfs.server.GenFSServer;
import togos.genfs.server.GenFSServer.AliasResponse;
import togos.mf.api.CallHandler;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.PathUtil;
import togos.minecraft.mapgen.app.ChunkWriter;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise2.lang.TNLCompiler;


public class ChunkServer
{
	PrintStream debugStream = null; // System.err;
	
	class ChunkWriteJob {
		String filename;
		ChunkGenerator cg;
		Thread thread;
		int x, y;
		
		public ChunkWriteJob( String filename, ChunkGenerator cg, int x, int y ) {
			this.filename = filename;
			this.cg = cg;
			this.x = x;
			this.y = y;
		}
		
		public void run() {
			File cf = new File(filename);
			if( cf.exists() ) return;
			
			if( debugStream != null ) debugStream.println("Generating "+filename+"...");
			
			// TODO: use resMan to get chunk data
			
			try {
				ChunkData cd = cg.getChunkData(x, y);
				
				File pd = cf.getParentFile();
				if( !pd.exists() ) pd.mkdirs();
				FileOutputStream fos = new FileOutputStream(cf);
				try {
					ChunkWriter.instance.writeChunk( cd, fos );
				} finally {
					fos.close();
				}
				if( debugStream != null ) debugStream.println("Done generating "+filename);
			} catch( IOException e ) {
				if( debugStream != null ) debugStream.println("Error generating "+filename);
				throw new RuntimeException(e);
			} catch( RuntimeException e ) {
				if( debugStream != null ) debugStream.println("Error generating "+filename);
				throw e;
			}
		}
		
		public void join() throws InterruptedException {
			thread.join();
		}
	}
	
	HashMap chunkWriteJobs = new HashMap();
	public ChunkWriteJob getChunkWriteJob( final String filename, ChunkGenerator cg, int x, int y ) {
		synchronized( chunkWriteJobs ) {
			ChunkWriteJob cwj = (ChunkWriteJob)chunkWriteJobs.get( filename );
			if( cwj == null ) {
				final ChunkWriteJob _cwj = cwj = new ChunkWriteJob( filename, cg, x, y );
				chunkWriteJobs.put( filename, cwj );
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							_cwj.run();
						} finally {
							removeChunkWriteJob(filename);
						}
					}
				});
				cwj.thread = t;
				t.start();
			}
			return cwj;
		}
	}
	
	public void removeChunkWriteJob( String filename ) {
		synchronized( chunkWriteJobs ) {
			chunkWriteJobs.remove(filename);
		}
	}
	
	static class PathInfo {
		// Throwing junk in here because I'm tired.
		// Well it seems to work alright for now...
		public String prp;
		public String path;
		public String dim;
		public int[] chunkCoords;
		public PathInfo( String prp, String path, String dim, int[] chunkCoords ) {
			this.prp = prp;
			this.path = path;
			this.dim = dim;
			this.chunkCoords = chunkCoords;
		}
	}
	
	public class GenFSRequestHandler implements GenFSServer.RequestHandler {
		String rootDir;
		public GenFSRequestHandler( String rootDir ) {
			this.rootDir = rootDir;
		}
		
		final Pattern DIMPAT = Pattern.compile("/(DIM-[^/]+)(/.*)?");
		final Pattern CHUNKDIRPAT = Pattern.compile("DIM-[^/]+(?:/[0-9a-z]+)*/?");
		
		protected PathInfo ex2in( String extPath ) {
			String dim;
			String in;
			Matcher dm = DIMPAT.matcher(extPath);
			if( "/".equals(extPath) ) {
				dim = "";
				in = "";
			} else if( dm.matches() ) {
				dim = dm.group(1);
				in = dm.group(2);
			} else {
				dim = "DIM-0";
				in = "/"+extPath.substring(1);
			}
			if( in == null ) in = "/";
			int[] xy = PathUtil.chunkCoords(in);
			if( xy == null ) {
				// Then not a chunk
				String prp = dim + in;
				return new PathInfo(
					prp,
					rootDir + "/" + prp,
					dim, null
				);
			}
			
			// Otherwise transform the coords to a form that's easier to
			// back up:
			String prp = dim + "/" + PathUtil.qtChunkDir(xy[0], xy[1]) + "/" +
				PathUtil.chunkBaseName(xy[0], xy[1]); 
			return new PathInfo(
				prp, rootDir + "/" + prp,
				dim, xy
			);
		}
		
		public Stat getStat( String path ) throws FSError {
			PathInfo pi = ex2in(path);
			File f = new File(pi.path);
			ChunkGenerator cg;
			if( f.exists() ) {
				return GenFSUtil.getStat(f);
			} else if( CHUNKDIRPAT.matcher(pi.prp).matches() ) {
				return new Stat( 42, GenFSUtil.DIRECTORY_MODE | GenFSUtil.DEFAULT_DIRECTORY_PERM_BITS );
			} else if( pi.chunkCoords != null && (cg = (ChunkGenerator)generators.get(pi.dim)) != null ) {
				getChunkWriteJob(pi.path, cg, pi.chunkCoords[0], pi.chunkCoords[1] );
				return new Stat( 42, GenFSUtil.FILE_MODE | GenFSUtil.DEFAULT_FILE_PERM_BITS );
			} else {
				throw new DoesNotExistError();
			}
		}
		
		public List getDirEntries( String path ) throws FSError {
			PathInfo pi = ex2in(path);
			System.err.println("getDirEntries: Real path = "+pi.path);
			File f = new File(pi.path);
			if( f.isDirectory() ) {
				System.err.println(f.getPath()+" is dir...");
				File[] sf = f.listFiles();
				List entries = new ArrayList();
				for( int i=0; i<sf.length; ++i ) {
					entries.add(GenFSUtil.getDirEntry(sf[i]));
				}
				System.err.println(entries.size()+" entries");
				return entries;
			} else if( f.isFile() ) {
				throw new InvalidOperationError();
			} else if( CHUNKDIRPAT.matcher(pi.prp).matches() ) {
				return Collections.EMPTY_LIST;
			} else {
				throw new DoesNotExistError();
			}
		}
		
		protected PathInfo open( String path ) {
			PathInfo pi = ex2in(path);
			ChunkGenerator cg = (ChunkGenerator)generators.get(pi.dim);
			if( cg != null && pi.chunkCoords != null ) {
				ChunkWriteJob cwj = getChunkWriteJob(pi.path, cg, pi.chunkCoords[0], pi.chunkCoords[1]);
				try { 
					cwj.join();
				} catch( InterruptedException e ) {
					throw new RuntimeException(e);
				}
			}
			return pi;
		}
		
		public AliasResponse openRead( String path ) throws FSError {
			return new AliasResponse( open(path).path );
		}
		
		public AliasResponse openWrite( String path ) throws FSError {
			return new AliasResponse( open(path).path );
		}
		
		public void closeRead( String path, String aliasedPath ) throws FSError {
		}
		
		public void closeWrite( String path, String aliasedPath ) throws FSError {
		}
		
		public void truncate( String path ) throws FSError {
			File f = new File(ex2in(path).path);
			if( f.isDirectory() ) {
				throw new InvalidOperationError();
			} else if( f.exists() ) {
				try {
					new FileOutputStream(f).close();
				} catch( IOException e ) {
					throw new ServerError(e);
				}
			} else {
				throw new DoesNotExistError();
			}
		}
	}
	
	ResourceManager resMan = new ResourceManager();
	HashMap generators = new HashMap();
	
	public void addChunkGenerator( final String name, final ChunkGenerator cGen ) {
		generators.put( name, cGen );
		resMan.addResourceLoader(new CallHandler() {
			public Response call( Request req ) {
				if( req.getResourceName().startsWith("/"+name+"/") ) {
					return cGen.call(req);
				}
				return BaseResponse.RESPONSE_UNHANDLED;
			}
		});
	}
	
	public GenFSRequestHandler getGenFSHandler( String rootDir ) {
		return new GenFSRequestHandler( rootDir );
	}
	
	public void initHandlers( WebServer ws ) {
		ws.addRequestHandler(new CallHandler() {
			public Response call( Request req ) {
				if( !"/".equals(req.getResourceName()) ) {
					return BaseResponse.RESPONSE_UNHANDLED;
				}
				
				BaseResponse res = new BaseResponse();
				res.status = 200;
				res.putContentMetadata(BaseResponse.DC_FORMAT, "text/html; charset=utf-8");
				String content = "<html><head><title>TMCMG Chunk Server</title></head><body>\n";
				content += "<h2>Welcome to the chunk server!</h2>\n";
				if( generators.size() == 0 ) {
					content += "<p>No generators</p>\n";
				} else {
					content += "<p>Generators:</p><ul>\n";
					for( Iterator i=generators.entrySet().iterator(); i.hasNext(); ) {
						Map.Entry ge = (Map.Entry)i.next();
						content += "<li>" + ge.getKey() + ": " + ((ChunkGenerator)ge.getValue()).scriptFile + "</li>\n";
					}
					content += "</ul>\n";
				}
				content += "</body></html>\n";
				try {
	                res.content = content.getBytes("UTF-8");
                } catch( UnsupportedEncodingException e ) {
                	throw new RuntimeException(e);
                }
                return res;
			}
		});
		ws.addRequestHandler(resMan);
	}
	
	public static final String USAGE =
		"Usage: ChunkServer <worldName>=<script> [options]\n" +
		"Options:\n" +
		"  -ws-port <port>   ; port to run web server on\n" +
		"  -gfs-port <port>  ; port to run GenFS server on\n" +
		"  -data-root <dir>  ; dir to store generated chunks";
	
	public static void main(String[] args) {
		WebServer ws = null;
		GenFSServer gfss = null;
		String gfsRoot = null;
		ChunkServer cs = new ChunkServer();
		Map generatorScripts = new HashMap();
		boolean debug = false;
		
		for( int i=0; i<args.length; ++i ) {
			if( args[i].contains("=") ) {
				String name = args[i].substring(0,args[i].indexOf('='));
				String path = args[i].substring(args[i].indexOf('=')+1);
				generatorScripts.put( name, path );
			} else if( "-ws-port".equals(args[i]) ) {
				if( ws == null ) ws = new WebServer();
			    ws.port = Integer.parseInt(args[++i]);
			} else if( "-gfs-port".equals(args[i]) ) {
				if( gfss == null ) gfss = new GenFSServer();
				gfss.listenPort = Integer.parseInt(args[++i]);
			} else if( "-data-root".equals(args[i]) ) {
				gfsRoot = args[++i];
				if( gfsRoot.endsWith("/") ) {
					gfsRoot = gfsRoot.substring(0,gfsRoot.length()-1);
				}
			} else if( "-debug".equals(args[i]) ) {
				debug = true;
				cs.debugStream = System.err;
			} else {
				System.err.println("Unrecognised argument: "+args[i]);
				System.err.println(USAGE);
				System.exit(1);
			}
		}
		
		TNLCompiler compiler = new TNLWorldGeneratorCompiler();
		
		for( Iterator i=generatorScripts.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry ge = (Map.Entry)i.next();
			String name = (String)ge.getKey();
			String scriptPath = (String)ge.getValue();
			cs.addChunkGenerator( name, new ChunkGenerator(compiler, scriptPath) ); 
		}
		
		if( ws != null ) {
			cs.initHandlers( ws );
		}
		if( gfss != null ) {
			if( debug ) {
				gfss.debugStream = System.err;
			}
			if( gfsRoot == null ) {
				System.err.println("No -gfs-root specified");
				System.exit(1);
			}
			gfss.requestHandler = cs.getGenFSHandler(gfsRoot);
		}
		
		new Thread(gfss).start();
		new Thread(ws).start();
	}
}
