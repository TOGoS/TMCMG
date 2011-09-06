package togos.mf.io;

import java.io.IOException;

public interface PacketWriter {
	public void writePacket( byte[] data, int length ) throws IOException;
}
