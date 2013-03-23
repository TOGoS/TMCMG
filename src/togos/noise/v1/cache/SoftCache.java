package togos.noise.v1.cache;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;

public class SoftCache<K,V> extends ReferenceCache<K,V>
{
	@Override protected <T> Reference<T> reference( T value ) {
		return new SoftReference<T>( value );
    }
}
