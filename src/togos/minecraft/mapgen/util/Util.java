package togos.minecraft.mapgen.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import togos.mf.base.SimpleByteChunk;
import togos.mf.value.ByteChunk;
import togos.mf.value.Chunk;

public class Util
{
	public static final String string( byte[] b, int offset, int length ) {
		try {
			return new String( b, offset, length, "UTF-8" );
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public static final byte[] bytes( String s ) {
		try {
			return s.getBytes("UTF-8");
		} catch( UnsupportedEncodingException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public static final String string( Object o ) {
		if( o instanceof String ) {
			return (String)o;
		} else if( o instanceof byte[] ) {
			return string( (byte[])o, 0, ((byte[])o).length );
		} else if( o instanceof ByteChunk ) {
			ByteChunk c = (ByteChunk)o;
			return string( c.getBuffer(), c.getOffset(), c.getSize() );
		} else if( o instanceof Chunk ) {
			Chunk c = (Chunk)o;
			return string( c.data, c.offset, c.length );
		} else if( o == null ) {
			return null;
		} else {
			throw new RuntimeException("Don't know how to turn "+o.getClass()+" into a String");
		}
	}
	
	public static final ByteChunk byteBuffer( Object o ) {
		if( o == null ) {
			return null;
		} else if( o instanceof ByteChunk ) {
			return (ByteChunk)o;
		} else if( o instanceof byte[] ) {
			byte[] b = (byte[])o;
			return new SimpleByteChunk( b, 0, b.length );
		} else if( o instanceof Chunk ) {
			Chunk c = (Chunk)o;
			return new SimpleByteChunk( c.data, c.offset, c.length );
		} else if( o instanceof String ) {
			byte[] b = bytes((String)o);
			return new SimpleByteChunk( b, 0, b.length );
		} else {
			throw new RuntimeException("Don't know how to turn "+o.getClass()+" into a ByteBuffer");
		}
	}
	
	public static final void write( ByteChunk bb, OutputStream os ) throws IOException {
		os.write( bb.getBuffer(), bb.getOffset(), bb.getSize() );
	}
	
	public static final byte[] readFile( File f ) throws IOException {
		long length = f.length();
		if( length > 1<<24 ) { // Arbitrary limit
			throw new RuntimeException("File "+f+" is too big to load into memory");
		}
		FileInputStream fis = new FileInputStream(f);
		byte[] buffer = new byte[(int)length];
		for( int read=0; read<length; ) {
			int rr = fis.read(buffer,read,(int)length-read);
			if( rr < 1 ) {
				throw new RuntimeException("File "+f+" was shortened during reading");
			}
			read += rr;
		}
		return buffer;
	}
	
	public static final Script readScript( File f ) throws IOException {
		return new Script(readFile(f), f.getPath());
	}
}
