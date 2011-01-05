package togos.noise2.cache;

import java.lang.ref.SoftReference;
import java.util.WeakHashMap;

import togos.noise2.function.FunctionO_O;

public class SoftCache implements Cache
{
	protected static SoftCache instance = new SoftCache();
	public static SoftCache getInstance() { return instance; }
	
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
	
	protected WeakHashMap handles = new WeakHashMap();
	
	protected synchronized Handle getHandle( Object key ) {
		SoftReference sr = (SoftReference)handles.get(key);
		Handle h = sr == null ? null : (Handle)sr.get();
		if( h == null ) {
			h = new Handle(key);
			handles.put(key,new SoftReference(h));
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
