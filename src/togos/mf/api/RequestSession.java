package togos.mf.api;


/** Used to send messages back to the requester */
public interface RequestSession {
	public void sendEvent( Event e );
	/** Sends a Response back to the sender.
	 * This should usually be followed by a call to close() */
	public void sendResponse( Response res );
	/** Close the session, indicating that there will be no more events or
	 * responses sent back. */
	public void close();
}
