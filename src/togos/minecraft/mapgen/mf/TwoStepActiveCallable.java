package togos.minecraft.mapgen.mf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import togos.jobkernel.mf.Active;
import togos.jobkernel.mf.ActiveFunction;
import togos.jobkernel.mf.PossibleRequestHandler;
import togos.jobkernel.uri.ActiveRef;
import togos.mf.api.Request;
import togos.mf.api.RequestVerbs;
import togos.mf.api.Response;
import togos.mf.api.TwoStepCallable;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.mf.value.URIRef;

public class TwoStepActiveCallable implements TwoStepCallable, PossibleRequestHandler
{
	Map activeFunctions; 
	TwoStepCallable rootCallable;
	BlockingQueue jobQueue = null;
	
	static class Handle {
		public ActiveRef ref;
		public ActiveFunction func;
		
		public Handle( ActiveRef ref, ActiveFunction func ) {
			this.ref = ref;
			this.func = func;
		}
		
		Map resourceHandles = new HashMap();
	}
	
	public Object beginRequest( ActiveRef ref ) {
		ActiveFunction af = (ActiveFunction)activeFunctions.get(ref.getFunctionName());
		Handle h = new Handle( ref, af );
		for( Iterator i=af.getRequiredResourceRefs(ref).iterator(); i.hasNext(); ) {
			URIRef r = (URIRef)i.next();
			h.resourceHandles.put(r.getUri(), rootCallable.beginRequest(new BaseRequest(RequestVerbs.GET,r.getUri())));
		}
		return h;
	}
	
	public Response readResponse( Handle hnd ) {
		Map resources = new HashMap();
		for( Iterator i=hnd.resourceHandles.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			resources.put(e.getKey(), rootCallable.readResponse(e.getValue()));
		}
		return hnd.func.run(hnd.ref, resources);
	}
	
	
	
	//// Implement TwoStepCallable
	
	public boolean canHandle( Request req ) {
		return req.getResourceName().startsWith("active:");
	}
	
	public Object beginRequest( Request req ) {
		if( !canHandle(req) ) return null;
		
		return beginRequest( (ActiveRef)Active.parseRef( req.getResourceName() ) );
	}
	
	public Response readResponse( Object hnd ) {
		if( hnd == null ) return BaseResponse.RESPONSE_UNHANDLED;
		return readResponse( (Handle)hnd );
	}
}
