package togos.noise.v3.program.structure;

import java.util.ArrayList;
import java.util.List;

import togos.lang.CompileError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.BoundArgumentList;
import togos.noise.v3.program.runtime.Context;

public class ArgumentList extends ProgramNode
{
	static class Argument<V> {
		public final String name;
		public final Expression<V> value;
		public final SourceLocation sLoc;
		
		public Argument( String name, Expression<V> value, SourceLocation sLoc ) {
			this.name = name;
			this.value = value;
			this.sLoc = sLoc;
		}
	}
	 
	List<Argument<?>> arguments = new ArrayList<Argument<?>>();
	public final SourceLocation callLocation;
	
	public ArgumentList( SourceLocation callLocation, SourceLocation sLoc ) {
	    super(sLoc);
	    this.callLocation = callLocation;
    }
	
	public <V,W> ArgumentList( Expression<V> arg0, Expression<W> arg1, SourceLocation callLocation, SourceLocation sLoc ) {
		this(callLocation, sLoc);
		arguments.add(new Argument<V>("",arg0,arg0.sLoc));
		arguments.add(new Argument<W>("",arg1,arg1.sLoc));
	}
	
	public <V> void add( String name, Expression<V> v, SourceLocation sLoc ) {
		arguments.add( new Argument<V>(name,v,sLoc) );
	}
	
	public <V> void add( Expression<V> v ) {
		add( "", v, v.sLoc );
    }
	
	public BoundArgumentList evaluate( Context context ) throws CompileError {
		BoundArgumentList bal = new BoundArgumentList( callLocation, sLoc );
		for( Argument<?> a : arguments ) {
			bal.add( a.name, a.value.bind(context), a.sLoc );
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
