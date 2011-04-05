package togos.minecraft.mapgen.mf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import togos.mf.api.Request;
import togos.mf.api.Response;
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

	public void writeRequest( Request req, long id ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		SimpleMFPacket.encodeRequest( req, id, baos );
		byte[] data = baos.toByteArray();
		pw.writePacket(data, data.length);
	}
	
	public void writeResponse( Response res, long id ) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		SimpleMFPacket.encodeResponse( res, id, baos );
		byte[] data = baos.toByteArray();
		pw.writePacket(data, data.length);
	}
}
