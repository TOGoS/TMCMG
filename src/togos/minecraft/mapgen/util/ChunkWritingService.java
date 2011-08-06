package togos.minecraft.mapgen.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;

import togos.jobkernel.Service;
import togos.mf.value.URIRef;
import togos.minecraft.mapgen.io.ChunkWriter;
import togos.minecraft.mapgen.job.ChunkGenerationJob;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class ChunkWritingService extends ChunkWriter implements Runnable, Service
{
	public interface ChunkWritingProgressListener {
		public void chunkProgressUpdated( int chunksWritten, int totalChunks );
	}
	
	// By design, if the service is started without being initialized,
	// it won't do anything, since bounds are all zero:
	int bx=0, bz=0, bw=0, bd=0;
	protected volatile boolean go = false;
	protected HashSet progressListeners = new HashSet();
	protected URIRef scriptRef;
	protected ChunkMunger cm;
	
	public boolean useJobSystem = true;
	protected BlockingQueue jobQueue;
	
	public ChunkWritingService( BlockingQueue jobQueue ) {
		super("junk-chunks");
		this.jobQueue = jobQueue;
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
	
	public void setWorldGenerator( URIRef scriptRef, ChunkMunger cm ) {
		this.scriptRef = scriptRef;
		this.cm = cm;
	}
	
	protected int written, total;
	
	protected synchronized void chunkWritten() {
		++written;
		for( Iterator li=progressListeners.iterator(); li.hasNext(); ) {
			((ChunkWritingProgressListener)li.next()).chunkProgressUpdated(written, total);
		}
		if( written == total ) {
			//System.err.println("Clear region file cache");
			// HEY WATCH OUT THINGS COULD STILL BE RUNNING
			// IF WE STARTED THINGS IN WEIRD ORDER
			// BUT I THINK NEED TO DO THIS TO MAEK SURE
			// THTA ALL WRITES ARE 'FLUSHED'?
			// See yayshots/2011/TMCMG/UnwrittenChunks...png
			// ACKSHULLY NO I THINK THOSE APPEAR BECAUSE MC WAS STILL
			// RUNNING AND HAD CHUNK DATA CACHED.
			//RegionFileCache.clear();
		}
	}

	public void writeGrid( int bx, int bz, int bw, int bd, URIRef scriptRef )
		throws IOException
	{
		written = 0;
		total = bw*bd;
		for( int z=0; z<bd; ++z ) {
			for( int x=0; x<bw; ++x ) {
				if( !go ) return;
				
				final int cx = bx+x, cz = bz+z;

				if( useJobSystem ) {
					try {
						jobQueue.put(new ChunkGenerationJob(
							scriptRef, cm,
							cx*ChunkData.NORMAL_CHUNK_WIDTH,
							0,
							cz*ChunkData.NORMAL_CHUNK_DEPTH,
							ChunkData.NORMAL_CHUNK_WIDTH,
							ChunkData.NORMAL_CHUNK_HEIGHT,
							ChunkData.NORMAL_CHUNK_DEPTH,
							new ChunkDataListener() {
								public void setChunkData(
									String worldId, long px, long py, long pz,
									int w, int h, int d, byte[] data, int format
								) {
									int cx = (int)(px / ChunkData.NORMAL_CHUNK_WIDTH);
									int cz = (int)(pz / ChunkData.NORMAL_CHUNK_DEPTH);
									
									try {
										writeChunk( cx, cz, data, format );
									} catch( IOException e ) {
										System.err.println("Error writing chunk "+cx+","+cz+":");
										e.printStackTrace();
									}
									chunkWritten();
								}
								
							}
						));
					} catch( InterruptedException e ) {
						Thread.currentThread().interrupt();
					}
				} else {
					writeChunk( cx, cz, cm );
					chunkWritten();
				}
			}
		}
	}
	
	public void run() {
		try {
			writeGrid( bx, bz, bw, bd, scriptRef );
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
