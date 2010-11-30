package togos.minecraft.mapgen.util;

import java.io.File;
import java.util.HashSet;
import java.util.Iterator;


public class FileWatcher implements Runnable, Service
{
	HashSet updateListeners = new HashSet();
	File file;
	Thread thread;
	long lastUpdated = -1;

	public FileWatcher( File f ) {
		this.file = f;
	}
	
	public void addUpdateListener( FileUpdateListener l ) {
		this.updateListeners.add(l);
	}
	
	public void run() {
		try {
			while( true ) {
				long updated = file.lastModified();
				if( lastUpdated == -1 ) {
					lastUpdated = updated;
				}
				if( updated > lastUpdated ) {
					for( Iterator i=updateListeners.iterator(); i.hasNext(); ) {
						((FileUpdateListener)i.next()).fileUpdated(file);
					}
					lastUpdated = updated;
				}
				Thread.sleep(500);
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
