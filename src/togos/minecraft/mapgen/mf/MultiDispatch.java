package togos.minecraft.mapgen.mf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.base.BaseResponse;

public class MultiDispatch implements Callable
{
	Collection callables;
	
	public MultiDispatch( Collection c ) {
		callables = c;
	}
	
	public MultiDispatch() {
		this(new HashSet());
	}

	public void add( Callable c ) {
		callables.add(c);
	}
	
	public void remove( Callable c ) {
		callables.remove(c);
	}
	
	public Response call( Request req ) {
		Response highestNonAuthoritative = null;
		for( Iterator i=callables.iterator(); i.hasNext(); ) {
			Callable c = (Callable)i.next();
			Response res = c.call(req);
			if( res.getStatus() < 100 ) {
				if( highestNonAuthoritative == null || res.getStatus() > highestNonAuthoritative.getStatus() ) {
					highestNonAuthoritative = res;
				}
			} else {
				return res;
			}
		}
		return highestNonAuthoritative == null ? BaseResponse.RESPONSE_UNHANDLED : highestNonAuthoritative;
	}
}
