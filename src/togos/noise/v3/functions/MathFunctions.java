package togos.noise.v3.functions;

import togos.lang.BaseSourceLocation;
import togos.lang.CompileError;
import togos.noise.v1.func.D5_2Perlin;
import togos.noise.v1.func.SimplexNoise;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.BoundArgumentList;
import togos.noise.v3.program.runtime.BoundArgumentList.BoundArgument;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.runtime.Function;

/**
 * TODO: If this is specific to v3.program evaluation, move to that package.
 * Otherwise need to make sure constant names indicate which stage of parsing/program evaluation
 * they are applicable to.
 */
public class MathFunctions
{
	static final BaseSourceLocation BUILTIN_LOC = new BaseSourceLocation( MathFunctions.class.getName()+".java", 0, 0);
	public static final Context CONTEXT = new Context();
	
	static abstract class BuiltinFunction<R> implements Function<R> {
		protected final Class<? extends R> returnType;
		protected final Class<?>[] argumentTypes;
		protected final Object[] argumentDefaults;
		
		public BuiltinFunction(
			Class<? extends R> returnType,
			Class<?>[] argumentTypes,
			Object[] argumentDefaults
		) {
			assert argumentTypes != null;
			assert argumentDefaults != null;
			assert argumentTypes.length == argumentDefaults.length;
			
			this.returnType = returnType;
			this.argumentTypes = argumentTypes;
			this.argumentDefaults = argumentDefaults;
		}
		
		abstract String getName();
		abstract R apply( Object[] arguments );
		
		public Class<? extends R> getReturnType() { return returnType; }
		
        public Binding<R> apply( BoundArgumentList input ) throws CompileError {
			for( BoundArgument<?> arg : input.arguments ) {
				if( !arg.name.isEmpty() ) {
					throw new CompileError("+ takes no named arguments, but was given '"+arg.name+"'", arg.value.sLoc);
				}
			}
			final Binding<?>[] argumentBindings = new Binding[argumentTypes.length];
			for( int i=0; i<argumentTypes.length; ++i ) {
				if( input.arguments.size() < i ) {
					if( argumentDefaults[i] == null ) {
						throw new CompileError("Argument "+(i+1)+" required but not given for "+getName(), input.callLocation );
					} else {
						argumentBindings[i] = Binding.forValue( argumentDefaults[i], BUILTIN_LOC );
					}
				} else {
					argumentBindings[i] = input.arguments.get(i).value;
					if( argumentBindings[i].getValueType() != null ) {
						if( !argumentTypes[i].isAssignableFrom(argumentBindings[i].getValueType()) ) {
							throw new CompileError( argumentTypes[i]+" required but argument would return "+argumentBindings[i].getValueType(), argumentBindings[i].sLoc );
						}
					}
				}
			}
			return new Binding<R>( input.callLocation ) {
				@Override
                public boolean isConstant() throws CompileError {
					for( int i=0; i<argumentBindings.length; ++i ) {
						if( !argumentBindings[i].isConstant() ) return false;
					}
					return true; 
                }

				@Override
                public R getValue() throws Exception {
					Object[] arguments = new Object[argumentBindings.length];
					for( int i=0; i<arguments.length; ++i ) {
						arguments[i] = argumentBindings[i].getValue();
					}
					return apply( arguments );
                }
				
				@Override
                public Class<? extends R> getValueType() {
	                return returnType;
                }
			};
        }
	}
	
	static abstract class BooleanInputFunction<R> extends BuiltinFunction<R> {
		abstract R apply( boolean a, boolean b );
		
		protected static final Class<?>[] ARG_TYPES = { Boolean.class, Boolean.class };
		protected static final Object[] ARG_DEFAULTS = { null, null };

		public BooleanInputFunction( Class<? extends R> returnType ) {
			super(returnType, ARG_TYPES, ARG_DEFAULTS);
		}
		
		protected R apply( Object[] args ) {
			return apply( ((Boolean)args[0]).booleanValue(), ((Boolean)args[1]).booleanValue() );
		}
	}
	
	static abstract class NumberInputFunction<R> extends BuiltinFunction<R> {
		abstract R apply( double a, double b );
		
		protected static final Class<?>[] ARG_TYPES = { Number.class, Number.class };
		protected static final Object[] ARG_DEFAULTS = { null, null };

