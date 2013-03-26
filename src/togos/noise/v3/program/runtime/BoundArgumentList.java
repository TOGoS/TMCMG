package togos.noise.v3.program.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class BoundArgumentList
{
	public class BoundArgument<V> {
		public final String name;
		public final Callable<V> value;
		
		public BoundArgument( String name, Callable<V> value ) {
			this.name = name;
			this.value = value;
		}
	}
	
	public List<BoundArgument<?>> values = new ArrayList<BoundArgument<?>>();
	public <V> void add( String name, Callable<V> value ) {
		values.add( new BoundArgument<V>(name,value) );
	}
}
