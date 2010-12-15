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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import togos.genfs.Tokenizer;

public class GenFSServer
{
	public class ClientError extends Exception {
        private static final long serialVersionUID = 1L;
	}
	public class FSError extends Exception {
        private static final long serialVersionUID = 1L;		
	}
	public class PermissionError extends FSError {
        private static final long serialVersionUID = 1L;
	}
	
	public static class AliasResponse {
		String realPath;
		public AliasResponse( String realPath ) {
			this.realPath = realPath;
		}
	}
	
	public static class Stat {
		public long size;
		public int mode;
		public Stat() {}
		public Stat( long size, int mode ) {
			this.size = size;
			this.mode = mode;
		}
	}
	
	public interface RequestHandler {
		public Stat getStat( String path ) throws FSError;
		public List getDirEntries( String path ) throws FSError;
		public void truncate( String path ) throws FSError;
		public AliasResponse openRead( String path ) throws FSError;
		public AliasResponse openWrite( String path ) throws FSError;
		public void closeRead( String path, String aliasedPath ) throws FSError;
		public void closeWrite( String path, String aliasedPath ) throws FSError;
	}
	
	public static class DirectoryEntry extends Stat {
		public String name;
		
		public DirectoryEntry() { }
		public DirectoryEntry( String name, long size, int mode ) {
			super( size, mode );
			this.name = name;
		}
	}
	
	static Charset CHARSET = Charset.forName("UTF-8");
	
	RequestHandler rh = new RequestHandler() {
		public Stat getStat( String path ) throws FSError {
			if( "/".equals(path) ) {
				return new Stat( 0, 0040755 );
			} else if( path.endsWith("/hello") ) {
				return new Stat( 0, 0040755 );
			} else {
				return new Stat( 0, 0100644 );
			}
		}
		public List getDirEntries( String path ) throws FSError {
			ArrayList entries = new ArrayList();
			entries.add( new DirectoryEntry( "hello", 0, 0040755) );
			entries.add( new DirectoryEntry( "good bye", 0, 0100644) );
			// Commented out for now because genfs doesn't properly escape when querying
			//entries.add( new DirectoryEntry( "name \" with \\ funky \t chars \n ;)", 0, 0100644) );
			return entries;
		}
		public void truncate( String path ) throws FSError {
			throw new PermissionError();
		}
		public AliasResponse openRead( String path ) throws FSError {
			throw new PermissionError();
		}
		public AliasResponse openWrite( String path ) throws FSError {
			throw new PermissionError();
		}
		public void closeRead( String path, String alias ) throws FSError {
			throw new PermissionError();
		}
		public void closeWrite( String path, String alias ) throws FSError {
			throw new PermissionError();
		}
	};
	
	public class ConnectionHandler implements Runnable {
		Socket sock;
		Writer w;
		public ConnectionHandler( Socket sock ) {
			this.sock = sock;
			try {
				w = new OutputStreamWriter( sock.getOutputStream() );
			} catch( IOException e ) {
				throw new RuntimeException(e);
			}
		}
		protected void writeLine( String line ) throws IOException {
			System.err.println("Writing "+line);
			w.write(line+"\n");
		}
		protected void writeLine( String[] stuff ) throws IOException {
			writeLine( Tokenizer.detokenize(stuff) );
		}
		public void run() {
			try {
				BufferedReader r = new BufferedReader( new InputStreamReader( sock.getInputStream(), CHARSET ));
				String requestLine = r.readLine();
				String[] tokens = Tokenizer.tokenize(requestLine);
				System.err.println("Read "+tokens[0]);
				try {
					if( "GET-STAT".equals(tokens[0]) ) {
						if( tokens.length != 2 ) throw new ClientError();
						Stat s = rh.getStat(tokens[1]);
						writeLine(new String[] {
							"OK-STAT", Long.toString(s.size),
							"0"+Integer.toOctalString(s.mode)
						});
					} else if( "READ-DIR".equals(tokens[0]) ) {
						if( tokens.length != 2 ) throw new ClientError();
						List entries = rh.getDirEntries(tokens[1]);
						writeLine("OK-DIR-LIST");
						for( Iterator i=entries.iterator(); i.hasNext(); ) {
							DirectoryEntry ent = (DirectoryEntry)i.next();
							writeLine(new String[] {
								"DIR-ENTRY", ent.name,
								Long.toString(ent.size),
								"0"+Integer.toOctalString(ent.mode)
							}); 
						}
						writeLine("END-DIR-LIST");
					} else if( "TRUNCATE".equals(tokens[0]) ) {
						if( tokens.length != 2 ) throw new ClientError();
						rh.truncate(tokens[1]);
						writeLine("OK-TRUNCATED");
					} else if( "OPEN-READ".equals(tokens[0]) ) {
						if( tokens.length != 2 ) throw new ClientError();
						AliasResponse ar = rh.openRead(tokens[1]);
						writeLine(new String[]{"OK-ALIAS",ar.realPath});
					} else if( "CREATE+OPEN-WRITE".equals(tokens[0]) ) {
						if( tokens.length != 2 ) throw new ClientError();
						AliasResponse ar = rh.openWrite(tokens[1]);
						writeLine(new String[]{"OK-ALIAS",ar.realPath});
					} else if( "OPEN-WRITE".equals(tokens[0]) ) {
						if( tokens.length != 2 ) throw new ClientError();
						AliasResponse ar = rh.openWrite(tokens[1]);
						writeLine(new String[]{"OK-ALIAS",ar.realPath});
					} else if( "CLOSE-READ".equals(tokens[0]) ) {
						if( tokens.length != 3 ) throw new ClientError();
						rh.closeRead(tokens[1], tokens[2]);
						writeLine("OK-CLOSED");
					} else if( "CLOSE-WRITE".equals(tokens[0]) ) {
						if( tokens.length != 3 ) throw new ClientError();
						rh.closeWrite(tokens[1], tokens[2]);
						writeLine("OK-CLOSED");
					} else {
						writeLine("CLIENT-ERROR");
					}
				} catch( ClientError e ) {
					writeLine("CLIENT-ERROR");
				} catch( PermissionError e ) {
					writeLine("PERMISSION-DENIED");
				} catch( FSError e ) {
					writeLine("INVALID-OPERATION");
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
