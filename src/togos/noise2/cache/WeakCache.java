package togos.noise2.cache;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class WeakCache<K,V> extends ReferenceCache<K,V>
{
	@Override protected <T> Reference<T> reference( T value ) {
		return new WeakReference<T>( value );
    }
}
