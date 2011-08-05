package togos.minecraft.mapgen.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

import togos.minecraft.mapgen.io.ChunkWriter;
import togos.minecraft.mapgen.io.RegionFile;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class ChunkGenerationJob implements Runnable
{
	public String scriptUri;
	public ChunkMunger generator; 
	public long px, py, pz;
	public int width, height, depth;
	public ChunkDataListener onComplete;
	
	public ChunkGenerationJob( String scriptUri, ChunkMunger generator,
		long px, long py, long pz, int w, int h, int d,
		ChunkDataListener onComplete
	) {
		this.scriptUri = scriptUri;
		this.generator = generator;
		this.px = px; this.py = py; this.pz = pz;
		this.width = w; this.height = h; this.depth = d;
		this.onComplete = onComplete;
	}
	
	public void run() {
		ChunkData cd = new ChunkData(px,py,pz,width,height,depth);
		generator.mungeChunk(cd);
		ByteArrayOutputStream dataGoesHere = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(new DeflaterOutputStream(dataGoesHere));
		try {
			ChunkWriter.writeChunk( cd, dos );
			dos.close();
		} catch( IOException e ) {
			System.err.println("Error while serializing chunk!");
			e.printStackTrace();
		}
		onComplete.setChunkData( scriptUri, px, py, pz, width, height, depth,
			dataGoesHere.toByteArray(), RegionFile.VERSION_DEFLATE );
	}
}
