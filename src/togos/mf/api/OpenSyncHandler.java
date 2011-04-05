package togos.mf.api;

public interface OpenSyncHandler {
	/** Will handle the request, send events back with calls to session.sendEvent, and
	 * then return the final result. */
	public Response openSync( Request req, RequestSession session );
}
