package togos.genfs.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;

import togos.genfs.Tokenizer;

public class GenFSServer
{
	public static class AliasResponse {
		String realPath;
		public AliasResponse( String realPath ) {
			this.realPath = realPath;
		}
	}
	
	public interface RequestHandler {
	}
	
	static Charset CHARSET = Charset.forName("UTF-8");
	
	RequestHandler rh;
	
	public class ConnectionHandler implements Runnable {
		Socket sock;
		public ConnectionHandler( Socket sock ) {
			this.sock = sock;
		}
		public void run() {
			try {
				BufferedReader r = new BufferedReader( new InputStreamReader( sock.getInputStream(), CHARSET ));
				Writer w = new OutputStreamWriter( sock.getOutputStream() );
				String requestLine = r.readLine();
				String[] tokens = Tokenizer.tokenize(requestLine);
				System.err.println("Read "+tokens[0]);
				if( "GET-STAT".equals(tokens[0]) ) {
					if( "/".equals(tokens[1]) ) {
						w.write("OK-STAT 0 0040755\n");
					} else {
						w.write("OK-STAT 0 0100644\n");
					}
				} else if( "READ-DIR".equals(tokens[0]) ) {
					w.write("OK-DIR-LIST\n");
					w.write("DIR-ENTRY hello 0 0100644\n" );
					w.write("DIR-ENTRY \"good bye\" 0 0100644\n" );
					w.write("END-DIR-LIST\n");
				} else if( "TRUNCATE".equals(tokens[0]) ) {
					w.write("PERMISSION-DENIED\n");
				} else if( "OPEN-READ".equals(tokens[0]) ) {
					w.write("PERMISSION-DENIED\n");
				} else if( "CREATE+OPEN-WRITE".equals(tokens[0]) ) {
					w.write("PERMISSION-DENIED\n");
				} else if( "OPEN-WRITE".equals(tokens[0]) ) {
					w.write("PERMISSION-DENIED\n");
				} else {
					w.write("CLIENT-ERROR\n");
				}
				w.flush();
			} catch( IOException e ) {
				throw new RuntimeException(e);
			} finally {
				try { 
					sock.close();
				} catch( IOException e ) {
					System.err.println("Darn, IOException while trying to close client socket!");
				}
			}
		}
	}
	
	protected void handleConnection( Socket sock ) {
		new Thread(new ConnectionHandler(sock)).start();
	}
	
	public int listenPort = 23823;
	public InetAddress listenAddr;
	public void run() {
		try {
			ServerSocket ss = new ServerSocket(listenPort, 0, listenAddr);
			Socket clientSock;
			while( (clientSock = ss.accept()) != null ) {
				handleConnection(clientSock);
			}
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		new GenFSServer().run();
	}
}