		public NumberInputFunction( Class<? extends R> returnType ) {
			super(returnType, ARG_TYPES, ARG_DEFAULTS);
		}
		
		protected R apply( Object[] args ) {
			return apply( ((Number)args[0]).doubleValue(), ((Number)args[1]).doubleValue() );
		}
	}

	static abstract class NoiseFunction extends BuiltinFunction<Number> {
		abstract double apply( double a, double b, double c );
		
		protected static final Class<?>[] ARG_TYPES = { Number.class, Number.class, Number.class };
		protected static final Object[] ARG_DEFAULTS = { null, null, null };

		public NoiseFunction() {
			super(Number.class, ARG_TYPES, ARG_DEFAULTS);
		}
		
		protected Double apply( Object[] args ) {
			return apply( ((Number)args[0]).doubleValue(), ((Number)args[1]).doubleValue(), ((Number)args[2]).doubleValue() );
		}
	}
	
	public static class ConstantBindingFunction<V> implements Function<V> {
		Binding<? extends V> v;
		
		public ConstantBindingFunction( Binding<? extends V> v ) {
			this.v = v;
		}

		@Override public Binding<? extends V> apply( BoundArgumentList input ) {
			return v;
        }
	}
	
	protected static <V> Binding<? extends V> builtinBinding( V v ) {
		return Binding.forValue( v, BUILTIN_LOC );
	}
	
