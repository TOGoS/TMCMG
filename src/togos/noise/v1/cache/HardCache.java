package togos.noise.v1.cache;

import java.util.HashMap;

import togos.noise.v1.func.Function;

public class HardCache<K,V> implements Cache<K,V>
{
	protected HashMap<K,Handle<K,V>> handles = new HashMap<K,Handle<K,V>>();
	
	protected synchronized Handle<K,V> getHandle( K key ) {
		Handle<K,V> h = handles.get(key);
		if( h == null ) {
			h = new Handle<K,V>(key);
			handles.put(key,h);
		}
		return h;
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
