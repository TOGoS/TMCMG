package togos.minecraft.mapgen.mf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import togos.mf.api.Message;
import togos.mf.io.MessageReader;
import togos.mf.io.MessageWriter;
import togos.mf.io.PacketReader;
import togos.mf.io.PacketWriter;

public class SimpleMFStream implements MessageReader, MessageWriter
{
	protected PacketReader pr;
	protected PacketWriter pw;
		
	public SimpleMFStream( PacketReader pr, PacketWriter pw ) {
		this.pr = pr;
		this.pw = pw;
	}
	
	/*
	public void writeRequest( Request req, long id ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		SimpleMFMessage.encodeRequest( req, id, baos );
		byte[] data = baos.toByteArray();
		pw.writePacket(data, data.length);
	}
	
	public void writeResponse( Response res, long id ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		SimpleMFMessage.encodeResponse( res, id, baos );
		byte[] data = baos.toByteArray();
		pw.writePacket(data, data.length);
	}
	*/
	
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
