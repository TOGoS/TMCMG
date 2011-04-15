package togos.minecraft.mapgen.util;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

import togos.jobkernel.mf.Active;
import togos.mf.api.AsyncCallable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.api.ResponseHandler;
import togos.mf.value.URIRef;
import togos.minecraft.mapgen.io.ChunkWriter;
import togos.minecraft.mapgen.io.RegionFile;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.af.GenerateTNLChunk;
import togos.minecraft.mapgen.world.gen.af.SerializeChunk;

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
	protected AsyncCallable callable;
	
	public ChunkWritingService( AsyncCallable callable ) {
		super("junk-chunks");
		this.callable = callable;
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
			System.err.println("Clear region file cache");
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
		// This whole thing could be wonderfully multi-threaded if I felt like
		// taking the half hour to write a job scheduler...
		written = 0;
		total = bw*bd;
		for( int z=0; z<bd; ++z ) {
			for( int x=0; x<bw; ++x ) {
				if( !go ) return;
				
				final int cx = bx+x, cz = bz+z;

				/*
				writeChunk( cx, cz, cm );
				chunkWritten();
				*/
				
				final Request req = Active.mkRequest(
					SerializeChunk.makeRef(
						GenerateTNLChunk.makeRef(scriptRef, cx*16, 0, cz*16, 16, 128, 16 ) ) );
				callable.callAsync(req, new ResponseHandler() {
					public void setResponse( Response res ) {
						if( res.getStatus() != ResponseCodes.NORMAL ) {
							System.err.println("Failed to fetch chunk "+cx+","+cz+" from "+req.getResourceName());
						} else {
							try {
								writeChunk( cx, cz, (byte[])res.getContent(), RegionFile.VERSION_DEFLATE );
								chunkWritten();
							} catch( IOException e ) {
								throw new RuntimeException(e);
							}
						}
					}
				});
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
