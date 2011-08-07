package togos.minecraft.mapgen.mf;

import java.util.Collection;
import java.util.Iterator;

import togos.jobkernel.mf.PossibleRequestHandler;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.TwoStepCallable;
import togos.mf.base.BaseResponse;

public class TwoStepMultiDispatch implements TwoStepCallable, PossibleRequestHandler
{
	Collection callables;

	public boolean canHandle( Request req ) {
		for( Iterator i=callables.iterator(); i.hasNext(); ) {
			Object c = i.next();
			if( !(c instanceof PossibleRequestHandler) || ((PossibleRequestHandler)c).canHandle(req) ) {
				return true;
			}
		}
		return false;
	}
	
	static class TSMDS {
		public Request req;
		public TwoStepCallable currentCallable;
		public Object currentHnd;
		public Iterator restOfCallables;
		public TSMDS( Request req, TwoStepCallable currentCallable, Object currentHnd, Iterator restOfCallables ) {
			this.req             = req;
			this.currentCallable = currentCallable;
			this.currentHnd      = currentHnd;
			this.restOfCallables = restOfCallables;
		}
	}
	
	public Object beginRequest( Request req ) {
		for( Iterator i=callables.iterator(); i.hasNext(); ) {
			TwoStepCallable c = (TwoStepCallable)i.next();
			if( !(c instanceof PossibleRequestHandler) || ((PossibleRequestHandler)c).canHandle(req) ) {
				return new TSMDS( req, c, c.beginRequest(req), i );
			}
		}
		return null;
	}

	public Response readResponse( Object hnd ) {
		if( hnd == null ) return BaseResponse.RESPONSE_UNHANDLED;
		
		TSMDS h = (TSMDS)hnd;
		Response res = h.currentCallable.readResponse(h.currentHnd);
		while( res.getStatus() < 100 &&  h.restOfCallables.hasNext() ) {
			TwoStepCallable c = (TwoStepCallable)h.restOfCallables.next();
			res = c.readResponse(c.beginRequest(h.req));
		}
		return res;
	}
}
