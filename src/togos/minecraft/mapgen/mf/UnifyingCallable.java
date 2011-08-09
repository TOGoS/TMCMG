package togos.minecraft.mapgen.mf;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.RequestVerbs;
import togos.mf.api.Response;
import togos.minecraft.mapgen.resource.ResourceHandle;

/**
 * GET requests that pass through at the same time
 * will only be handled once by the backing Callable.
 */
public class UnifyingCallable implements Callable
{
	Callable next;
	WeakHashMap handling = new WeakHashMap();
	
	public UnifyingCallable( Callable next ) {
		this.next = next;
	}
	
	protected synchronized ResourceHandle getHandle( String name ) {
		ResourceHandle h = new ResourceHandle(name);
		
		Reference hr = (Reference)handling.get(h);
		if( hr == null || (hr).get() == null ) {
			handling.put(name, new WeakReference(h));
		}
		return h;
	}
	
	public Response call( Request req ) {
		if( RequestVerbs.GET.equals(req.getVerb()) ) {
			ResourceHandle rh = getHandle(req.getResourceName());
			if( rh.getResolvePermission() ) {
				rh.setValue(next.call(req));
			}
			try {
                return (Response)rh.waitForValue();
            } catch( InterruptedException e ) {
            	Thread.currentThread().interrupt();
            	throw new RuntimeException(e);
            }
		} else {
			return next.call( req );
		}
	}
}