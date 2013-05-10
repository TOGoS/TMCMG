package togos.noise.v3.program.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import togos.lang.CompileError;
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

		public String toSource() throws CompileError {
			String s = (name.length() > 0) ? name + " @ " : "";
			s += value.toSource();
			return s;
		}
	}
	
	public final List<BoundArgument<?>> arguments = new ArrayList<BoundArgument<?>>();
	
	public BoundArgumentList( SourceLocation callLocation, SourceLocation argListLocation ) {
		this.callLocation = callLocation;
		this.argListLocation = argListLocation;
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

	public String toSource() throws CompileError {
		String s = "";
		for( BoundArgument<?> ba : arguments ) {
			if( s.length() > 0 ) s += ", ";
			s += ba.toSource();
		}
		return s;
	}

	public Collection<Binding<?>> getArgumentBindings() {
		ArrayList<Binding<?>> dependencies = new ArrayList<Binding<?>>();
		for( BoundArgument<?> bArg : arguments ) {
			dependencies.add( bArg.value );
		}
		return dependencies;
    }
}
