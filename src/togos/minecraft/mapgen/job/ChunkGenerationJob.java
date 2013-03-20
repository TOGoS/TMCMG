package togos.minecraft.mapgen.job;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

import togos.mf.value.ByteChunk;
import togos.minecraft.mapgen.io.BetterByteArrayOutputStream;
import togos.minecraft.mapgen.io.ChunkWriter;
import togos.minecraft.mapgen.io.RegionFile;
import togos.minecraft.mapgen.util.ChunkDataListener;
import togos.minecraft.mapgen.util.Script;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class ChunkGenerationJob implements Runnable
{
	public final Script script;
	public final ChunkMunger generator; 
	public final long px, py, pz;
	public final int width, height, depth;
	public final ChunkDataListener onComplete;
	public final int serializationFormat = RegionFile.VERSION_DEFLATE;
	
	public ChunkGenerationJob( Script script, ChunkMunger generator,
		long px, long py, long pz, int w, int h, int d,
		ChunkDataListener onComplete
	) {
		this.script = script;
		this.generator = generator;
		this.px = px; this.py = py; this.pz = pz;
		this.width = w; this.height = h; this.depth = d;
		this.onComplete = onComplete;
	}
	
	public void setResourceData( ByteChunk data ) {
		onComplete.setChunkData( px, py, pz, width, height, depth,
				data, serializationFormat );
	}
	
	public void run() {
		ChunkData cd = new ChunkData(px,py,pz,width,height,depth);
		generator.mungeChunk(cd);
		BetterByteArrayOutputStream dataGoesHere = new BetterByteArrayOutputStream(1024);
		DataOutputStream dos = new DataOutputStream(new DeflaterOutputStream(dataGoesHere));
		try {
			ChunkWriter.writeChunk( cd, dos );
			dos.close();
		} catch( IOException e ) {
			System.err.println("Error while serializing chunk!");
			e.printStackTrace();
		}
		setResourceData( dataGoesHere );
	}
}
