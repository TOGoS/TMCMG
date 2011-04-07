package togos.mf.api;

import java.util.Iterator;

/** Represents the stream of events and responses sent back after opening a
 * call to a RequestHandler */ 
public interface ResponseSession extends Iterator
{
	/** Skips over any intermediate events and return the final response.
	 * Should only be called once and not in combination with next() */
	public Response getResponse();
	/** Indicate to the responding component that no more results are desired. */
	public void close();
}
