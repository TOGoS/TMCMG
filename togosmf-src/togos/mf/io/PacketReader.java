package togos.mf.io;

import java.io.IOException;

public interface PacketReader {
	public byte[] readPacket( int maxLength ) throws IOException ;
}
