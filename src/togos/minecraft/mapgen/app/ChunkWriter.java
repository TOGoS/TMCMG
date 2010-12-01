package togos.minecraft.mapgen.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import org.jnbt.CompoundTag;
import org.jnbt.NBTOutputStream;

import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.minecraft.mapgen.world.gen.ChunkMunger;

public class ChunkWriter
{
	public int tmod( int num, int modby ) {
		if( num < 0 ) {
			num = -num;
			num %= modby;
			num = modby - num;
			return num;
		} else {
			return num % modby;
		}
	}
	
	public String chunkPath( int x, int z ) {
		return Integer.toString( tmod(x,64), 36 ) + "/" +
			Integer.toString( tmod(z,64), 36 ) + "/" +
			"c." + Integer.toString(x,36) + "." + Integer.toString(z,36) + ".dat";
	}
	
	public void writeChunkToFile( ChunkData cd, String baseDir ) throws IOException {
		String fullPath = baseDir + "/" + chunkPath( cd.getChunkX(), cd.getChunkZ() );
		File f = new File(fullPath);
		File dir = f.getParentFile();
		if( dir != null && !dir.exists() ) dir.mkdirs();
		FileOutputStream os = new FileOutputStream(f);
		try {
			NBTOutputStream nbtos = new NBTOutputStream(os);
			
			HashMap levelRootTags = new HashMap();
			levelRootTags.put("Level",cd.toTag());
			CompoundTag fileRootTag = new CompoundTag("",levelRootTags);
			
			nbtos.writeTag(fileRootTag);
			nbtos.close();
		} finally {
			os.close();
		}
	}
	
	protected String chunkBaseDir;
	public ChunkWriter( String baseDir ) {
		this.chunkBaseDir = baseDir;
	}
	
	public void writeChunk( int cx, int cz, ChunkMunger cm ) throws IOException {
		ChunkData cd = new ChunkData(cx,cz);
		cm.mungeChunk(cd);
		writeChunkToFile(cd, chunkBaseDir);
	}
}
