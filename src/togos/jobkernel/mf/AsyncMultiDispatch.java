package togos.jobkernel.mf;

import java.util.Collection;
import java.util.Iterator;

import togos.mf.api.AsyncCallable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.api.ResponseHandler;
import togos.mf.base.BaseResponse;

public class AsyncMultiDispatch implements AsyncCallable, PossibleRequestHandler
{
	static class AsyncMultiDispatchInstance implements ResponseHandler
	{
		Iterator callables;
		Request request;
		ResponseHandler rHandler;
		Response highestNonAuthoritativeResponse = BaseResponse.RESPONSE_UNHANDLED;
		
		public AsyncMultiDispatchInstance( Iterator callables, Request req, ResponseHandler rHandler ) {
			this.request = req;
			this.callables = callables;
			this.rHandler = rHandler;
		}
		
		public void setResponse( Response res ) {
			switch( res.getStatus() ) {
			case( ResponseCodes.NOT_FOUND ): case( ResponseCodes.UNHANDLED ):
				if( res.getStatus() > highestNonAuthoritativeResponse.getStatus() ) {
					highestNonAuthoritativeResponse = res;
				}
				if( callables.hasNext() ) {
					((AsyncCallable)callables.next()).callAsync(request, this);
				} else {
					rHandler.setResponse( highestNonAuthoritativeResponse );
				}
				break;
			default:
				rHandler.setResponse(res);
			}
		}
	}
	
	Collection callables;
	
	public AsyncMultiDispatch( Collection c ) {
		callables = c;
	}
	
	public void add( AsyncCallable c ) {
		callables.add(c);
	}
	
	public void remove( AsyncCallable c ) {
		callables.remove(c);
	}
	
	public boolean canHandle( Request req ) {
		for( Iterator i=callables.iterator(); i.hasNext(); ) {
			Object c = i.next();
			if( c instanceof PossibleRequestHandler ) {
				PossibleRequestHandler prc = (PossibleRequestHandler)c;
				if( prc.canHandle(req) ) return true;
			} else {
				// If it doesn't implement PRH we don't know that it can't!
				return true;
			}
		}
		return false;
	}
	
	public void callAsync( Request req, ResponseHandler rHandler ) {
		for( Iterator ci = callables.iterator(); ci.hasNext(); ) {
			AsyncCallable c = (AsyncCallable)ci.next();
			if( !(c instanceof PossibleRequestHandler) || ((PossibleRequestHandler)c).canHandle(req) ) {
				c.callAsync(req, new AsyncMultiDispatchInstance(ci,req,rHandler));
				return;
			}
		}
		rHandler.setResponse(BaseResponse.RESPONSE_UNHANDLED);
	}
}
