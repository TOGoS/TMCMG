package togos.noise2.cache;

import togos.noise2.vm.dftree.func.Function;

public interface Cache<Key,Value>
{
	public Value get( Key key );
	public Value get( Key key, Function<Key,Value> generator );
	public void put( Key key, Value value );
}
