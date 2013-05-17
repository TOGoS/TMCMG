package togos.noise.v3.functions;

import togos.noise.v3.parser.Parser;
import togos.noise.v3.program.runtime.Function;

public abstract class BuiltinFunction<R> implements Function<R>
{
	public abstract String getName();
	
	public String getCalculationId() {
		return "builtin-function(" + Parser.quote(getName()) + ", "+Parser.quote(super.toString()) + ")";
	}
	
	public String toString() {
		return getCalculationId();
	}
}
