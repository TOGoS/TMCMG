package togos.minecraft.mapgen.util;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;

import togos.jobkernel.Service;


public class FileWatcher implements Runnable, Service
{
	HashSet updateListeners = new HashSet();
	File file;
	long interval;
	Thread thread;
	long lastUpdated = -1;

	public FileWatcher( File f, long interval ) {
		this.file = f;
		this.interval = interval;
	}
	
	public void addUpdateListener( FileUpdateListener l ) {
		this.updateListeners.add(l);
	}
	
	protected synchronized void updated(long updated) {
		for( Iterator i=updateListeners.iterator(); i.hasNext(); ) {
			((FileUpdateListener)i.next()).fileUpdated(file);
		}
		lastUpdated = updated;
	}
	
	/**
	 * Force on-update callbacks to be called as if
	 * the file were updated right now.  It may be useful to
	 * call this to initialize the state of things after the
	 * filewatcher is created, since otherwise the callbacks
	 * would not be called until after the first update to the
	 * file's modification time *after* the FileWatcher's creation.
	 */
	public void forceUpdate() {
		updated(System.currentTimeMillis());
	}
	
	public void run() {
		try {
			while( true ) {
				// updated will = 0 as long as the file does not exist
				long updated = file.lastModified();
				if( lastUpdated == -1 ) {
					lastUpdated = updated;
				}
				if( updated > lastUpdated ) {
					updated(updated);
				}
				Thread.sleep(interval);
			}
        } catch( InterruptedException e ) {
        }
	}
	
	public synchronized void start() {
		if( thread == null ) {
			thread = new Thread(this, "File Watcher");
			thread.start();
		}
	}
	
	public synchronized void halt() {
		if( thread != null ) {
			thread.interrupt();
        	thread = null;
		}
	}
}
