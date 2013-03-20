package togos.noise2.cache;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.WeakHashMap;

import togos.noise2.vm.dftree.func.Function;

public class SoftCache<K,V> implements Cache<K,V>
{
	protected static class Handle<K,V> {
		public final K key;
		volatile V value = null;
		
		public Handle( K key ) {
			assert key != null;
			this.key = key;
		}
		
		synchronized V getValue( Function<K,V> generator ) {
			if( value == null ) {
				value = generator.apply(key);
				assert value != null;
			}
			return value;
		}
		
		@Override
		public boolean equals( Object other ) {
			return other == this || (other instanceof Handle && key.equals(((Handle<?,?>)other).key));
		}
		
		@Override
		public int hashCode() {
			return key.hashCode();
		}
	}
	
	protected WeakHashMap<Handle<K,V>,Reference<Handle<K,V>>> handles =
		new WeakHashMap<Handle<K,V>,Reference<Handle<K,V>>>();
	
	public int cacheHits = 0, cacheMisses = 0;
	
	protected final Function<Handle<K,V>,Handle<K,V>> newHandleFunction = new Function<Handle<K,V>,Handle<K,V>>() {
		@Override public Handle<K, V> apply( Handle<K, V> input ) {
			return input;
        }
	};
	
	//HashSet<Handle<K,V>> doNotCollect = new HashSet<Handle<K,V>>();
	
	protected synchronized Handle<K,V> getHandle( K key ) {
		final Handle<K,V> tempHandle = new Handle<K,V>( key );
		Reference<Handle<K,V>> sr = handles.get(tempHandle);
		Handle<K,V> h = sr == null ? null : sr.get();
		if( h == null ) {
			++cacheMisses;
			handles.put(tempHandle, new SoftReference<Handle<K,V>>(tempHandle));
			//doNotCollect.add(tempHandle);
			return tempHandle;
		} else {
			++cacheHits;
			return h;
		}
	}
	
	public V get( K key ) {
		return getHandle(key).value;
	}
	
	public V get( K key, Function<K,V> generator ) {
		return getHandle(key).getValue(generator);
	}
	
	public void put( K key, V obj ) {
		getHandle(key).value = obj;
	}
}
