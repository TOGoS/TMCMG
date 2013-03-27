package togos.noise.v3.program.structure;

import togos.lang.ScriptError;
import togos.lang.SourceLocation;
import togos.noise.Function;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.BoundArgumentList;
import togos.noise.v3.program.runtime.Context;

public class FunctionApplication extends Expression<Object>
{
	final Expression<?> function;
	final ArgumentList argumentList;
	
	public FunctionApplication( Expression<?> function, ArgumentList argumentList, SourceLocation sLoc ) {
	    super(sLoc);
	    this.function = function;
	    this.argumentList = argumentList;
    }
	
	@Override
    public Binding<Object> bind( final Context context ) {
		return new Binding.Delegated<Object>( sLoc ) {
            public Binding<?> generateDelegate() throws Exception {
				Object funObj = function.bind(context).getValue();
				if( !(funObj instanceof Function ) ) {
					throw new ScriptError("Function returned by "+function+" is not a closure, but a "+funObj.getClass(), sLoc);
				}
				@SuppressWarnings("unchecked")
				Function<BoundArgumentList,Binding<?>> c = (Function<BoundArgumentList,Binding<?>>)funObj;
				return c.apply( argumentList.evaluate(context) );
			}
		};
    }
	
	@Override public String toString() {
		return function.toAtomicString() + "(" + argumentList.toString() + ")";
	}
	@Override public String toAtomicString() {
		return toString();
	}
}
