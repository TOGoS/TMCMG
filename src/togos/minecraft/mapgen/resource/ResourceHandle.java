package togos.minecraft.mapgen.resource;

public class ResourceHandle
{
	protected Object resolver;
	protected Object value;
	
	public synchronized boolean prepareToResolve( Object me ) {
		if( resolver != null || value != null ) {
			return false;
		} else {
			resolver = me;
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
}
