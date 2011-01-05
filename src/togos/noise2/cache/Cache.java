package togos.noise2.cache;

import togos.noise2.function.FunctionO_O;

public interface Cache
{
	public Object get( Object key );
	public Object get( Object key, FunctionO_O generator );
	public void put( Object key, Object value );
}
