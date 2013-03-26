package togos.noise.v3.program.structure;

import java.util.Map;
import java.util.concurrent.Callable;

import togos.lang.ScriptError;
import togos.lang.SourceLocation;
import togos.noise.v3.program.runtime.Closure;

public class FunctionApplication extends ValueNode<Object>
{
	final ValueNode<?> function;
	final ArgumentList argumentList;
	
	public FunctionApplication( ValueNode<?> function, ArgumentList argumentList, SourceLocation sLoc ) {
	    super(sLoc);
	    this.function = function;
	    this.argumentList = argumentList;
    }

	@Override
    public Callable<Object> evaluate( final Map<String, Callable<?>> context ) {
		return new Callable<Object>() {
			public Object call() throws Exception {
				Object funObj = function.evaluate(context).call();;
				if( !(funObj instanceof Closure) ) {
					throw new ScriptError("Function returned by "+function+" is not a closure, but a "+funObj.getClass(), sLoc);
				}
				Closure<?> c = (Closure<?>)funObj;
				return c.apply( argumentList.evaluate(context) ).call();
			}
		};
    }
}
