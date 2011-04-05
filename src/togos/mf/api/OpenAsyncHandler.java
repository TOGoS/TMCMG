package togos.mf.api;

public interface OpenAsyncHandler {
	/** Will enqueue the request for handling and return immediately.
	 * Events and Requests will be sent back using calls to
	 * session.sendEvent and session.sendResponse, respectively. */
	public void openAsync( Request req, RequestSession session );
}
