package togos.minecraft.mapgen.mf;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.util.ByteUtil;

public class SimpleMFPacket
{
	public static void encodeContent( Object c, OutputStream os ) throws IOException {
		if( c instanceof byte[] ) {
			os.write( ((byte[])c) );
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
	
	protected static int findHeaderLength( byte[] buf, int begin, int length ) {
		int end = begin+length;
		for( int i=begin; i<end; ++i ) {
			if( buf[i] == '\n' && (i+1 == end || buf[i+1] == '\n') ) {
				return i-begin;
			}
		}
		return length;
	}
	
	protected static Request decodeRequest( String[] headerLines, byte[] buf, int contentBegin, int contentLength ) {
		String[] requestInfo = headerLines[1].split(" ");
		return new BaseRequest(requestInfo[0], requestInfo[1], ByteUtil.slice(buf, contentBegin, contentLength), Collections.EMPTY_MAP);
	}
	
	protected static Response decodeResponse( String[] headerLines, byte[] buf, int contentBegin, int contentLength ) {
		String[] responseInfo = headerLines[1].split(" ");
		return new BaseResponse(Integer.parseInt(responseInfo[0]), ByteUtil.slice(buf, contentBegin, contentLength));
	}
	
	public static SimpleMFPacket decode( byte[] buf, int begin, int length ) throws IOException {
		int headerLength = findHeaderLength(buf, begin, length);
		String header = ByteUtil.string( buf, begin, headerLength );
		String[] headerLines = header.split("\n");
		
		if( headerLines.length < 2 ) {
			throw new IOException("Message had < 2 header lines:\n"+header);
		}
		
		String[] messageInfo = headerLines[0].split(" ");
		long id = Long.parseLong(messageInfo[1]);
		int contentBegin, contentLength;
		if( headerLength == length ) {
			contentBegin = 0;
			contentLength = 0; 
		} else {
			contentBegin = begin+headerLength+2;
			contentLength = length-headerLength-2; 
		}
		if( "REQUEST".equals(messageInfo[0]) ) {
			return new SimpleMFPacket( id, decodeRequest( headerLines, buf, contentBegin, contentLength ) );
		} else if( "RESPONSE".equals(messageInfo[0]) ) {
			return new SimpleMFPacket( id, decodeResponse( headerLines, buf, contentBegin, contentLength ) );
		} else {
			throw new IOException( "Unrecognised message type");
		}
	}
	
	long id;
	Object payload;
	
	public SimpleMFPacket( long id, Object payload ) {
		this.id = id;
		this.payload = payload;
	}
}
