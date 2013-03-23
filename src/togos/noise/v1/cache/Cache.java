package togos.noise.v1.cache;

import togos.noise.v1.func.Function;

public interface Cache<Key,Value>
{
	public Value get( Key key );
	public Value get( Key key, Function<Key,Value> generator );
	public void put( Key key, Value value );
}
