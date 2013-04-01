package togos.noise.v3.functions;

import togos.lang.RuntimeError;
import togos.noise.v1.lang.BaseSourceLocation;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.BoundArgumentList;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.runtime.Function;
import togos.noise.v3.program.runtime.BoundArgumentList.BoundArgument;

/**
 * TODO: If this is specific to v3.program evaluation, move to that package.
 * Otherwise need to make sure constant names indicate which stage of parsing/program evaluation
 * they are applicable to.
 */
public class MathFunctions
{
	static final BaseSourceLocation BUILTIN_LOC = new BaseSourceLocation("built-in math function", 0, 0);
	public static final Context CONTEXT = new Context();
	
	static abstract class BooleanInputFunction<R> implements Function<R> {
		abstract String getName();
		abstract R apply( boolean a, boolean b );
		
		@Override
        public Binding<R> apply( BoundArgumentList input ) throws Exception {
			if( input.arguments.size() != 2 ) {
				throw new RuntimeError( "Too many arguments (exactly 2 required but "+input.arguments.size()+" given) to "+getName(), input.sLoc );
			}
			
			int argCount = 0;
			boolean a=false, b=false;
			for( BoundArgument<?> arg : input.arguments ) {
				if( !arg.name.isEmpty() ) {
					throw new RuntimeError("+ takes no named arguments, but was given '"+arg.name+"'", arg.value.sLoc);
				}
				Object o = arg.value.getValue();
				if( !(o instanceof Boolean) ) {
					throw new RuntimeError("Non-boolean argument: "+o, arg.value.sLoc );
				}
				boolean argValue = ((Boolean)o).booleanValue();
				switch( argCount ) {
				case 0: a = argValue; break;
				case 1: b = argValue; break;
				default: throw new RuntimeError( "Too many arguments to "+getName(), arg.value.sLoc );
				}
				
				++argCount;
			}
			if( argCount < 2 ) {
				throw new RuntimeError( "Not enough arguments to "+getName(), input.sLoc );
			}
			return new Binding.Constant<R>( apply(a,b), BUILTIN_LOC );
        }
	}
	
	static abstract class NumberInputFunction<R> implements Function<R> {
		abstract String getName();
		abstract R apply( double a, double b );
		
		@Override
        public Binding<R> apply( BoundArgumentList input ) throws Exception {
			if( input.arguments.size() != 2 ) {
				throw new RuntimeError( "Too many arguments (exactly 2 required but "+input.arguments.size()+" given) to "+getName(), input.sLoc );
			}
			
			int argCount = 0;
			double a=Double.NaN, b=Double.NaN;
			for( BoundArgument<?> arg : input.arguments ) {
				if( !arg.name.isEmpty() ) {
					throw new RuntimeError("+ takes no named arguments, but was given '"+arg.name+"'", arg.value.sLoc);
				}
				Object o = arg.value.getValue();
				if( !(o instanceof Number) ) {
					throw new RuntimeError("Non-numeric argument: "+o, arg.value.sLoc );
				}
				double argValue = ((Number)o).doubleValue();
				switch( argCount ) {
				case 0: a = argValue; break;
				case 1: b = argValue; break;
				default: throw new RuntimeError( "Too many arguments to "+getName(), arg.value.sLoc );
				}
				
				++argCount;
			}
			if( argCount < 2 ) {
				throw new RuntimeError( "Not enough arguments to "+getName(), input.sLoc );
			}
			return new Binding.Constant<R>( apply(a,b), BUILTIN_LOC );
        }
	}
	
	protected static <V> Binding<V> builtinBinding( V v ) {
		return new Binding.Constant<V>( v, BUILTIN_LOC );
	}
	
