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

public class WebServer {
	class Request {
		public Request( String verb, String resourceName ) {
			this.verb = verb;
			this.resourceName = resourceName;
		}
		
		public String verb;
		public String resourceName;
	}
	
	class Response {
		public int statusCode;
		public String statusText;
		public String contentType = "text/plain; charset=utf-8";
		public byte[] data;
	}
	
	interface RequestHandler {
		public Response handle( Request req );
	}
	
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
				Request req = new Request(rp[0], rp[1]);
				Response res = handle( req );
				
				OutputStream o = cs.getOutputStream();
				String responseHeaders =
					"HTTP/1.0 "+res.statusCode+" "+res.statusText+"\r\n"+
					"Content-Length: "+res.data.length+"\r\n"+
					"Content-Type: "+res.contentType+"\r\n"+
					"\r\n";
				o.write(responseHeaders.getBytes("ASCII"));
				o.write(res.data);
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
	
	protected Response _handle( Request req ) {
		for( Iterator i=requestHandlers.iterator(); i.hasNext(); ) {
			Response res = ((RequestHandler)i.next()).handle(req);
			if( res != null ) return res;
		}
		throw new RuntimeException("No handler found for "+req.verb+" "+req.resourceName);
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
				
				Response res = new Response();
				res.statusCode = 500;
				res.statusText = "Server Error";
				res.contentType = "text/plain; charset=utf-8";
				res.data = baos.toByteArray();
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
		"Usage: WebServer [-index-root <dir-containing-indexes>]";
	
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
