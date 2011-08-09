package togos.mf.api;


public interface TwoStepCallable
{
	/**
	 * Should do any initial queuing that can be done quickly of work
	 * to be done to fulfill the request. 
	 * 
	 * @return a handle that can be passed to readResponse to get the
	 *   final Response object.  May be null (but if so, readResponse
	 *   must be able to handle nulls).
	 */
	public Object beginRequest( Request req );
	
	/**
	 * Do any remaining work or waiting required to get the response based on
	 * the handle hnd, which should have been created by beginRequest on
	 * this same TwoStepCallable. 
	 */
	public Response readResponse( Object hnd );
}
