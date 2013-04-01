package togos.minecraft.mapgen.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.BlockingQueue;

import togos.minecraft.mapgen.io.ChunkWriter;
import togos.minecraft.mapgen.world.gen.ChunkGenerator;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.service.Service;

public class ChunkWritingService extends ChunkWriter implements Runnable, Service
{
	public interface ChunkWritingProgressListener {
		public void chunkProgressUpdated( int chunksWritten, int totalChunks );
	}
	
	// By design, if the service is started without being initialized,
	// it won't do anything, since bounds are all zero:
	int bx=0, bz=0, bw=0, bd=0;
	protected volatile boolean go = false;
	protected HashSet<ChunkWritingProgressListener> progressListeners = new HashSet<ChunkWritingProgressListener>();
	protected Script script;
	
	protected BlockingQueue<Runnable> jobQueue;
	
	public ChunkWritingService() {
		super("junk-chunks");
	}
	
	public void addProgressListener( ChunkWritingProgressListener l ) {
		progressListeners.add(l);
	}
	
	public void setChunkDir( String chunkBaseDir ) {
		this.worldDir = chunkBaseDir;
	}
	
	public void setBounds( int bx, int bz, int bw, int bd ) {
		this.bx = bx;
		this.bz = bz;
		this.bw = bw;
		this.bd = bd;
	}
	
	public void setScript( Script s ) {
		this.script = s;
	}
	
	protected int written, total;
	
	protected synchronized void chunkWritten() {
		++written;
		for( ChunkWritingProgressListener pi : progressListeners ) {
			pi.chunkProgressUpdated(written, total);
		}
	}
	
	public void writeGrid( int bx, int bz, int bw, int bd, Script script )
		throws IOException
	{
		ChunkMunger cm = ((WorldGenerator)script.program).getChunkMunger();
		ChunkGenerator cg = new ChunkGenerator( cm );
		
		written = 0;
		total = bw*bd;
		
		for( int z=0; z<bd; ++z ) {
			for( int x=0; x<bw; ++x ) {
				if( !go ) return;
				
				final int cx = bx+x, cz = bz+z;
				
				saveChunk( cg.generateChunk(cx,cz) );
				chunkWritten();
			}
		}
	}
	
	public void run() {
		try {
			if( script == null ) return;
			writeGrid( bx, bz, bw, bd, script );
		} catch( IOException e ) {
			throw new RuntimeException(e);
		} finally {
			go = false;
		}
	}
	
	public void start() {
		if( !go ) {
			Thread t = new Thread(this, "Chunk Writer");
			go = true;
			t.start();
		}
	}
	
	public void halt() {
		go = false;
	}
	
	public boolean isRunning() {
		return go;
	}
}
