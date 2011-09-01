package togos.minecraft.mapgen.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import togos.mf.base.ListByteBlob;
import togos.mf.base.SimpleByteChunk;
import togos.mf.value.ByteBlob;
import togos.minecraft.mapgen.util.SubRegion;
import togos.minecraft.mapgen.util.Util;

public class SubRegionIO
{
	protected static final int HEADER_SIZE = 32;
	protected static final int INDEX_ENTRY_SIZE = 16;
		
	protected static void encodeHeader( SubRegion sr, byte[] header ) {
		header[0] = 'T'; // TOGoS
		header[1] = 'M'; // Minecraft
		header[2] = 'S'; // Sub
		header[3] = 'R'; // Region
		header[4] = sr.chunkFormat;
		Util.encodeInt48( sr.x, header,  5 );
		Util.encodeInt48( sr.y, header, 11 );
		Util.encodeInt48( sr.z, header, 17 );
		Util.encodeInt16( sr.chunksWide, header, 23 );
		Util.encodeInt16( sr.chunksHigh, header, 25 );
		Util.encodeInt16( sr.chunksDeep, header, 27 );
		header[29] = (byte)sr.chunkWidth;
		header[30] = (byte)sr.chunkHeight;
		header[31] = (byte)sr.chunkDepth;
	}
	
	public static ByteBlob toByteBlob( SubRegion sr ) {
		List chunks = new ArrayList(sr.chunkData.length+1);
		
		byte[] head = new byte[HEADER_SIZE + INDEX_ENTRY_SIZE*sr.chunkData.length];
		encodeHeader( sr, head );
		chunks.add( new SimpleByteChunk(head) );
		int size = head.length;
		for( int i=0; i<sr.chunkData.length; ++i ) {
			Util.encodeInt32( sr.chunkData[i].getSize(), head, HEADER_SIZE+i*INDEX_ENTRY_SIZE+0 );
			Util.encodeInt32( sr.chunkMtime[i],          head, HEADER_SIZE+i*INDEX_ENTRY_SIZE+4 );
			chunks.add( sr.chunkData[i] );
			size += sr.chunkData[i].getSize();
		}
		
		return new ListByteBlob(chunks, size);
	}
	
	public static void wrtie( SubRegion sr, OutputStream os ) throws IOException {
		Util.write( toByteBlob(sr), os );
	}
	
	protected static short decodePositiveInt8( byte b ) {
		short v = (short)(b&0xFF);
		return v == 0 ? 256 : v;
	}
	
	public static SubRegion read( InputStream is ) throws IOException {
		// Read header
		byte[] header = new byte[HEADER_SIZE];
		Util.readFully( is, header, 0, HEADER_SIZE );
		
		long x = Util.decodeInt48(header, 5);
		long y = Util.decodeInt48(header,11);
		long z = Util.decodeInt48(header,17);
		short chunksWide = Util.decodeInt16( header, 23 );
		short chunksHigh = Util.decodeInt16( header, 25 );
		short chunksDeep = Util.decodeInt16( header, 27 );
		short chunkWidth  = decodePositiveInt8( header[29] );
		short chunkHeight = decodePositiveInt8( header[30] );
		short chunkDepth  = decodePositiveInt8( header[31] );
		
		SubRegion sr = new SubRegion( x, y, z, chunksWide, chunksHigh, chunksDeep, chunkWidth, chunkHeight, chunkDepth );
		
		// Read index
		byte[] index = new byte[sr.chunkData.length * INDEX_ENTRY_SIZE];
		Util.readFully( is, index, 0, sr.chunkData.length * INDEX_ENTRY_SIZE );
		
		// Read chunk data
		for( int i=0; i<sr.chunkData.length; ++i ) {
			int chunkDataLength = Util.decodeInt32(index, i*INDEX_ENTRY_SIZE);
			sr.chunkMtime[i] = Util.decodeInt32(index, i*INDEX_ENTRY_SIZE + 4);
			sr.chunkData[i] = new SimpleByteChunk(Util.readFully(is, chunkDataLength));
		}
		
		return sr;
	}
}
