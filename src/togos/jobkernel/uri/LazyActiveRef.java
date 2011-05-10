package togos.jobkernel.uri;

import java.util.Collections;
import java.util.List;

import togos.jobkernel.mf.Active;

/**
 * Doesn't generate a URI until asked to
 */
public class LazyActiveRef extends BaseActiveRef
{
	protected String uri;
	
	public LazyActiveRef( String functionName, List arguments ) {
		super( functionName, arguments );
	}
	
	public LazyActiveRef(String functionName) {
		this( functionName, Collections.EMPTY_LIST );
	}
	
	public synchronized String getUri() {
		if( uri == null ) uri = Active.mkActiveUri(functionName, arguments);
		return uri;
	}
}