	static {
		BooleanInputFunction<Boolean> logicalOr = new BooleanInputFunction<Boolean>( Boolean.class ) {
			@Override String getName() { return "or"; }
			@Override Boolean apply( boolean a, boolean b ) { return a || b; }
		};
		BooleanInputFunction<Boolean> logicalXor = new BooleanInputFunction<Boolean>( Boolean.class ) {
			@Override String getName() { return "xor"; }
			@Override Boolean apply( boolean a, boolean b ) { return (a && !b) || (b && !a); }
		};
		BooleanInputFunction<Boolean> logicalAnd = new BooleanInputFunction<Boolean>( Boolean.class ) {
			@Override String getName() { return "and"; }
			@Override Boolean apply( boolean a, boolean b ) { return a && b; }
		};
		
		CONTEXT.put("simplex", builtinBinding(new NoiseFunction() {
			@Override String getName() { return "simplex"; }
			
			ThreadLocal<SimplexNoise> simplex = new ThreadLocal<SimplexNoise>() {
				@Override public SimplexNoise initialValue() {
					return new SimplexNoise();
				}
			};
			
			@Override
			double apply(double a, double b, double c) {
				return simplex.get().apply((float)a, (float)b, (float)c);
			}
		}) );
		
		CONTEXT.put("simplex", builtinBinding(new NoiseFunction() {
			@Override String getName() { return "simplex"; }
			
			ThreadLocal<SimplexNoise> simplex = new ThreadLocal<SimplexNoise>() {
				@Override public SimplexNoise initialValue() {
					return new SimplexNoise();
				}
			};
			
			@Override
			double apply(double a, double b, double c) {
				return simplex.get().apply((float)a, (float)b, (float)c);
			}
		}) );
		CONTEXT.put("perlin", builtinBinding(new NoiseFunction() {
			@Override String getName() { return "perlin"; }
			
			@Override
			double apply(double a, double b, double c) {
				return D5_2Perlin.instance.get(a, b, c);
			}
		}) );
		
		CONTEXT.put("if", builtinBinding(new Function<Object>() {
			@Override public Binding<Object> apply(final BoundArgumentList input) throws CompileError {
				if( input.hasNamedArguments() ) throw new CompileError("'if' does not take named arguments", input.argListLocation);
				if( input.arguments.size() < 3 || input.arguments.size() % 2 == 0 ) {
					throw new CompileError("'if' requires an odd number of arguments >= 3", input.argListLocation);
				}
				return new Binding<Object>( input.callLocation ) {
					@Override
                    public boolean isConstant() throws CompileError {
						try {
							int i = 0;
							while( i <= input.arguments.size()-2 ) {
								Binding<? extends Boolean> condition = Binding.cast(input.arguments.get(i).value, Boolean.class);
								if( condition.isConstant() && condition.getValue().booleanValue() ) {
									Binding<?> value = input.arguments.get(i+1).value;
									return value.isConstant();
								}
								i += 2;
							}
						} catch( CompileError e ) {
							throw e;
						} catch( Exception e ) {
							throw new RuntimeException( e );
						}
						return false;
                    }
					
					@Override
                    public Object getValue() throws Exception {
						int i = 0;
						while( i <= input.arguments.size()-2 ) {
							Binding<? extends Boolean> condition = Binding.cast(input.arguments.get(i).value, Boolean.class);
							if( condition.getValue().booleanValue() ) {
								return input.arguments.get(i+1).value.getValue();
							}
							i += 2;
						}
						return input.arguments.get(i).value.getValue();
                    }
					
					@Override
                    public Class<? extends Object> getValueType() throws CompileError {
	                    return null;
                    }
				};
			}
		}));
		CONTEXT.put("&&",  builtinBinding(logicalAnd));
		CONTEXT.put("^^",  builtinBinding(logicalXor));
		CONTEXT.put("||",  builtinBinding(logicalOr));
		CONTEXT.put("and", builtinBinding(logicalAnd));
		CONTEXT.put("xor", builtinBinding(logicalXor));
		CONTEXT.put("or",  builtinBinding(logicalOr));
		
		CONTEXT.put("<<", builtinBinding(new NumberInputFunction<Long>( Long.class ) {
			@Override String getName() { return "<<"; }
			@Override Long apply( double a, double b ) { return (long)a << (long)b; }
		}));
		CONTEXT.put(">>", builtinBinding(new NumberInputFunction<Long>( Long.class ) {
			@Override String getName() { return "<<"; }
			@Override Long apply( double a, double b ) { return (long)a >> (long)b; }
		}));
		
		CONTEXT.put("|", builtinBinding(new NumberInputFunction<Long>( Long.class ) {
			@Override String getName() { return "|"; }
			@Override Long apply( double a, double b ) { return (long)a | (long)b; }
		}));
		CONTEXT.put("&", builtinBinding(new NumberInputFunction<Long>( Long.class ) {
			@Override String getName() { return "&"; }
			@Override Long apply( double a, double b ) { return (long)a & (long)b; }
		}));
		CONTEXT.put("^", builtinBinding(new NumberInputFunction<Long>( Long.class ) {
			@Override String getName() { return "^"; }
			@Override Long apply( double a, double b ) { return (long)a ^ (long)b; }
		}));
		CONTEXT.put(">", builtinBinding(new NumberInputFunction<Boolean>( Boolean.class ) {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a > b; }
		}));
		CONTEXT.put(">=", builtinBinding(new NumberInputFunction<Boolean>( Boolean.class ) {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a >= b; }
		}));
		CONTEXT.put("==", builtinBinding(new NumberInputFunction<Boolean>( Boolean.class ) {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a == b; }
		}));
		CONTEXT.put("<=", builtinBinding(new NumberInputFunction<Boolean>( Boolean.class ) {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a <= b; }
		}));
		CONTEXT.put("<", builtinBinding(new NumberInputFunction<Boolean>( Boolean.class ) {
			@Override String getName() { return "+"; }
			@Override Boolean apply( double a, double b ) { return a < b; }
		}));
		CONTEXT.put("+", builtinBinding(new NumberInputFunction<Double>( Double.class ) {
			@Override String getName() { return "+"; }
			@Override Double apply( double a, double b ) { return a + b; }
		}));
		CONTEXT.put("-", builtinBinding(new NumberInputFunction<Double>( Double.class ) {
			@Override String getName() { return "-"; }
			@Override Double apply( double a, double b ) { return a - b; }
		}));
		CONTEXT.put("*", builtinBinding(new NumberInputFunction<Double>( Double.class ) {
			@Override String getName() { return "*"; }
			@Override Double apply( double a, double b ) { return a * b; }
		}));
		CONTEXT.put("/", builtinBinding(new NumberInputFunction<Double>( Double.class ) {
			@Override String getName() { return "/"; }
			@Override Double apply( double a, double b ) { return a / b; }
		}));
		CONTEXT.put("**", builtinBinding(new NumberInputFunction<Double>( Double.class ) {
			@Override String getName() { return "*"; }
			@Override Double apply( double a, double b ) { return Math.pow(a, b); }
		}));
		
		CONTEXT.put("true", builtinBinding(Boolean.TRUE));
		CONTEXT.put("false", builtinBinding(Boolean.FALSE));
	}
}
