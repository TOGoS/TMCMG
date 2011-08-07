package togos.jobkernel.mf;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import togos.jobkernel.uri.ActiveRef;
import togos.mf.api.AsyncCallable;
import togos.mf.api.Request;
import togos.mf.api.RequestVerbs;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.api.ResponseHandler;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.mf.value.URIRef;

public class ActiveCallable implements AsyncCallable, PossibleRequestHandler
{
	Map activeFunctions;
	AsyncCallable rootCallable;
	BlockingQueue jobQueue;
	
	public ActiveCallable( Map activeFunctions, AsyncCallable rc, BlockingQueue jobQueue ) {
		this.activeFunctions = activeFunctions;
		this.rootCallable = rc;
		this.jobQueue = jobQueue;
	}
	
	protected static ActiveRef getActiveRef( URIRef ref ) {
		if( ref instanceof ActiveRef ) {
			// SHORTCUT!!!
			return (ActiveRef)ref;
		} else {
			return (ActiveRef)Active.parseRef(ref.getUri());
		}
	}
	
	protected static ActiveRef getActiveRef( Request req ) {
		if( req instanceof RefRequest ) {
			// SHORTCUT!!!
			return getActiveRef(((RefRequest)req).getResourceRef());
		} else {
			return (ActiveRef)Active.parseRef( req.getResourceName() );
		}
	}
	
	public boolean canHandle( Request req ) {
		return
			// SHORTCUT!!!
			(req instanceof RefRequest && ((RefRequest)req).getResourceRef() instanceof ActiveRef) ||
			req.getResourceName().startsWith("active:");
	}
	
	abstract class ResponseCounter {
		protected abstract void onReady();
		/**
		 * @return true if we should continue normal operation despite getting
		 * an error from a required resource.
		 * */
		protected abstract boolean onError( String uri, Response res );
		
		Collection requiredResourceUris;
		Map resources = new HashMap();
		boolean done;
		
		public ResponseCounter( Collection requiredResourceRefs ) {
			requiredResourceUris = new HashSet();
			for( Iterator i=requiredResourceRefs.iterator(); i.hasNext(); ) {
				requiredResourceUris.add(i.next().toString());
			}
		}
		
		public synchronized void setResponse( String uri, Response res ) {
			if( done ) return;
			
			if( res.getStatus() != ResponseCodes.NORMAL ) {
				if( !onError(uri,res) ) {
					done = true;				
					return;
				}
			}
			
			resources.put(uri, res.getContent());
			requiredResourceUris.remove(uri);
			if( requiredResourceUris.size() == 0 ) {
				done = true;
				onReady();
			}
		}
	}
	
	public void callAsync( Request req, final ResponseHandler rHandler ) {
		if( !canHandle(req) ) {
			rHandler.setResponse(BaseResponse.RESPONSE_UNHANDLED);
			return;
		}
		
		final ActiveRef ref = getActiveRef(req);
		final ActiveFunction func = (ActiveFunction)activeFunctions.get(ref.getFunctionName());
		if( func == null ) {
			rHandler.setResponse(BaseResponse.RESPONSE_UNHANDLED);
			return;
		}
		
		Collection requiredResourceRefs;
		try {
			requiredResourceRefs = func.getRequiredResourceRefs(ref);
		} catch( RuntimeException e ) {
			rHandler.setResponse(new BaseResponse(ResponseCodes.SERVER_ERROR, e));
			return;
		}
		
		if( requiredResourceRefs.size() == 0 ) {
			try {
				Response res;
				if( (res = func.runFast(ref, Collections.EMPTY_MAP)) != null ) {
					rHandler.setResponse(res);
					return;
				}
				
	            jobQueue.put( new Runnable() {
	            	public void run() {
						Response res;
						try {
							res = func.run(ref, Collections.EMPTY_MAP);
						} catch( RuntimeException e ) {
							res = new BaseResponse(ResponseCodes.SERVER_ERROR, e);
						}
						rHandler.setResponse(res);
	            	}
	            });
            } catch( InterruptedException e ) {
            	// Then I guess rHandler'll never be triggered?
            	Thread.currentThread().interrupt();
	            throw new RuntimeException(e);
            }
		} else {
			final ResponseCounter rc = new ResponseCounter(requiredResourceRefs) {
				protected void onReady() {
					Response res;
					if( (res = func.runFast(ref, resources)) != null ) {
						rHandler.setResponse(res);
						return;
					}
					
					jobQueue.add( new Runnable() {
						public void run() {
							Response res;
							try {
								res = func.run(ref, resources);
							} catch( RuntimeException e ) {
								res = new BaseResponse(ResponseCodes.SERVER_ERROR, e);
							}
							rHandler.setResponse(res);
						}
					});
				}
				
				protected boolean onError( String uri, Response res ) {
					rHandler.setResponse(res);
					return false;
				}
			};
			for( Iterator i=requiredResourceRefs.iterator(); i.hasNext(); ) {
				final String uri = i.next().toString();
				rootCallable.callAsync(new BaseRequest(RequestVerbs.GET, uri), new ResponseHandler() {
					public void setResponse( Response res ) {
						rc.setResponse(uri, res);
					}
				});
			}
		}
	}
}
