package togos.mf.io.sm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import togos.mf.api.Message;
import togos.mf.io.MessageReader;
import togos.mf.io.MessageWriter;
import togos.mf.io.PacketReader;
import togos.mf.io.PacketWriter;

public class SimpleMessageStream implements MessageReader, MessageWriter
{
	protected PacketReader pr;
	protected PacketWriter pw;
		
	public SimpleMessageStream( PacketReader pr, PacketWriter pw ) {
		this.pr = pr;
		this.pw = pw;
	}
	
	public void writeMessage( Message mess ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		SimpleMessageCodec.encode( mess, baos );
		byte[] data = baos.toByteArray();
		pw.writePacket(data, data.length);
	}
	
	public Message readMessage() throws IOException {
		byte[] data = this.pr.readPacket(8192);
		return SimpleMessageCodec.decode(data, 0, data.length);
	}
}
