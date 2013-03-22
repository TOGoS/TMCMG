package togos.noise3.asyncstream;

import java.util.ArrayList;
import java.util.Collection;

public class Collector<T> implements StreamDestination<T>
{
	public final Collection<T> collection;
	
	public Collector( Collection<T> c ) {
		this.collection = c;
	}
	
	public Collector() {
		this( new ArrayList<T>() );
	}

	@Override public void data( T value ) { collection.add(value); }
	@Override public void end() {}
}
