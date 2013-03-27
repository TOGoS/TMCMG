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
	
	public <T,U> ArgumentList( Expression<T> arg0, Expression<U> arg1, SourceLocation sLoc ) {
		this(sLoc);
		arguments.add(new Argument<T>("",arg0));
		arguments.add(new Argument<U>("",arg1));
	}
	
	public BoundArgumentList evaluate( Map<String,Callable<?>> context ) {
		BoundArgumentList bal = new BoundArgumentList();
		for( Argument<?> a : arguments ) {
			bal.add( a.name, a.value.evaluate(context) );
		}
		return bal;
	}
}
