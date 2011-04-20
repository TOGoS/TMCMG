package togos.minecraft.mapgen.mf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import togos.mf.api.Message;
import togos.mf.api.MessageTypes;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.base.BaseMessage;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.data.Chunk;
import togos.minecraft.mapgen.util.ByteUtil;

public class SimpleMessageCodec
{
	public static void encodeContent( Object c, OutputStream os ) throws IOException {
		if( c instanceof byte[] ) {
			os.write( ((byte[])c) );
		} else if( c instanceof Chunk ) {
			Chunk ch = (Chunk)c;
			os.write( ch.data, ch.offset, ch.length );
		} else {
			throw new IOException("Don't know how to encode "+c.getClass());
		}
	}
	
	public static void encodeRequest( Request req, long id, OutputStream os ) throws IOException {
		os.write( ByteUtil.bytes("REQUEST "+id+" SimpleMF/1.0\n") );
		os.write( ByteUtil.bytes(req.getVerb() + " " + req.getResourceName()+"\n") );
		os.write( ByteUtil.bytes("\n") );
		Object c = req.getContent();
		if( c != null ) encodeContent( c, os );
	}
	
	public static void encodeResponse( Response res, long id, OutputStream os ) throws IOException {
		os.write( ByteUtil.bytes("RESPONSE "+id+" SimpleMF/1.0\n") );
		os.write( ByteUtil.bytes(res.getStatus()+"\n") );
		os.write( ByteUtil.bytes("\n") );
		Object c = res.getContent();
		if( c != null ) encodeContent( c, os );
	}
	
	public static void encode( Message mess, OutputStream os ) throws IOException {
		switch( mess.getMessageType() ) {
		case( MessageTypes.REQUEST ): case( MessageTypes.REQUEST_NR ):
			encodeRequest( (Request)mess.getPayload(), mess.getSessionId(), os );
			break;
		case( MessageTypes.RESPONSE ):
			encodeResponse( (Response)mess.getPayload(), mess.getSessionId(), os );
			break;
		default:
			throw new RuntimeException("Don't know how to encode message type: "+mess.getMessageType());
		}
	}
	
	protected static int findHeaderLength( byte[] buf, int begin, int length ) {
		int end = begin+length;
		for( int i=begin; i<end; ++i ) {
			if( buf[i] == '\n' && (i+1 == end || buf[i+1] == '\n') ) {
				return i-begin;
			}
		}
		return length;
	}
	
	protected static Request decodeRequest( String[] headerLines, Chunk content ) {
		String[] requestInfo = headerLines[1].split(" ");
		return new BaseRequest(requestInfo[0], requestInfo[1], content, Collections.EMPTY_MAP);
	}
	
	protected static Response decodeResponse( String[] headerLines, Chunk content ) {
		String[] responseInfo = headerLines[1].split(" ");
		return new BaseResponse(Integer.parseInt(responseInfo[0]), content);
	}
	
	public static Message decode( byte[] buf, int begin, int length ) throws IOException {
		int headerLength = findHeaderLength(buf, begin, length);
		String header = ByteUtil.string( buf, begin, headerLength );
		String[] headerLines = header.split("\n");
		
		if( headerLines.length < 2 ) {
			throw new IOException("Message had < 2 header lines:\n"+header);
		}
		
		String[] messageInfo = headerLines[0].split(" ");
		long id = Long.parseLong(messageInfo[1]);
		Chunk content = Chunk.copyOf(buf, begin+headerLength+2, length-headerLength-2);
		if( "REQUEST".equals(messageInfo[0]) ) {
			return new BaseMessage(
				id == 0 ? MessageTypes.REQUEST_NR : MessageTypes.REQUEST,
				id, decodeRequest( headerLines, content )
			);
		} else if( "RESPONSE".equals(messageInfo[0]) ) {
			return new BaseMessage(
				MessageTypes.RESPONSE,
				id, decodeResponse( headerLines, content )
			);
		} else {
			throw new IOException( "Unrecognised message type");
		}
	}
}
