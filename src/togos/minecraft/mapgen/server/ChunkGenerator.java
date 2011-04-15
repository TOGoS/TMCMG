package togos.minecraft.mapgen.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.PathUtil;
import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.io.ChunkWriter;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise2.lang.ScriptError;
import togos.noise2.lang.TNLCompiler;

public class ChunkGenerator
{
	File scriptFile;
	long lastCompiled = -1;
	TNLCompiler compiler;
	Object compiled;
	int chunkWidth, chunkHeight, chunkDepth;
	public ChunkGenerator( TNLCompiler c, String scriptFile, int chunkWidth, int chunkHeight, int chunkDepth ) {
		this.scriptFile = new File(scriptFile);
		this.compiler = c;
		this.chunkWidth  = chunkWidth;
		this.chunkHeight = chunkHeight;
		this.chunkDepth  = chunkDepth;
	}
	
	protected boolean shouldRecompile() {
		return lastCompiled == -1 || lastCompiled < scriptFile.lastModified();
	}
	
	public Object getCompiledScript() {
		try {
			if( shouldRecompile() ) {
				synchronized( compiler ) {
					if( shouldRecompile() ) {
						compiled = ScriptUtil.compile(compiler, scriptFile);
						lastCompiled = System.currentTimeMillis();
					}
				}
			}
			return compiled;
		} catch( IOException e ) {
			throw new RuntimeException(e);
		} catch( ScriptError e ) {
			System.err.println(ScriptUtil.formatScriptError(e));
			throw new RuntimeException(e);
		}
	}
	
	public ChunkData getChunkData( int x, int y ) {
		WorldGenerator worldGenerator = (WorldGenerator)getCompiledScript();
		ChunkData cd = new ChunkData(x*chunkWidth,0,y*chunkDepth,chunkWidth,chunkHeight,chunkDepth);
		worldGenerator.getChunkMunger().mungeChunk(cd);
		return cd;
	}
	
	public byte[] getSerializedChunkData( int x, int y ) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(baos));
	        ChunkWriter.writeChunk( getChunkData(x,y), dos );
	        dos.close();
        } catch( IOException e ) {
        	throw new RuntimeException(e);
        }
		return baos.toByteArray();
	}
	
	public Response call( Request req ) {
		int[] xy = PathUtil.chunkCoords( req.getResourceName() );
		
		if( xy == null ) return BaseResponse.RESPONSE_UNHANDLED;
		
		return new BaseResponse(
			ResponseCodes.NORMAL,
			getSerializedChunkData(xy[0], xy[1]),
			"application/octet-stream"
		);
	}
}
