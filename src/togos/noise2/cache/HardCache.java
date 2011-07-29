package togos.noise2.cache;

import java.util.HashMap;

import togos.noise2.vm.dftree.func.FunctionO_O;

public class HardCache implements Cache
{
	protected class Handle {
		final Object key;
		volatile Object value = null;
		
		public Handle( Object key ) {
			this.key = key;
		}
		
		synchronized Object getValue( FunctionO_O generator ) {
			if( value == null ) {
				value = generator.apply(key);
			}
			return value;
		}
	}
	
	protected HashMap handles = new HashMap();
	
	protected synchronized Handle getHandle( Object key ) {
		Handle h = (Handle)handles.get(key);
		if( h == null ) {
			h = new Handle(key);
			handles.put(key,h);
		}
		return h;
	}
	
	public Object get( Object key ) {
		return getHandle(key).value;
	}
	
	public Object get( Object key, FunctionO_O generator ) {
		return getHandle(key).getValue(generator);
	}
	
	public void put( Object key, Object obj ) {
		getHandle(key).value = obj;
	}
}
