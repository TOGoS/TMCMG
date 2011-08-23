package togos.minecraft.mapgen.io;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.DeflaterOutputStream;

import togos.minecraft.mapgen.util.ByteBlob;
import togos.minecraft.mapgen.util.ByteChunk;
import togos.minecraft.mapgen.util.ListByteBufferList;
import togos.minecraft.mapgen.util.SimpleByteBuffer;
import togos.minecraft.mapgen.world.gen.ChunkGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;

/**
 * Generates an entire region file in one go.
 */
public class RegionWriter
{
	protected static final int SECTOR_SIZE = 4096;
	protected static final int CHUNKS_PER_REGION_SIDE = 32;
	protected static final int CHUNKS_PER_REGION = CHUNKS_PER_REGION_SIDE*CHUNKS_PER_REGION_SIDE;
	protected static byte FORMAT = RegionFile.VERSION_DEFLATE;
	
	protected static final int PAD_SIZE = 1024;
	protected static final byte[] PAD_BYTES = new byte[PAD_SIZE];
	protected static final SimpleByteBuffer PAD_BUFFER = new SimpleByteBuffer(PAD_BYTES, 0, PAD_SIZE);
	
	protected static final void addEmptyBuffers( List bufList, long length ) {
		if( length < 0 ) {
			throw new RuntimeException("Can't create buffers with negative length: "+length);
		}
		while( length > 1024 ) {
			bufList.add(PAD_BUFFER);
			length -= 1024;
		}
		if( length != 0 ) {
			bufList.add(new SimpleByteBuffer(PAD_BYTES,0,(int)length));
		}
	}
	
	protected static final long add( List bufList, long offset, ByteChunk buf ) {
		bufList.add(buf);
		return offset + buf.getSize();
	}
	
	protected static final long padSector( List bufList, long offset ) {
		int padding = (4096 - (int)(offset % SECTOR_SIZE)) % 4096;
		addEmptyBuffers(bufList,padding);
		return offset+padding;
	}
	
	protected static final void encodeInt( byte[] dest, int offset, int i ) {
		dest[offset+0] = (byte)(i>>24);
		dest[offset+1] = (byte)(i>>16);
		dest[offset+2] = (byte)(i>> 8);
		dest[offset+3] = (byte)(i>> 0);
	}
	
	protected static final ByteChunk encodeChunkHeader( int size, byte format ) {
		byte[] data = new byte[5];
		encodeInt( data, 0, size );
		data[4] = format;
		return new SimpleByteBuffer(data);
	}
	
	protected static final int chunkOffsetCode( long offset, int length ) {
		if( offset % SECTOR_SIZE != 0 ) {
			throw new RuntimeException("Can't create chunk offset code for non-sector-aligned location: "+offset);
		}
		int sectorNumber = (int)(offset / SECTOR_SIZE);
		int sectorsNeeded = (length / SECTOR_SIZE) + (length % SECTOR_SIZE) == 0 ? 0 : 1;
		return (sectorNumber << 8) | sectorsNeeded;
	}
	
	protected static final long addChunk( int[] offsetCodes, int idx, List bufList, long offset, ByteChunk chunkData, byte format ) {
		ByteChunk header = encodeChunkHeader(chunkData.getSize(),format);
		offset = padSector( bufList, offset );
		offsetCodes[idx] = chunkOffsetCode( offset, header.getSize()+chunkData.getSize() );
		offset = add( bufList, offset, header );
		offset = add( bufList, offset, chunkData );
		offset = padSector( bufList, offset );
		return offset;
	}
	
	protected static ByteChunk encodeInts( int[] values ) {
		byte[] buf = new byte[values.length*4];
		for( int i=values.length-1; i>=0; --i ) {
			encodeInt(buf,i*4,values[i]);
		}
		return new SimpleByteBuffer(buf);
	}
	
	public ByteBlob createRegionData( int[] timestamps, byte[][] chunkData ) {
		List bufList = new ArrayList(CHUNKS_PER_REGION*3+2);
		
		bufList.add(null);
		bufList.add(null);
		long offset = SECTOR_SIZE*2;
		
		int[] offsetCodes = new int[CHUNKS_PER_REGION];
		
		for( int i=0; i<CHUNKS_PER_REGION; ++i ) {
			offset = addChunk( offsetCodes, i, bufList, offset, new SimpleByteBuffer(chunkData[i]), FORMAT );
		}
		bufList.set(0, encodeInts(offsetCodes));
		bufList.set(1, encodeInts(timestamps));
		
		return new ListByteBufferList(bufList, offset);
	}
	
	/**
	 * @param cm chunk munger to use to generate chunks
	 * @param rx x-index (1 increment = 512 meters) or region
	 * @param rz z-index (1 increment = 512 meters) or region
	 * @param timestamp unix timestamp (in seconds)
	 * @return
	 */
	public ByteBlob generateRegion( ChunkGenerator cg, int rx, int rz, int timestamp ) {
		byte[][] chunkData = new byte[CHUNKS_PER_REGION][];
		int[] timestamps = new int[CHUNKS_PER_REGION];
		
		for( int i=0, rcz=0; rcz<CHUNKS_PER_REGION_SIDE; ++rcz ) {
			int cz = CHUNKS_PER_REGION_SIDE*rz+rcz;
			for( int rcx=0; rcx<CHUNKS_PER_REGION_SIDE; ++rcx, ++i ) {
				int cx = CHUNKS_PER_REGION_SIDE*rx+rcx;
				
				ChunkData cd = cg.generateChunk(cx,cz);
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(new DeflaterOutputStream(baos));
				try {
					ChunkWriter.writeChunk( cd, dos );
					dos.close();
					baos.close();
				} catch( IOException e ) {
					throw new RuntimeException(e);
				}
				chunkData[i] = baos.toByteArray();
				timestamps[i] = timestamp;
			}
		}
		return createRegionData( timestamps, chunkData );
	}
}
