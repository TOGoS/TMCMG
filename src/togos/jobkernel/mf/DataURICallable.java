package togos.jobkernel.mf;

import java.util.HashMap;

import togos.jobkernel.uri.URIUtil;
import togos.mf.api.AsyncCallable;
import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.api.ResponseHandler;
import togos.mf.base.BaseResponse;
import togos.mf.value.Chunk;

public class DataURICallable implements Callable, AsyncCallable, PossibleRequestHandler
{
	public static final DataURICallable instance = new DataURICallable();

	public Response call( Request req ) {
		// @todo: use a buffer or something instead of a byte array
		
		if( !canHandle(req) ) return BaseResponse.RESPONSE_UNHANDLED;
		
		HashMap metadata = new HashMap();
		byte[] data = URIUtil.decodeDataUri(req.getResourceName(), metadata);
		return new BaseResponse(ResponseCodes.NORMAL,new Chunk(data,0,data.length),metadata);
	}

	public void callAsync( Request req, ResponseHandler rHandler ) {
		rHandler.setResponse(call(req));
	}
	
	public boolean canHandle( Request req ) {
		return req.getResourceName().startsWith("data:");
	}
}
