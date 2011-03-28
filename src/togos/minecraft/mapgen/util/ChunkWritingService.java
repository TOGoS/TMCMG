package togos.minecraft.mapgen.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import togos.minecraft.mapgen.io.ChunkWriter;
import togos.minecraft.mapgen.world.gen.ChunkMunger;

public class ChunkWritingService extends ChunkWriter implements Runnable, Service
{
	public interface ChunkWritingProgressListener {
		public void chunkProgressUpdated( int chunksWritten, int totalChunks );
	}
	
	// By design, if the service is started without being initialized,
	// it won't do anything, since bounds are all zero:
	int bx=0, bz=0, bw=0, bd=0;
	ChunkMunger chunkMunger;
	protected volatile boolean go = false;
	protected HashSet progressListeners = new HashSet();
	
	public ChunkWritingService() {
		super("junk-chunks");
	}
	
	public void addProgressListener( ChunkWritingProgressListener l ) {
		progressListeners.add(l);
	}
	
	public void setChunkDir( String chunkBaseDir ) {
		this.chunkBaseDir = chunkBaseDir;
	}
	
	public void setBounds( int bx, int bz, int bw, int bd ) {
		this.bx = bx;
		this.bz = bz;
		this.bw = bw;
		this.bd = bd;
	}
	
	public void setChunkMunger( ChunkMunger cm ) {
		chunkMunger = cm;
	}
	
	public void writeGrid( int bx, int bz, int bw, int bd, ChunkMunger cm )
		throws IOException
	{
		// This whole thing could be wonderfully multi-threaded if I felt like
		// taking the half hour to write a job scheduler...
		int written = 0;
		int total = bw*bd;
		for( int z=0; z<bd; ++z ) {
			for( int x=0; x<bw; ++x ) {
				if( !go ) return;
				writeChunk( bx+x, bz+z, cm );
				++written;
				for( Iterator li=progressListeners.iterator(); li.hasNext(); ) {
					((ChunkWritingProgressListener)li.next()).chunkProgressUpdated(written, total);
				}
			}
		}
	}
	
	public void run() {
		try {
			writeGrid( bx, bz, bw, bd, chunkMunger );
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
		go = false;
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
