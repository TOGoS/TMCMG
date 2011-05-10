package togos.jobkernel.mf;

import java.util.Collections;
import java.util.Map;

import togos.mf.api.Request;
import togos.mf.value.URIRef;

public class RefRequest implements Request
{
	final String verb;
	final URIRef ref;
	
	public RefRequest( String verb, URIRef r ) {
		this.verb = verb;
		this.ref = r;
	}
	
	public URIRef getResourceRef() {
		return ref;
	}
	
	public String getResourceName() {
	    return ref.getUri();
	}
	
	public String getVerb() {
	    return verb;
	}
	
	public Map getContentMetadata() {
		return Collections.EMPTY_MAP;
	}
	
	public Object getContent() {
	    return null;
	}
	
	public Map getMetadata() {
		return Collections.EMPTY_MAP;
	}
}
