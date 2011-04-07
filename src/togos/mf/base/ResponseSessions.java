package togos.mf.base;

import java.util.ArrayList;
import java.util.Iterator;

import togos.mf.api.Response;
import togos.mf.api.ResponseSession;

public class ResponseSessions {
	public static ResponseSession createSingleResultSession(Response res) {
		ArrayList responses = new ArrayList();
		responses.add(res);
		return wrapIterator( responses.iterator() );
	}

	public static final ResponseSession NORESPONSE = new ResponseSession() {
		public boolean hasNext() {return false;}
		public Object next() {return null;}
		public Response getResponse() {return null;}
		public void remove() {}
		public void close() {}
	};
	
	public static final ResponseSession wrapIterator(final Iterator i) {
		return new ResponseSession() {
			public boolean hasNext() { return i.hasNext(); }
			public Object next() { return i.next(); }
			public Response getResponse() {
				while( i.hasNext() ) {
					Object n = i.next();
					if( n instanceof Response ) return (Response)i;
				}
				return null;
			}
			public void remove() { i.remove(); }
			public void close() {}
		};
	}
}
