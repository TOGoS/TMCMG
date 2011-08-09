package togos.minecraft.mapgen.server;

import junit.framework.TestCase;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.base.BaseRequest;
import togos.minecraft.mapgen.http.HTTPClient;
import togos.minecraft.mapgen.uri.Active;
import togos.minecraft.mapgen.uri.BaseRef;
import togos.minecraft.mapgen.util.ReverseBytes;
import togos.minecraft.mapgen.world.gen.af.GenerateTNLChunk;
import togos.minecraft.mapgen.world.gen.af.SerializeChunk;
import togos.noise2.DigestUtil;

public class TMCMGActiveServerTest extends TestCase
{
	Thread serverThread;
	TMCMGActiveWebServer server;
	
	public void setUp() {
		server = new TMCMGActiveWebServer();
		server.start();
	}
	
	public void shutDown() {
		server.halt();
	}
	
	protected String n2l( String urn ) {
		return "http://localhost:"+server.port+"/N2R?"+URIUtil.uriEncode(urn);
	}
	
	public void testSomeSha1Stuff() {
		String hw = "Hello, world!";
		String hwr = "!dlrow ,olleH";
		byte[] hwBytes = hw.getBytes();
		String hwUrn = DigestUtil.getSha1Urn(hwBytes);
		String hwHttpUri = n2l(hwUrn);
		String hwrUrn = Active.build(
			ReverseBytes.FUNCNAME
		).with("operand",new BaseRef(hwUrn)).toRef().getUri();
		String hwrHttpUri = n2l(hwrUrn);
		BaseRequest getRequest = new BaseRequest("GET", hwHttpUri);
		BaseRequest putRequest = new BaseRequest("PUT", hwHttpUri);
		BaseRequest getReverseRequest = new BaseRequest("GET", hwrHttpUri);
		putRequest.content = hwBytes;
		
		String tnlScript = "layered-terrain()";
		byte[] tnlBytes = tnlScript.getBytes();
		String tnlUrn = DigestUtil.getSha1Urn(tnlBytes);
		String tnlUrl = n2l(tnlUrn);
		BaseRequest tnlPutRequest = new BaseRequest("PUT", tnlUrl);
		tnlPutRequest.content = tnlBytes;
		
		String chunkUrn = SerializeChunk.makeRef(
			GenerateTNLChunk.makeRef(
				new BaseRef(tnlUrn),
				0,0,0, 16,128,16
			),
			SerializeChunk.FORMAT_DEFLATE
		).getUri();
		String chunkUrl = n2l(chunkUrn);
		BaseRequest chunkGetRequest = new BaseRequest("GET", chunkUrl);
		
		HTTPClient c = new HTTPClient();
		
		Response dne = c.call(getRequest);
		assertEquals(ResponseCodes.DOES_NOT_EXIST, dne.getStatus());
		
		Response putted = c.call(putRequest);
		assertEquals(ResponseCodes.NORMAL_NO_RESPONSE, putted.getStatus());
		
		Response got = c.call(getRequest);
		assertEquals(ResponseCodes.NORMAL, got.getStatus());
		assertEquals(hw, new String((byte[])got.getContent()));
		
		Response gotReverse = c.call(getReverseRequest);
		assertEquals(ResponseCodes.NORMAL, gotReverse.getStatus());
		assertEquals(hwr, new String((byte[])gotReverse.getContent()));
		
		Response putTnlResponse = c.call(tnlPutRequest);
		assertEquals(ResponseCodes.NORMAL_NO_RESPONSE, putTnlResponse.getStatus());
		
		Response getChunkResponse = c.call(chunkGetRequest);
		assertEquals(ResponseCodes.NORMAL, getChunkResponse.getStatus());
		assertTrue( getChunkResponse.getContent() instanceof byte[] );
		assertTrue( ((byte[])getChunkResponse.getContent()).length > 0 );
	}
}
