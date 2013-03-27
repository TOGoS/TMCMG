package togos.noise.v3.program.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.BoundArgumentList;

public class ArgumentList extends ProgramNode
{
	static class Argument<V> {
		final String name;
		final Expression<V> value;
		
		public Argument( String name, Expression<V> value ) {
			this.name = name;
			this.value = value;
		}
	}
	 
	List<Argument<?>> arguments = new ArrayList<Argument<?>>();
	
	public ArgumentList( SourceLocation sLoc ) {
	    super(sLoc);
    }
	
	public <V,W> ArgumentList( Expression<V> arg0, Expression<W> arg1, SourceLocation sLoc ) {
		this(sLoc);
		arguments.add(new Argument<V>("",arg0));
		arguments.add(new Argument<W>("",arg1));
	}
	
	public <V> void add( String name, Expression<V> v ) {
		arguments.add( new Argument<V>(name,v) );
	}
	
	public <V> void add( Expression<V> v ) {
		add( "", v );
    }
	
	public BoundArgumentList evaluate( Map<String,Callable<?>> context ) {
		BoundArgumentList bal = new BoundArgumentList();
		for( Argument<?> a : arguments ) {
			bal.add( a.name, a.value.evaluate(context) );
		}
		return bal;
	}
	
	@Override public String toString() {
		String res = null;
		for( Argument<?> arg : arguments ) {
			res = res == null ? "" : res + ", ";
			res += arg.name.isEmpty() ? arg.value.toAtomicString() : arg.name + " @ " + arg.value.toAtomicString();
		}
		return res;
	}
}
