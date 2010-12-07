package togos.minecraft.mapgen.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import togos.mf.api.CallHandler;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;

public class WebServer {
	class ConnectionHandler implements Runnable {
		protected Socket cs;
		
		public ConnectionHandler( Socket cs ) {
			this.cs = cs;
		}
		
		protected String getConnectionErrorDescription() {
			SocketAddress remoteAddr = cs.getRemoteSocketAddress();
			if( remoteAddr != null ) {
				return "Error handling connection from "+remoteAddr;
			} else {
				return "Error handling connection";
			}
		}
		
		public void run() {
			try {
				BufferedReader r = new BufferedReader(new InputStreamReader(cs.getInputStream()));
				String rl = r.readLine();
				if( rl == null ) return;
				String hl = r.readLine();
				while( hl != null && hl.length() > 0 ) {
					hl = r.readLine();
					// ignoring headers for now...
				}
				String[] rp = rl.split("\\s");
				Request req = new BaseRequest(rp[0], rp[1]);
				Response res = handle( req );
				
				byte[] contentBytes;
				if( res.getContent() instanceof byte[] ) {
					contentBytes = (byte[])res.getContent();
				} else {
					throw new RuntimeException("Response content not a byte array, but "+res.getContent());
				}
				String contentType = res.getContentMetadata().get(BaseResponse.DC_FORMAT).toString();
				
				OutputStream o = cs.getOutputStream();
				String responseHeaders =
					"HTTP/1.0 "+res.getStatus()+" Hello\r\n"+
					"Content-Length: "+contentBytes.length+"\r\n";
				if( contentType != null ) {
					responseHeaders += "Content-Type: "+contentType+"\r\n";
				}
				responseHeaders += "\r\n";
				o.write(responseHeaders.getBytes("ASCII"));
				o.write(contentBytes);
				o.flush();
			} catch( UnsupportedEncodingException e ) {
				System.err.println(getConnectionErrorDescription());
				e.printStackTrace();
			} catch( IOException e ) {
				System.err.println(getConnectionErrorDescription());
				e.printStackTrace();
			} finally {
				try {
					if( !cs.isClosed() ) cs.close();
				} catch( IOException e ) {}
			}
		}
	}
	
	protected List requestHandlers = new ArrayList();
	public int port = 14419;
	
	public void addRequestHandler( CallHandler rh ) {
		this.requestHandlers.add(rh);
	}
	
	protected Response _handle( Request req ) {
		for( Iterator i=requestHandlers.iterator(); i.hasNext(); ) {
			Response res = ((CallHandler)i.next()).call(req);
			if( res.getStatus() != ResponseCodes.RESPONSE_UNHANDLED ) return res;
		}
		throw new RuntimeException("No handler found for "+req.getVerb()+" "+req.getResourceName());
	}
	
	protected Response handle( Request req ) {
		try {
			return _handle( req );
		} catch( RuntimeException e ) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
				PrintWriter pw = new PrintWriter(osw);
				e.printStackTrace(pw);
				pw.flush(); osw.flush();
				
				BaseResponse res = new BaseResponse();
				res.status = 500;
				res.putContentMetadata(BaseResponse.DC_FORMAT, "text/plain; charset=utf-8");
				res.content = baos.toByteArray();
				return res;
			} catch( IOException e2 ) {
				throw new RuntimeException(e2);
			}
		}
	}
	
	public void run() throws IOException {
		ServerSocket ss = new ServerSocket(port);
		while( true ) {
			Socket clientSock = ss.accept();
			new Thread(new ConnectionHandler(clientSock)).start();
		}
	}
	
	public static final String USAGE =
		"Usage: WebServer [-port <listen-port>]";
	
	public static void main(String[] args) {
		WebServer ws = new WebServer();
		
		for( int i=0; i<args.length; ++i ) {
			if( "-port".equals(args[i]) ) {
			    ws.port = Integer.parseInt(args[++i]);
			} else {
				System.err.println("Unrecognised argument: "+args[i]);
				System.err.println(USAGE);
				System.exit(1);
			}
		}
		
		try {
			ws.run();
		} catch( Exception e ) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
