package togos.mf.api;

public interface SendHandler {
	/** Send a request, but do not wait for any response.
	 * Should return false if this RequestHandler does not know what to do with the request. */
	public boolean send( Request req );
}
