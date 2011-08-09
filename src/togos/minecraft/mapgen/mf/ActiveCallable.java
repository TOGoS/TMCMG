package togos.minecraft.mapgen.mf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import togos.jobkernel.mf.Active;
import togos.jobkernel.mf.ActiveFunction;
import togos.jobkernel.mf.PossibleRequestHandler;
import togos.jobkernel.uri.ActiveRef;
import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.RequestVerbs;
import togos.mf.api.Response;
import togos.mf.base.BaseRequest;
import togos.mf.value.URIRef;

public class ActiveCallable implements Callable, PossibleRequestHandler
{
	Map activeFunctions; 
	Callable rootCallable;
	BlockingQueue jobQueue = null;
	
	public ActiveCallable( Callable rootCallable, Map activeFunctions ) {
		this.rootCallable = rootCallable;
		this.activeFunctions = activeFunctions;
	}
	
	public Response call( Request req ) {
		final ActiveRef ref = (ActiveRef)Active.parseRef( req.getResourceName() );
		final ActiveFunction af = (ActiveFunction)activeFunctions.get(ref.getFunctionName());
		final Map resources = new HashMap();
		
		for( Iterator i=af.getRequiredResourceRefs(ref).iterator(); i.hasNext(); ) {
			URIRef r = (URIRef)i.next();
			Response res = rootCallable.call(new BaseRequest(RequestVerbs.GET,r.getUri()));
			if( res.getStatus() != 200 ) {
				throw new RuntimeException("Non-normal status returned for "+r.getUri());
			}
			resources.put(r.getUri(), res.getContent());
		}
		
		if( jobQueue != null ) {
			final LinkedBlockingQueue resultQueue = new LinkedBlockingQueue();
			Runnable job = new Runnable() {
				public void run() {
					try {
						resultQueue.put( af.run(ref,resources) );
					} catch( InterruptedException e ) {
						Thread.currentThread().interrupt();
						throw new RuntimeException(e);
					}
				}
			};
			try {
				jobQueue.put( job );
				return (Response)resultQueue.take();
			} catch( InterruptedException e ) {
				Thread.currentThread().interrupt();
				throw new RuntimeException(e);
			}
		} else {
			return af.run(ref, resources);
		}
	}
	
	public boolean canHandle( Request req ) {
		return req.getResourceName().startsWith("active:");
    }
}
