package togos.minecraft.mapgen.mf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import togos.mf.io.PacketReader;
import togos.mf.io.PacketWriter;

public class SimpleMFStream
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
	
	public void writeMessage( SimpleMFMessage mess ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		SimpleMFMessage.encode( mess, baos );
		byte[] data = baos.toByteArray();
		pw.writePacket(data, data.length);
	}
	
	public SimpleMFMessage readMessage() throws IOException {
		byte[] data = this.pr.readPacket(8192);
		return SimpleMFMessage.decode(data, 0, data.length);
	}
}
