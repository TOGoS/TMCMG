package togos.minecraft.mapgen.mf;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

import togos.jobkernel.mf.PossibleRequestHandler;
import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.base.BaseResponse;

public class MultiDispatch implements Callable, PossibleRequestHandler
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
	
	public boolean canHandle( Request req ) {
		for( Iterator i=callables.iterator(); i.hasNext(); ) {
			Object c = i.next();
			if( !(c instanceof PossibleRequestHandler) || ((PossibleRequestHandler)c).canHandle(req) ) {
				return true;
			}
		}
		return false;
	}
	
	public Response call( Request req ) {
		Response highestNonAuthoritative = null;
		for( Iterator i=callables.iterator(); i.hasNext(); ) {
			Callable c = (Callable)i.next();
			if( !(c instanceof PossibleRequestHandler) || ((PossibleRequestHandler)c).canHandle(req) ) {
				Response res = c.call(req);
				if( res.getStatus() < 100 ) {
					if( highestNonAuthoritative == null || res.getStatus() > highestNonAuthoritative.getStatus() ) {
						highestNonAuthoritative = res;
					}
				} else {
					return res;
				}
			}
		}
		return highestNonAuthoritative == null ? BaseResponse.RESPONSE_UNHANDLED : highestNonAuthoritative;
	}
}
