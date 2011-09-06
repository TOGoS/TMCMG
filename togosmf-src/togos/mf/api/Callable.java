package togos.mf.api;

public interface Callable {
	/** Send a request and expect a response in return.
	 * Should return null if this RequestHandler does not know what to do with the request. */
	public Response call( Request req );
}
