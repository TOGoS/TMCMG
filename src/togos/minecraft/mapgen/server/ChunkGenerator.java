package togos.minecraft.mapgen.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.app.ChunkWriter;
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
	public ChunkGenerator( TNLCompiler c, String scriptFile ) {
		this.scriptFile = new File(scriptFile);
		this.compiler = c;
	}
	
	public Object getCompiledScript() {
		try {
			if( lastCompiled == -1 || lastCompiled < scriptFile.lastModified() ) {
				compiled = ScriptUtil.compile(compiler, scriptFile);
				lastCompiled = System.currentTimeMillis();
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
		ChunkData cd = new ChunkData(x,y);
		worldGenerator.getChunkMunger().mungeChunk(cd);
		return cd;
	}
	
	public byte[] getSerializedChunkData( int x, int y ) {
		ChunkWriter cw = new ChunkWriter();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
	        cw.writeChunk( getChunkData(x,y), baos );
        } catch( IOException e ) {
        	throw new RuntimeException(e);
        }
		return baos.toByteArray();
	}
	
	Pattern CHUNKPAT = Pattern.compile("^(.*/)?c\\.(\\-?[0-9a-z]+)\\.(\\-?[0-9a-z]+)\\.dat");
	
	public Response call( Request req ) {
		String resName = req.getResourceName();
		Matcher cpm = CHUNKPAT.matcher(resName);
		if( !cpm.matches() ) return BaseResponse.RESPONSE_UNHANDLED;
		
		String x36 = cpm.group(2);
		String y36 = cpm.group(3);
		int x = Integer.parseInt(x36, 36);
		int y = Integer.parseInt(y36, 36);
		return new BaseResponse(ResponseCodes.RESPONSE_NORMAL,
				getSerializedChunkData(x, y), "application/octet-stream");
	}
}
