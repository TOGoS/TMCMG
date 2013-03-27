package togos.noise.v3.program.structure;

import java.util.ArrayList;
import java.util.List;

import togos.lang.SourceLocation;

public class ParameterList extends ProgramNode
{
	static class Parameter<V> {
		final String name;
		final boolean slurpy;
		final Expression<V> defaultValue;
		
		public Parameter( String name, boolean slurpy, Expression<V> defaultValue ) {
			this.name = name;
			this.slurpy = slurpy;
			this.defaultValue = defaultValue;
		}
	}
	 
	List<Parameter<?>> arguments = new ArrayList<Parameter<?>>();
	
	public ParameterList( SourceLocation sLoc ) {
	    super(sLoc);
    }
	
	public <T> void add( String name, boolean slurpy, Expression<T> defaultValue ) {
		arguments.add( new Parameter<T>(name, slurpy, defaultValue) );
	}
}
