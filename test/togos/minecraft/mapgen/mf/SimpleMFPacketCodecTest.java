package togos.minecraft.mapgen.mf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.mf.io.PacketReader;
import togos.mf.io.slip.SLIP;
import togos.mf.io.slip.SLIP.PacketWriter;
import togos.minecraft.mapgen.data.Chunk;

public class SimpleMFPacketCodecTest extends TestCase
{
	public void testPacketEquals() {
		SimpleMFMessage p1 = new SimpleMFMessage(123, "");
		SimpleMFMessage p2 = new SimpleMFMessage(123, "");
		SimpleMFMessage p3 = new SimpleMFMessage(123, "asd");
		assertTrue( p1.equals(p2) );
		assertFalse( p1.equals(p3) );
	}
	
	public void testRequestMessageEquals() {
		SimpleMFMessage p1 = new SimpleMFMessage(123, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		SimpleMFMessage p2 = new SimpleMFMessage(123, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		SimpleMFMessage p3 = new SimpleMFMessage(123, new BaseRequest("GET", "/bar", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		SimpleMFMessage p4 = new SimpleMFMessage(124, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		assertTrue( p1.equals(p2) );
		assertFalse( p1.equals(p3) );
		assertFalse( p1.equals(p4) );

	}
	
	public void testMessageCodec() throws IOException {
		SimpleMFMessage p1 = new SimpleMFMessage(123, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SimpleMFMessage.encode(p1, baos);
		byte[] encoded = baos.toByteArray();
		
		SimpleMFMessage p2 = SimpleMFMessage.decode(encoded, 0, encoded.length);
		
		assertTrue( p1.equals(p2) );
	}
	
	public void testMessageSlipStream() throws IOException {
		ArrayList messages = new ArrayList();
		messages.add( new SimpleMFMessage(123, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP) ));
		messages.add( new SimpleMFMessage(124, new BaseRequest("POST", "/gaaa", new Chunk(new byte[]{1,2,3,4,5},0,5), Collections.EMPTY_MAP ) ));
		messages.add( new SimpleMFMessage(100, new BaseResponse(200, Chunk.EMPTY) ));
		messages.add( new SimpleMFMessage(101, new BaseResponse(500, Chunk.EMPTY) ));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PacketWriter pw = new SLIP.PacketWriter(baos);
		SimpleMFStream smfs = new SimpleMFStream(null, pw);
		
		for( int i=0; i<messages.size(); ++i ) {
			smfs.writeMessage((SimpleMFMessage)messages.get(i));
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
		PacketReader pr = new SLIP.PacketReader(bais);
		SimpleMFStream smis = new SimpleMFStream(pr, null);
		for( int i=0; i<messages.size(); ++i ) {
			SimpleMFMessage readMessage = smis.readMessage();
			assertEquals( readMessage, (SimpleMFMessage)messages.get(i) );
		}
	}
}
