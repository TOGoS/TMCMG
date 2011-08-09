package togos.minecraft.mapgen.http;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.URL;

import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.base.BaseResponse;

public class HTTPClient implements Callable {
	public Response call(Request req) {
		if( !req.getResourceName().startsWith("http://") && !req.getResourceName().startsWith("https://") ) return BaseResponse.RESPONSE_UNHANDLED;

		URL url;
		try {
			url = new URL(req.getResourceName());
		} catch( MalformedURLException e ) {
			throw new RuntimeException(e);
		}
		try {
			HttpURLConnection urlConn = (HttpURLConnection)url.openConnection();
			urlConn.setRequestMethod(req.getVerb());
			if( req.getContent() instanceof byte[] && (!"GET".equals(req.getVerb()) || !"HEAD".equals(req.getVerb())) ) {
				urlConn.setDoOutput(true);
				OutputStream os = urlConn.getOutputStream();
				os.write( (byte[])req.getContent() );
				os.close();
			}
			urlConn.connect();
			int status = urlConn.getResponseCode();
			long length;
			if( status == 204 ) {
				length = 0;
			} else {
				length = urlConn.getContentLength();
			}
			if( length == -1 ) {
				throw new RuntimeException("Can't handle unknown content length!");
			}
			if( length > 2*1024*1024 ) {
				throw new RuntimeException("Response content too long: "+length);
			}
			byte[] content = new byte[(int)length];
			InputStream is = urlConn.getInputStream();
			for( int read=0; read < length; read += is.read(content,read,(int)length-read) );
			BaseResponse res = new BaseResponse(status, content);
			if( urlConn.getContentType() != null ) {
				res.putContentMetadata("http://purl.org/dc/terms/format", urlConn.getContentType());
			}
			return res;
		} catch( NoRouteToHostException e ) {
			String mess = "No route to host " + url.getHost();
			return new BaseResponse(ResponseCodes.DOES_NOT_EXIST, mess, "text/plain");
		} catch( ConnectException e ) {
			String mess = "Could not connect to " + url.getHost() + ":" + url.getPort();
			return new BaseResponse(ResponseCodes.DOES_NOT_EXIST, mess, "text/plain");
		} catch( FileNotFoundException e ) {
			return new BaseResponse(ResponseCodes.DOES_NOT_EXIST, "File not found: " + req.getResourceName(), "text/plain");
		} catch( IOException e ) {
			e.printStackTrace();  // eh
			String mess = "I/O error reading " + req.getResourceName();
			return new BaseResponse(ResponseCodes.SERVER_ERROR, mess, "text/plain");
		}
	}
}
