package togos.minecraft.mapgen.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.service.Service;

public class WebServer implements Runnable, Service {
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
		
		// TODO: something faster
		// Can't use BufferedReader because it reads too much
		// and then later content = read(...) misses data.
		protected String readLine(InputStream is) throws IOException {
			int byt;
			String line = "";
			while( (byt = is.read()) != -1 && byt != '\n' ) {
				if( byt != '\r' ) line += (char)byt;
			}
			return line;
		}
		
		public void run() {
			try {
				InputStream is = cs.getInputStream();
				String rl = readLine(is);
				if( rl == null ) return;
				String hl = readLine(is);
				HashMap headers = new HashMap();
				while( hl != null && hl.length() > 0 ) {
					String[] kv = hl.trim().split(": ");
					headers.put(kv[0].toLowerCase(), kv[1]);
					hl = readLine(is);
				}
				
				// Set up content metadata
				Map reqContentMetadata = new HashMap();
				String reqContentType = (String)headers.get("content-type");
				if( reqContentType != null ) {
					reqContentMetadata.put( "http://purl.org/dc/terms/format", reqContentType );
				}
				
				// Read content
				String clstr = (String)headers.get("content-length");
				byte[] reqContent;
				if( clstr != null ) {
					int contentLength = Integer.parseInt(clstr);
					if( contentLength > maxRequestContentLength ) {
						throw new RuntimeException("Input content length too long: "+contentLength);
					}
					reqContent = new byte[contentLength];
					for( int read=0; read < contentLength; ) {
						read += is.read( reqContent, read, contentLength-read );
					}
				} else {
					reqContent = null;
				}
				
				String[] rp = rl.split("\\s");
				Request req = new BaseRequest(rp[0], rp[1], reqContent, reqContentMetadata);
				
				Response res = handle( req );
				
				String contentType = (String)res.getContentMetadata().get(BaseResponse.DC_FORMAT);

				byte[] contentBytes;
				if( res.getContent() instanceof byte[] ) {
					contentBytes = (byte[])res.getContent();
				} else if( res.getContent() instanceof String ) {
					contentBytes = ((String)res.getContent()).getBytes("UTF-8");
					if( contentType == null ) contentType = "text/plain";
					contentType += "; charset=utf-8";
				} else if( res.getStatus() == 204 || "HEAD".equals(req.getVerb()) ) {
					contentBytes = null;
				} else {
					if( res.getContent() instanceof Exception ) {
						((Exception)res.getContent()).printStackTrace();
					}
					throw new RuntimeException("Response content not a byte array, but "+res.getContent());
				}
				
				int httpStatus = 0;
				if( res.getStatus() < 100 ) {
					httpStatus = 404;
				} else {
					httpStatus = res.getStatus();
				}
				
				OutputStream o = cs.getOutputStream();
				String responseHeaders =
					"HTTP/1.0 "+httpStatus+" Hello\r\n";
				if( contentBytes != null ) {
					responseHeaders += "Content-Length: "+contentBytes.length+"\r\n";
				}
				if( contentType != null ) {
					responseHeaders += "Content-Type: "+contentType+"\r\n";
				}
				responseHeaders += "\r\n";
				o.write(responseHeaders.getBytes("ASCII"));
				if( contentBytes != null ) o.write(contentBytes);
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
	public int maxRequestContentLength = 2*1024*1024; // 2 MiB max (we keep it all in RAM!)
	
	public void addRequestHandler( Callable rh ) {
		this.requestHandlers.add(rh);
	}
	
	protected Response _handle( Request req ) {
		for( Iterator i=requestHandlers.iterator(); i.hasNext(); ) {
			Response res = ((Callable)i.next()).call(req);
			if( res.getStatus() != ResponseCodes.UNHANDLED ) return res;
		}
		throw new RuntimeException("No handler found for "+req.getVerb()+" "+req.getResourceName());
	}
	
	protected Response handle( Request req ) {
		try {
			return _handle( req );
		} catch( RuntimeException e ) {
			try {
				e.printStackTrace();
				
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
	
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(port);
			while( true ) {
				Socket clientSock = ss.accept();
				new Thread(new ConnectionHandler(clientSock)).start();
			}
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	Thread runThread; 
	
	public synchronized void start() {
		if( runThread != null ) return;
		runThread = new Thread(this);
		runThread.start();
	}
	public synchronized  void halt() {
		if( runThread == null ) return;
		runThread.interrupt();
		runThread = null;
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
