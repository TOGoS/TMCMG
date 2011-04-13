package togos.minecraft.mapgen.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.RequestVerbs;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;

public class ResourceManager implements Callable
{	
	WeakHashMap chunkHandles;
	
	ArrayList resourceLoaders = new ArrayList();
	
	public void addResourceLoader( Callable l ) {
		this.resourceLoaders.add(l);
	}
	
	public synchronized ResourceHandle getChunkHandle( String resourceId ) {
		ResourceHandle h = new ResourceHandle( resourceId );
		ResourceHandle stored = (ResourceHandle)chunkHandles.get(h);
		if( stored == null ) {
			chunkHandles.put(h,stored = h);
		}
		return stored;
	}
	
	public Response load( String resourceId ) {
		BaseRequest req = new BaseRequest("GET",resourceId); 
		for( Iterator i=resourceLoaders.iterator(); i.hasNext(); ) {
			Callable l = (Callable)i.next();
			Response t = l.call(req);
			if( t.getStatus() != ResponseCodes.UNHANDLED ) return t;
		}
		return BaseResponse.RESPONSE_UNHANDLED;
	}
	
	public Object getTarget( String resourceId ) {
		ResourceHandle ch = getChunkHandle( resourceId );
		Response target;
		synchronized( ch ) {
			target = ch.getTarget();
			if( target == null ) {
				target = load(resourceId);
			}
			ch.setTarget( target );
		}
		return target;
	}
	
	public Response call( Request req ) {
		if( RequestVerbs.GET.equals(req.getVerb()) ) {
			return load( req.getResourceName() );
		}
		return BaseResponse.RESPONSE_UNHANDLED;
	}
}
