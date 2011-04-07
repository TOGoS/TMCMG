package togos.minecraft.mapgen.job;

import togos.mf.api.Request;
import togos.mf.api.Response;

public class MFRequestJob extends Job
{
	Request req;
	Response res;
	
	public MFRequestJob( Request req ) {
		this.req = req;
	}
	
	public Request getRequest() {
		return req;
	}
	
	public synchronized void setResponse( Response res ) {
		this.res = res; 
		setStatus( Job.STATUS_COMPLETE );
	}
	
	public Response getResponse() {
		return res;
	}
}
