package togos.mf.api;

public interface OpenSessionHandler {
	/** Send a request and return a MessageIterator that will give every event
	 * and message sent back associated with the request.
	 * Should return null if this RequestHandler does not know what to do with the request. */
	public ResponseSession openSession( Request req );
}
