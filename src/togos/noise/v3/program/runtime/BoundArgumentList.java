package togos.noise.v3.program.runtime;

import java.util.ArrayList;
import java.util.List;

import togos.lang.SourceLocation;

public class BoundArgumentList
{
	/** Location of the application argument list */
	public final SourceLocation sLoc;
	
	public class BoundArgument<V> {
		public final String name;
		public final Binding<V> value;
		
		public BoundArgument( String name, Binding<V> value ) {
			this.name = name;
			this.value = value;
		}
	}
	
	public final List<BoundArgument<?>> arguments = new ArrayList<BoundArgument<?>>();
	
	public BoundArgumentList( SourceLocation sLoc ) {
		this.sLoc = sLoc;
	}
	
	public <V> void add( String name, Binding<V> value ) {
		arguments.add( new BoundArgument<V>(name,value) );
	}
}
