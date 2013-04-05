package togos.noise.v3.program.runtime;

import java.util.ArrayList;
import java.util.List;

import togos.lang.SourceLocation;

public class BoundArgumentList
{
	/** Location of the application argument list */
	public final SourceLocation callLocation;
	public final SourceLocation argListLocation;
	
	public class BoundArgument<V> {
		public final String name;
		public final Binding<V> value;
		public final SourceLocation sLoc; // In case of named arguments, may be different than value.sLoc!
		
		public BoundArgument( String name, Binding<V> value, SourceLocation sLoc ) {
			this.name = name;
			this.value = value;
			this.sLoc = sLoc;
		}
	}
	
	public final List<BoundArgument<?>> arguments = new ArrayList<BoundArgument<?>>();
	
	public BoundArgumentList( SourceLocation fcLoc, SourceLocation sLoc ) {
		this.callLocation = fcLoc;
		this.argListLocation = sLoc;
	}
	
	public <V> void add( String name, Binding<V> value, SourceLocation sLoc ) {
		arguments.add( new BoundArgument<V>(name, value, sLoc) );
	}
	
	public boolean hasNamedArguments() {
		for( BoundArgument<?> b : arguments ) {
			if( !b.name.isEmpty() ) return true;
		}
		return false;
	}
}
