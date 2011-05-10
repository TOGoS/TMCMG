package togos.jobkernel.mf;

import togos.mf.api.AsyncCallable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseHandler;

public class LimitingAsyncCallable implements AsyncCallable, PossibleRequestHandler
{
	int concurrentRequestLimit;
	int concurrentRequests = 0;
	AsyncCallable next;
	
	public LimitingAsyncCallable( int concurrentRequestLimit, AsyncCallable next ) {
		this.concurrentRequestLimit = concurrentRequestLimit;
		this.next = next;
	}
	
	protected synchronized void requestFinished() {
		--concurrentRequests;
		notify();
	}
	
	public void callAsync( Request req, final ResponseHandler rHandler ) {
		synchronized( this ) {
			while( concurrentRequests >= concurrentRequestLimit ) {
				try {
					wait();
				} catch( InterruptedException e ) {
					Thread.currentThread().interrupt();
					throw new RuntimeException(e);
				}
			}
			++concurrentRequests;
		}
		
		next.callAsync(req, new ResponseHandler() {
			public void setResponse(Response res) {
				requestFinished();
				rHandler.setResponse(res);
			}
		});
	}
	
	public boolean canHandle( Request req ) {
		return ((PossibleRequestHandler)next).canHandle(req);
	}
}
