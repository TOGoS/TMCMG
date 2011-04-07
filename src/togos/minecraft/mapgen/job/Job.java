package togos.minecraft.mapgen.job;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Job
{
	public static final int STATUS_CREATED = 0;
	public static final int STATUS_FETCHED = 10;
	public static final int STATUS_STARTED = 50;
	public static final int STATUS_COMPLETE = 100;
	public static final int STATUS_ERROR = 200;
	
	public int status = STATUS_CREATED;
	protected Set statusListeners = Collections.EMPTY_SET; 
	
	public synchronized void addStatusListener( JobStatusListener l ) {
		if( statusListeners == Collections.EMPTY_SET ) {
			statusListeners = new HashSet();
		}
		statusListeners.add(l);
	}
	
	public synchronized void setStatus( int status ) {
		/** Don't want to get multiple setStatus(complete or error): */
		if( isDone() ) return;
		
		if( status == this.status ) return;
		this.status = status;
		for( Iterator i=statusListeners.iterator(); i.hasNext(); ) {
			((JobStatusListener)i.next()).jobStatusUpdated(this);
		}
	}
	
	public boolean isDone() {
		return status >= STATUS_COMPLETE;
	}
}
