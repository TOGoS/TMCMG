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
		final ValueNode<V> value;
		
		public Argument( String name, ValueNode<V> value ) {
			this.name = name;
			this.value = value;
		}
	}
	 
	List<Argument<?>> arguments = new ArrayList<Argument<?>>();
	
	public ArgumentList( SourceLocation sLoc ) {
	    super(sLoc);
    }
	
	public BoundArgumentList evaluate( Map<String,Callable<?>> context ) {
		BoundArgumentList bal = new BoundArgumentList();
		for( Argument<?> a : arguments ) {
			bal.add( a.name, a.value.evaluate(context) );
		}
		return bal;
	}
}
