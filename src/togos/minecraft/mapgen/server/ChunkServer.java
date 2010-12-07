package togos.minecraft.mapgen.server;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import togos.mf.api.CallHandler;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.noise2.lang.TNLCompiler;


public class ChunkServer
{
	ResourceManager resMan = new ResourceManager();
	HashMap generators = new HashMap();
	
	public void addChunkGenerator( final String name, final ChunkGenerator cGen ) {
		generators.put( name, cGen );
		resMan.addResourceLoader(new CallHandler() {
			public Response call( Request req ) {
				if( req.getResourceName().startsWith("/"+name) ) {
					return cGen.call(req);
				}
				return BaseResponse.RESPONSE_UNHANDLED;
			}
		});
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
		"Usage: ChunkServer <worldName>=<script> [-port <listen-port>]";
	
	public static void main(String[] args) {
		WebServer ws = new WebServer();
		ChunkServer cs = new ChunkServer();
		cs.initHandlers( ws );
		Map generatorScripts = new HashMap();
		
		for( int i=0; i<args.length; ++i ) {
			if( args[i].contains("=") ) {
				String name = args[i].substring(0,args[i].indexOf('='));
				String path = args[i].substring(args[i].indexOf('=')+1);
				generatorScripts.put( name, path );
			} else if( "-port".equals(args[i]) ) {
			    ws.port = Integer.parseInt(args[++i]);
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
		
		try {
			ws.run();
		} catch( Exception e ) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
