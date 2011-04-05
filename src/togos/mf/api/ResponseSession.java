package togos.mf.api;

import java.util.Iterator;

/** Represents the stream of events and responses sent back after opening a
 * call to a RequestHandler */ 
public interface ResponseSession extends Iterator {
	/** Indicate to the responding component that no more results are desired. */
	public void close();
}