	static {
		BooleanInputFunction<Boolean> logicalOr = new BooleanInputFunction<Boolean>() {
			@Override String getName() { return "or"; }
			@Override Boolean apply( boolean a, boolean b ) { return a || b; }
		};
		BooleanInputFunction<Boolean> logicalXor = new BooleanInputFunction<Boolean>() {
			@Override String getName() { return "xor"; }
			@Override Boolean apply( boolean a, boolean b ) { return (a && !b) || (b && !a); }
		};
		BooleanInputFunction<Boolean> logicalAnd = new BooleanInputFunction<Boolean>() {
			@Override String getName() { return "and"; }
			@Override Boolean apply( boolean a, boolean b ) { return a && b; }
		};
		
		CONTEXT.put("if", builtinBinding(new Function<Object>() {
			@Override public Binding<? extends Object> apply(BoundArgumentList input) throws Exception {
				if( input.hasNamedArguments() ) throw new RuntimeError("if does not take named arguments", input.sLoc);
				if( input.arguments.size() < 3 || input.arguments.size() % 2 == 0 ) {
					throw new RuntimeError("if requires an odd number of arguments >= 3", input.sLoc);
				}
				int i = 0;
				while( i <= input.arguments.size()-2 ) {
					Object condition = input.arguments.get(i).value.getValue();
					if( !(condition instanceof Boolean) ) {
						throw new RuntimeError("Boolean value expected for condition, but got a "+condition.getClass().getName(), input.arguments.get(i).sLoc);
					}
					if( ((Boolean)condition).booleanValue() ) {
						return input.arguments.get(i+1).value;
					}
					i += 2;
				}
				return input.arguments.get(i).value;
			}
		}));
		CONTEXT.put("&&",  builtinBinding(logicalAnd));
		CONTEXT.put("^^",  builtinBinding(logicalXor));
		CONTEXT.put("||",  builtinBinding(logicalOr));
		CONTEXT.put("and", builtinBinding(logicalAnd));
		CONTEXT.put("xor", builtinBinding(logicalXor));
		CONTEXT.put("or",  builtinBinding(logicalOr));
		
		CONTEXT.put("<<", builtinBinding(new NumberInputFunction<Long>() {
			@Override String getName() { return "<<"; }
			@Override Long apply( double a, double b ) { return (long)a << (long)b; }
		}));
		CONTEXT.put(">>", builtinBinding(new NumberInputFunction<Long>() {
			@Override String getName() { return "<<"; }
			@Override Long apply( double a, double b ) { return (long)a >> (long)b; }
		}));
		
		CONTEXT.put("|", builtinBinding(new NumberInputFunction<Long>() {
			@Override String getName() { return "|"; }
			@Override Long apply( double a, double b ) { return (long)a | (long)b; }
		}));
		CONTEXT.put("&", builtinBinding(new NumberInputFunction<Long>() {
			@Override String getName() { return "&"; }
			@Override Long apply( double a, double b ) { return (long)a & (long)b; }
		}));
		CONTEXT.put("^", builtinBinding(new NumberInputFunction<Long>() {
			@Override String getName() { return "^"; }
			@Override Long apply( double a, double b ) { return (long)a ^ (long)b; }
		}));
		CONTEXT.put(">", builtinBinding(new NumberInputFunction<Boolean>() {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a > b; }
		}));
		CONTEXT.put(">=", builtinBinding(new NumberInputFunction<Boolean>() {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a >= b; }
		}));
		CONTEXT.put("==", builtinBinding(new NumberInputFunction<Boolean>() {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a == b; }
		}));
		CONTEXT.put("<=", builtinBinding(new NumberInputFunction<Boolean>() {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a <= b; }
		}));
		CONTEXT.put("<", builtinBinding(new NumberInputFunction<Boolean>() {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a < b; }
		}));
		CONTEXT.put("+", builtinBinding(new NumberInputFunction<Double>() {
			@Override String getName() { return "+"; }
			@Override Double apply( double a, double b ) { return a + b; }
		}));
		CONTEXT.put("-", builtinBinding(new NumberInputFunction<Double>() {
			@Override String getName() { return "-"; }
			@Override Double apply( double a, double b ) { return a - b; }
		}));
		CONTEXT.put("*", builtinBinding(new NumberInputFunction<Double>() {
			@Override String getName() { return "*"; }
			@Override Double apply( double a, double b ) { return a * b; }
		}));
		CONTEXT.put("/", builtinBinding(new NumberInputFunction<Double>() {
			@Override String getName() { return "/"; }
			@Override Double apply( double a, double b ) { return a / b; }
		}));
		CONTEXT.put("**", builtinBinding(new NumberInputFunction<Double>() {
			@Override String getName() { return "*"; }
			@Override Double apply( double a, double b ) { return Math.pow(a, b); }
		}));
		
		CONTEXT.put("true", builtinBinding(Boolean.TRUE));
		CONTEXT.put("false", builtinBinding(Boolean.FALSE));
	}
}
