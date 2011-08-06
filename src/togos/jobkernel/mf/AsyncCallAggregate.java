package togos.jobkernel.mf;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseHandler;

/**
 * A response handler that wraps many response handlers to
 * allow multiple identical requests to be served by a single
 * call.  Used by AggregatingAsyncCallable. 
 */
public class AsyncCallAggregate implements ResponseHandler
{
	SoftReference res;
	Set responseHandlers = Collections.EMPTY_SET;
	boolean beingFetched;
	
	/**
	 * @param rh
	 * @return true if the response handler was added (not immediately used),
	 *   indicating that the caller may want to call prepareToFetch and
	 *   queue this handle for fetching.
	 */
	public boolean addResponseHandler( ResponseHandler rh ) {
		Response res;
		synchronized( this ) {
			res = (Response)(this.res == null ? null : this.res.get());
			if( res == null ) {
				if( this.responseHandlers == Collections.EMPTY_SET ) {
					this.responseHandlers = new HashSet();
				}
				this.responseHandlers.add(rh);
			}
		}
		if( res != null ) {
			rh.setResponse(res);
			return false;
		} else {
			return true;
		}
	}
	
	/**
	 * @param req
	 * @return true if the caller should take care of fetching
	 *   the resource.
	 */
	public synchronized boolean prepareToFetch( Request req ) {
		if( this.beingFetched ) {
			return false;
		}
		this.beingFetched = true;
		return true;
	}
	
	public void setResponse( Response res ) {
		Set oldResponseHandlers;
		synchronized( this ) {
			oldResponseHandlers = this.responseHandlers;
			this.responseHandlers = Collections.EMPTY_SET;
			if( res.getStatus() >= 100 ) {
				// Only cache authoritative responses
				// (this is kind of a workaround to make a certain unit test pass...)
				this.res = new SoftReference(res);
			}
			this.beingFetched = false;
		}
		for( Iterator i=oldResponseHandlers.iterator(); i.hasNext(); ) {
			((ResponseHandler)i.next()).setResponse(res);
		}
	}
}
