package togos.noise.v3.program.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import togos.lang.CompileError;
import togos.lang.SourceLocation;

public class ParameterList extends ProgramNode
{
	public static class Parameter<V> {
		public final String name;
		public final boolean slurpy;
		public final Expression<V> defaultValue;
		public final SourceLocation sLoc;
		
		public Parameter( String name, boolean slurpy, Expression<V> defaultValue, SourceLocation sLoc ) {
			this.name = name;
			this.slurpy = slurpy;
			this.defaultValue = defaultValue;
			this.sLoc = sLoc;
		}
	}
	 
	public final List<Parameter<?>> parameters = new ArrayList<Parameter<?>>();
	
	public ParameterList( SourceLocation sLoc ) {
	    super(sLoc);
    }
	
	public <T> void add( String name, boolean slurpy, Expression<T> defaultValue, SourceLocation sLoc ) {
		parameters.add( new Parameter<T>(name, slurpy, defaultValue, sLoc) );
	}
	
	public void validate() throws CompileError {
		HashSet<String> names = new HashSet<String>();
		for( Parameter<?> p : parameters ) {
			if( names.contains(p.name) ) {
				throw new CompileError("Parameter '"+p.name+"' appears multiple times", p.sLoc);
			}
			names.add(p.name);
		}
	}
	
	public Map<String,Parameter<?>> getParameterMap() {
		HashMap<String,Parameter<?>> params = new HashMap<String,Parameter<?>>();
		for( Parameter<?> p : parameters ) params.put(p.name, p);
		return params;
	}
	
	public String toString() {
		String r = null;
		for( Parameter<?> p : parameters ) {
			r = r == null ? "" : r + ", ";
			r += p.name;
			if( p.slurpy ) r += "...";
			if( p.defaultValue != null ) {
				r += " @ " + p.defaultValue.toAtomicString();
			}
		}
		return r;
	}
	
	public String toAtomicString() {
		return "(" + toString() + ")";
	}
}
