package togos.minecraft.mapgen.util;

import java.util.HashSet;
import java.util.Iterator;

public class ServiceManager implements Service
{
	protected boolean running;
	protected HashSet services = new HashSet();
	
	public synchronized void add(Service s) {
		services.add(s);
		if( running ) s.start();
	}
	
	public synchronized void start() {
		if( running ) return;
		
		for( Iterator i=services.iterator(); i.hasNext(); ) {
			((Service)i.next()).start();
		}
		
		running = true;
	}
	
	public synchronized void halt() {
		if( !running ) return;
		
		for( Iterator i=services.iterator(); i.hasNext(); ) {
			((Service)i.next()).halt();
		}
		
		running = false;
	}
}
