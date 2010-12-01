package togos.minecraft.mapgen.nbt;

import java.io.IOException;
import java.io.InputStream;

public class NBTReader
{
	InputStream is;
	public NBTReader(InputStream is) {
		this.is = is;
	}
	
	protected byte[] read(int bytes) throws IOException {
		byte[] dat = new byte[bytes];
		int read = 0;
		while( read < bytes ) {
			read += is.read(dat, read, bytes-read);
		}
		return dat;
	}
	
	protected short parseShort(byte[] dat) {
		return (short)((short)dat[0]<<16 | dat[1]);
	}
	
	public short readShort() throws IOException {
		return parseShort(read(2));
	}
	
	public String readString() throws IOException {
		int length = readShort();
		
	}
	
	public Tag readSimpleTag() throws IOException {
		byte type = (byte)is.read();
		String name = readString();
	}
}
