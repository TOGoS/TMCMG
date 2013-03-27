package togos.noise.v3.program.structure;

import togos.lang.ScriptError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Closure;
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
    public Binding<Object> evaluate( final Context context ) {
		return new Binding.Delegated<Object>() {
            public Binding<Object> generateDelegate() throws Exception {
				Object funObj = function.evaluate(context).getValue();
				if( !(funObj instanceof Closure) ) {
					throw new ScriptError("Function returned by "+function+" is not a closure, but a "+funObj.getClass(), sLoc);
				}
				@SuppressWarnings("unchecked")
                Closure<Object> c = (Closure<Object>)funObj;
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
