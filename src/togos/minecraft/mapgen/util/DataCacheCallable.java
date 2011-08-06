package togos.minecraft.mapgen.util;

import java.util.HashMap;
import java.util.Map;

import togos.mf.api.AsyncCallable;
import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.RequestVerbs;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.api.ResponseHandler;
import togos.mf.base.BaseResponse;
import togos.noise2.DigestUtil;

public class DataCacheCallable implements Callable, AsyncCallable
{
	// TODO: the data management stuff should
	// be separated
	
	Map newData = new HashMap();
	Map oldData = new HashMap();
	
	int newDataSize = 0;
	int threshold = 2*1024*1024;
	
	public synchronized void put( String id, byte[] data ) {
		if( oldData.containsKey(id) ) {
			oldData.remove(id);
		}

		newData.put(id, data);
		if( newDataSize > threshold ) {
			Map temp = oldData;
			oldData = newData;
			newData = temp;
			newData.clear();
			newDataSize = data.length;
			newData.put(id,data);
		}
	}
	
	public synchronized byte[] get( String id ) {
		if( newData.containsKey(id) ) {
			return (byte[])newData.get(id);
		}
		if( oldData.containsKey(id) ) {
			return (byte[])oldData.get(id);
		}
		return null;
	}
	
	public Response call( Request req ) {
		String name = req.getResourceName();
		if( !name.startsWith("urn:sha1:") ) return BaseResponse.RESPONSE_UNHANDLED;
		
		if( RequestVerbs.PUT.equals(req.getVerb()) ) {
			byte[] data = (byte[])req.getContent();
			String urn = DigestUtil.getSha1Urn(data);
			if( !urn.equals(name) ) {
				return new BaseResponse(
					ResponseCodes.CALLER_ERROR,
					"Hash of content ("+urn+") does not match resource name ("+name+")"
				);
			}
			put( name, data );
			return new BaseResponse(ResponseCodes.NORMAL_NO_RESPONSE, null);
		} else if( RequestVerbs.GET.equals(req.getVerb()) ) {
			byte[] data = get(name);
			if( data == null ) {
				return new BaseResponse(ResponseCodes.NOT_FOUND, "Couldn't find "+name);
			} else {
				return new BaseResponse(ResponseCodes.NORMAL, data);
			}
		} else {
			return new BaseResponse(ResponseCodes.CALLER_ERROR, "Verb "+req.getVerb()+" not allowed");
		}
	}
	
	public void callAsync( Request req, ResponseHandler rHandler ) {
		rHandler.setResponse( call(req) );
	}
}
