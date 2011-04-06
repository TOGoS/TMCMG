package togos.mf.io.slip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SLIP {
	public static final byte END = (byte)192;
	public static final byte ESC = (byte)219;
	public static final byte ESC_END = (byte)220;
	public static final byte ESC_ESC = (byte)221;
	
	//// Write
	
	public static void openPacket( OutputStream os, boolean doubleEnd )
		throws IOException
	{
		if( doubleEnd ) {
			os.write(END);
		}
	}
	
	public static void writePacketBytes( OutputStream os, byte[] bytes, int begin, int length )
		throws IOException
	{
		int ues = begin;
		int uee;
		for( int i=0; i<length; ++i ) {
			if( bytes[i] == END ) {
				uee = i;
				os.write(bytes, ues, uee-ues);
				os.write(ESC);
				os.write(ESC_END);
				ues = i+1;
			} else if( bytes[i] == ESC ) {
				uee = i;
				os.write(bytes, ues, uee-ues);
				os.write(ESC);
				os.write(ESC_ESC);
				ues = i+1;
			}
		}
		uee = length;
		os.write(bytes, ues, uee-ues);
	}
	
	public static void closePacket( OutputStream os )
		throws IOException
	{
		os.write(END);
	}
	
	public static void writePacket( OutputStream os, byte[] data, int begin, int length, boolean doubleEnd )
		throws IOException
	{
		openPacket(os, doubleEnd);
		writePacketBytes(os, data, begin, length);
		closePacket(os);
	}
	
	public static class PacketWriter implements togos.mf.io.PacketWriter {
		OutputStream os;
		
		public PacketWriter( OutputStream os ) {
			this.os = os;
		}
		
		public void writePacket( byte[] data, int length ) throws IOException {
		    SLIP.writePacket( os, data, 0, length, true );
		}
	}
	
	//// Read
	
	public static class PacketReader implements togos.mf.io.PacketReader {
		InputStream is;
		protected int nextByte;
		protected boolean useNextByte = false;
		
		protected int read() throws IOException {
			if( useNextByte ) {
				useNextByte = false;
				return nextByte;
			}
			return is.read();
		}
		protected void unread(int b) {
			nextByte = b;
			useNextByte = true;
		}
		protected int current() throws IOException {
			if( useNextByte ) return nextByte;
			useNextByte = true;
			return nextByte = is.read();
		}
		
		public PacketReader( InputStream is ) {
			this.is = is;			
		}
		public int readPacketBytes( byte[] buf, int offset, int maxLength )
			throws IOException
		{
			int b = read();
			int i;
			for( i=0; b != -1 && (byte)b != END && i < maxLength; ++i ) {
				if( (byte)b == ESC ) {
					b = read();
					if( (byte)b == ESC_END ) b = END;
					else if( (byte)b == ESC_ESC ) b = ESC;
				}
				buf[i] = (byte)b;
				b = read();
			}
			unread(b);
			return i;
		}
		public boolean eos() throws IOException {
			return current() == -1;
		}
		public void skipToNextPacket() throws IOException {
			int b;
			while( (byte)(b = read()) == END );
			unread(b);
		}
		public void close() throws IOException {
			is.close();
		}
		public byte[] readPacket( int maxLength ) throws IOException {
			skipToNextPacket();
			byte[] buf = new byte[maxLength];
			int length = readPacketBytes(buf, 0, maxLength);
			if( length == maxLength ) {
				return buf;
			} else {
				byte[] ret = new byte[length];
				for( int i=0; i<length; ++i ) {
					ret[i] = buf[i];
				}
				return ret;
			}
		}
	}
}
