package togos.minecraft.mapgen.resource;

public class ResourceHandle
{
	protected Object key;
	protected boolean resolving;
	protected Object value;
	
	public ResourceHandle( Object key ) {
		this.key = key;
	}
	
	public synchronized boolean getResolvePermission() {
		if( resolving || value != null ) {
			return false;
		} else {
			resolving = true;
			return true;
		}
	}
	
	public synchronized Object waitForValue() throws InterruptedException {
		while( value == null ) {
			wait();
		}
		return value;
	}
	
	public Object getValue() {
		return value;
	}
	
	public synchronized void setValue( Object value ) {
		this.value = value;
		notifyAll();
	}
	
	public boolean equals( Object oth ) {
		if( !(oth instanceof ResourceHandle) ) return false;
		
		return key.equals( ((ResourceHandle)oth).key );
	}
	
	public int hashCode() {
		return key.hashCode() ^ 119012;
	}
}
