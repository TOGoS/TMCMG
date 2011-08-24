package togos.minecraft.mapgen.io;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import org.jnbt.CompoundTag;

import togos.minecraft.mapgen.PathUtil;
import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.util.ByteChunk;
import togos.minecraft.mapgen.world.gen.ChunkGenerator;
import togos.minecraft.mapgen.world.gen.SimpleWorldGenerator;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise2.lang.ParseUtil;
import togos.noise2.lang.ScriptError;

public class ChunkWriter
{
	public static String chunkPath( int x, int z ) {
		return PathUtil.mcChunkDir(x,z) + "/" + PathUtil.chunkBaseName(x,z);
	}
	
	public static int chunkX( ChunkData cd ) { return (int)(cd.getChunkPositionX()/cd.getChunkWidth()); }
	public static int chunkZ( ChunkData cd ) { return (int)(cd.getChunkPositionZ()/cd.getChunkDepth()); }
	
	public static void writeChunk( ChunkData cd, DataOutputStream os ) throws IOException {
		BetterNBTOutputStream nbtos = new BetterNBTOutputStream(os);
		
		HashMap levelRootTags = new HashMap();
		levelRootTags.put("Level",cd.toTag());
		CompoundTag fileRootTag = new CompoundTag("",levelRootTags);
		
		nbtos.writeTag(fileRootTag);
		nbtos.close();
	}
	
	public static ByteChunk serializeChunk( ChunkData cd, int format ) {
		BetterByteArrayOutputStream baos = new BetterByteArrayOutputStream();
		try {
			writeChunk( cd, new DataOutputStream(
				format == RegionFile.VERSION_GZIP ? new GZIPOutputStream(baos) : new DeflaterOutputStream(baos)
			) );
		} catch( IOException e ) {
			throw new RuntimeException("IOException while serializing chunk", e);
		}
		return baos;
	}
	
	public static void writeChunkToFile( ChunkData cd, String worldDir ) throws IOException {
		String fullPath = worldDir + "/" + chunkPath( chunkX(cd), chunkZ(cd) );
		File f = new File(fullPath);
		File dir = f.getParentFile();
		if( dir != null && !dir.exists() ) dir.mkdirs();
		FileOutputStream os = new FileOutputStream(f);
		try {
			writeChunk( cd, new DataOutputStream(new GZIPOutputStream(os)) );
		} finally {
			os.close();
		}
	}
	
	public static void writeChunkToRegionFile( int cx, int cz, ByteChunk data, int format, String baseDir ) throws IOException {
		RegionFileCache.getRegionFile(new File(baseDir), cx, cz).write( cx&31, cz&31, data.getBuffer(), data.getSize(), format );
	}
	
	public static void writeChunkToRegionFile( ChunkData cd, String baseDir, int format ) throws IOException {
		int cx = (int)(cd.getChunkPositionX()/cd.getChunkWidth());
		int cz = (int)(cd.getChunkPositionZ()/cd.getChunkDepth());
		writeChunkToRegionFile( cx, cz, serializeChunk(cd,RegionFile.VERSION_DEFLATE), RegionFile.VERSION_DEFLATE, baseDir );
	}
	
	////
	
	public static final int FORMAT_CHUNKS = 1;
	public static final int FORMAT_REGION = 2;
	
	protected String worldDir;
	int worldFormat = FORMAT_REGION;
	
	public ChunkWriter( String worldDir ) {
		this.worldDir = worldDir;
	}
	
	public ChunkWriter() {
		this( null );
	}
	
	/**
	 * This method should be thread-safe, as os.close() calls
	 * RegionFile.write, which is synchronized
	 * */
	public void saveChunk( int cx, int cz, ByteChunk data, int format ) throws IOException {
		switch( worldFormat ) {
		case( FORMAT_CHUNKS ):
			if( format != RegionFile.VERSION_GZIP ) {
				throw new RuntimeException("Can't save non-region-format chunk in non-gzip format");
			}
			throw new UnsupportedOperationException("Saving chunk data to files not supported");
		case( FORMAT_REGION ):
			writeChunkToRegionFile( cx, cz, data, format, worldDir );
			break;
		default:
			throw new RuntimeException("Unsupported world format: "+worldFormat);
		}
	}
	
	/**
	 * This method should be thread-safe, as os.close() calls
	 * RegionFile.write, which is synchronized
	 * */
	public void saveChunk( ChunkData cd ) throws IOException {
		switch( worldFormat ) {
		case( FORMAT_CHUNKS ):
			// Slight shortcut (writes directly instead of serializing first)
			writeChunkToFile( cd, worldDir );
			break;
		case( FORMAT_REGION ):
			saveChunk( chunkX(cd), chunkZ(cd), serializeChunk(cd,RegionFile.VERSION_DEFLATE), RegionFile.VERSION_DEFLATE );
			break;
		default:
			throw new RuntimeException("Unsupported world format: "+worldFormat);
		}
	}
	
	public static String USAGE =
		"Usage: ChunkWriter [options]\n" +
		"\n" +
		"Options:\n" +
		"  -world-dir <dir>  ; directory under which to store chunk data\n" +
		"  -x, -z, -width, -depth  ; bounds of area to generate";
	
	public static void main(String[] args) {
		int boundsX = 0;
		int boundsZ = 0;
		int boundsWidth = 1;
		int boundsDepth = 1;
		String worldDir = ".";
		String scriptFile = null;
		for( int i=0; i<args.length; ++i ) {
			if( "-world-dir".equals(args[i]) ) {
				worldDir = args[++i];
			} else if( "-x".equals(args[i]) ) {
				boundsX = Integer.parseInt(args[++i]);
			} else if( "-z".equals(args[i]) ) {
				boundsZ = Integer.parseInt(args[++i]);
			} else if( "-width".equals(args[i]) ) {
				boundsWidth = Integer.parseInt(args[++i]);
			} else if( "-depth".equals(args[i]) ) {
				boundsDepth = Integer.parseInt(args[++i]);
			} else if( !args[i].startsWith("-") ) {
				scriptFile = args[i];
			} else {
				System.err.println("Unrecognised argument: "+args[i]);
				System.err.println(USAGE);
				System.exit(1);
			}
		}
		
		try {
			WorldGenerator worldGenerator;
			if( scriptFile != null ) {
				try {
					worldGenerator = (WorldGenerator)ScriptUtil.compile( new TNLWorldGeneratorCompiler(), new File(scriptFile) );
				} catch( ScriptError e ) {
					System.err.println(ParseUtil.formatScriptError(e));
					System.exit(1);
					return;
				} catch( FileNotFoundException e ) {
					System.err.println(e.getMessage());
					System.exit(1);
					return;
				} catch( IOException e ) {
					throw new RuntimeException(e);
				}
			} else {
				worldGenerator = SimpleWorldGenerator.DEFAULT;
			}
			
			ChunkGenerator cg = new ChunkGenerator(worldGenerator.getChunkMunger());
			ChunkWriter cw = new ChunkWriter(worldDir);
			for( int z=0; z<boundsDepth; ++z ) {
				for( int x=0; x<boundsWidth; ++x ) {
					ChunkData cd = cg.generateChunk(boundsX+x,boundsZ+z);
					cw.saveChunk(cd);
				}
			}
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
}
