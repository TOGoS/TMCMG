package togos.minecraft.mapgen.data;

import togos.minecraft.mapgen.util.ByteUtil;

public class Chunk
{
	public static Chunk EMPTY = new Chunk(ByteUtil.EMPTY,0,0);
	
	public static final Chunk copyOf( byte[] buffer, int begin, int length ) {
		if( length > 0 ) {
			return new Chunk( ByteUtil.slice(buffer,begin,length), 0, length );
		} else {
			return EMPTY;
		}
	}
	
	public byte[] data;
	public int offset;
	public int length;
	
	public Chunk( byte[] data, int offset, int length ) {
		this.data = data;
		this.offset = offset;
		this.length = length;
	}
	
	public boolean equals( Object oth ) {
		if( oth instanceof Chunk ) {
			Chunk oc = (Chunk)oth;
			if( this.length != oc.length ) return false;
			for( int i=0; i<length; ++i ) {
				if( data[offset+i] != oc.data[oc.offset+i] ) return false;
			}
			return true;
		}
		return false;
	}
	
	public int hashCode() {
		return ByteUtil.hashCode(data, offset, length);
	}
}
