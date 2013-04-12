package togos.noise.v3.vector.util;

import java.lang.ref.SoftReference;

public class SoftThreadLocalVectorBuffer<T extends HasMaxVectorSize>
{
	final ThreadLocal<SoftReference<T>> var = new ThreadLocal<SoftReference<T>>();
	
	public T initialValue( int vectorSize ) {
		return null;
	}
	
	public T get(int vectorSize) {
		T v;
		SoftReference<T> ref = var.get();
		if( ref == null || (v = ref.get()) == null || v.getMaxVectorSize() < vectorSize ) {
			v = initialValue(vectorSize);
			if( v == null ) return null;
			var.set(new SoftReference<T>(v));
		}
		return v;
	}
}
