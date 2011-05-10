package togos.jobkernel.mf;

import togos.mf.api.AsyncCallable;
import togos.mf.api.Request;
import togos.mf.api.ResponseHandler;

public class AggregatingAsyncCallable implements AsyncCallable, PossibleRequestHandler
{
	AsyncCallAggregatePool aggregatorPool;
	AsyncCallable next;
	
	/**
	 * @param hp handles will be fetched from here
	 * @param hq unfilled handles (on which #prepareToFetch returns true) will be pushed here 
	 */
	public AggregatingAsyncCallable( AsyncCallAggregatePool hp, AsyncCallable next ) {
		this.aggregatorPool = hp;
		this.next = next;
	}
	
	public void callAsync( Request req, ResponseHandler rHandler ) {
		AsyncCallAggregate h = aggregatorPool.getAggregate(req);
		if( h.addResponseHandler(rHandler) && h.prepareToFetch(req) ) {
			next.callAsync(req, h);
		}
    }
	
	public boolean canHandle( Request req ) {
		return ((PossibleRequestHandler)next).canHandle(req);
	}
}
