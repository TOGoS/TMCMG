package togos.noise2.cache;

import java.lang.ref.Reference;
import java.util.WeakHashMap;

import togos.noise2.vm.dftree.func.Function;

/**
 * TODO: This is a little goofy because it is internal handles that are
 * wrapped in XReferences rather than the cached values themselves.
 * So if using soft or weak references, an object may still be alive in
 * the system, but its entry might get collected.
 * 
 * This could be fixed by adding a second WeakHashMap<V,Handle>.
 * 
 * Or maybe there's some simpler way that I haven't thought of.
 */
public abstract class ReferenceCache<K,V> implements Cache<K,V>
{
	protected WeakHashMap<Handle<K,V>,Reference<Handle<K,V>>> handles =
		new WeakHashMap<Handle<K,V>,Reference<Handle<K,V>>>();
	
	protected abstract <T> Reference<T> reference( T value );
	
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
			handles.put(tempHandle, reference(tempHandle));
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
