package togos.minecraft.mapgen.mf;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import junit.framework.TestCase;
import togos.mf.api.Message;
import togos.mf.api.MessageTypes;
import togos.mf.base.BaseMessage;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.mf.io.PacketReader;
import togos.mf.io.PacketWriter;
import togos.mf.io.slip.SLIP;
import togos.minecraft.mapgen.data.Chunk;

public class SimpleMessageCodecTest extends TestCase
{
	public void testPacketEquals() {
		Message p1 = new BaseMessage(MessageTypes.REQUEST, 123, "");
		Message p2 = new BaseMessage(MessageTypes.REQUEST, 123, "");
		Message p3 = new BaseMessage(MessageTypes.REQUEST, 123, "asd");
		assertTrue( p1.equals(p2) );
		assertFalse( p1.equals(p3) );
	}
	
	public void testRequestMessageEquals() {
		Message p1 = new BaseMessage(MessageTypes.REQUEST, 123, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		Message p2 = new BaseMessage(MessageTypes.REQUEST, 123, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		Message p3 = new BaseMessage(MessageTypes.REQUEST, 123, new BaseRequest("GET", "/bar", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		Message p4 = new BaseMessage(MessageTypes.REQUEST, 124, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		assertTrue( p1.equals(p2) );
		assertFalse( p1.equals(p3) );
		assertFalse( p1.equals(p4) );

	}
	
	public void testMessageCodec() throws IOException {
		Message p1 = new BaseMessage(MessageTypes.REQUEST, 123, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP ) );
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SimpleMessageCodec.encode(p1, baos);
		byte[] encoded = baos.toByteArray();
		
		Message p2 = SimpleMessageCodec.decode(encoded, 0, encoded.length);
		
		assertTrue( p1.equals(p2) );
	}
	
	public void testMessageSlipStream() throws IOException {
		ArrayList messages = new ArrayList();
		messages.add( new BaseMessage(MessageTypes.REQUEST, 123, new BaseRequest("GET", "/foo", Chunk.EMPTY, Collections.EMPTY_MAP) ));
		messages.add( new BaseMessage(MessageTypes.REQUEST, 124, new BaseRequest("POST", "/gaaa", new Chunk(new byte[]{1,2,3,4,5},0,5), Collections.EMPTY_MAP ) ));
		messages.add( new BaseMessage(MessageTypes.RESPONSE, 100, new BaseResponse(200, Chunk.EMPTY) ));
		messages.add( new BaseMessage(MessageTypes.RESPONSE, 101, new BaseResponse(500, Chunk.EMPTY) ));
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PacketWriter pw = new SLIP.PacketWriter(baos);
		SimpleMFStream smfs = new SimpleMFStream(null, pw);
		
		for( int i=0; i<messages.size(); ++i ) {
			smfs.writeMessage((Message)messages.get(i));
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
		PacketReader pr = new SLIP.PacketReader(bais);
		SimpleMFStream smis = new SimpleMFStream(pr, null);
		for( int i=0; i<messages.size(); ++i ) {
			Message readMessage = smis.readMessage();
			assertEquals( readMessage, (Message)messages.get(i) );
		}
	}
}
