package togos.noise.v3.program.runtime;

import togos.lang.BaseSourceLocation;
import togos.lang.CompileError;
import togos.lang.RuntimeError;
import togos.lang.SourceLocation;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.program.compiler.ExpressionVectorProgramCompiler;
import togos.noise.v3.program.compiler.UnvectorizableError;
import togos.noise.v3.vector.vm.Program.RegisterID;

/**
 * Represents the result of applying an expression with a context.
 */
public abstract class Binding<V>
{
	enum EvaluationState { UNEVALUATED, EVALUATING, EVALUATED, ERRORED };
	
	public static <V> Binding<V> forValue( V v, Class<V> valueType, SourceLocation sLoc ) {
		return new Binding.Constant<V>( v, valueType, sLoc );
	}
	
	public static <V> Binding<? extends V> forValue( V v, SourceLocation sLoc ) {
		return new Binding.Constant<V>( v, (Class<? extends V>)v.getClass(), sLoc );
	}
	
	public static <T> Binding<T> memoize(Binding<T> binding) {
		return new Binding.Memoizing<T>( binding, binding.sLoc );
	}
	
	public static <V> Binding<V> cast( final Binding<?> b, final Class<V> targetClass ) throws CompileError {
		if( b.getValueType() == null ) {
			return Binding.memoize(new Binding<V>( b.sLoc ) {
				@Override public boolean isConstant() throws CompileError {
					return b.isConstant();
                }
				
                @Override public V getValue() throws Exception {
					Object v = b.getValue();
					try {
						return targetClass.cast(v);
					} catch( ClassCastException e ) {
						throw new RuntimeError(targetClass+" required, but expression returned "+v.getClass(), b.sLoc);
					}
                }

				@Override public Class<? extends V> getValueType() {
	                return targetClass;
                }
				
				@Override public RegisterID<?> toVectorProgram(
					ExpressionVectorProgramCompiler compiler
				) throws CompileError {
					return compiler.compile(b);
				}
				
				@Override public String toSource() throws CompileError {
					return "cast("+b.toSource()+", '"+targetClass.getName()+"'";
				}
			});
		} else if( targetClass.isAssignableFrom(b.getValueType()) ) {
			return (Binding<V>)b;
		} else {
			throw new CompileError(targetClass+" required, but expression returns "+b.getValueType(), b.sLoc);
		}
	}
	
	////
	
	public static class Variable<V> extends Binding<V> {
		public final String variableId;
		protected final Class<? extends V> type;
		public Variable( String variableId, Class<? extends V> type ) {
			super( BaseSourceLocation.NONE );
			this.variableId = variableId;
			this.type = type;
		}
		
		@Override public boolean isConstant() {
			return false;
		}
		
		@Override public V getValue() {
			throw new RuntimeException("Cannot getValue "+variableId+"; it is a variable");
		}
		
		@Override public Class<? extends V> getValueType() {
			return type;
		}
		
		@Override public RegisterID<?> toVectorProgram( ExpressionVectorProgramCompiler compiler ) throws CompileError {
			return compiler.getVariableRegister(variableId);
		}
		
		@Override public String toSource() {
			return "variable('"+variableId+"')";
		}
	}
	
	public static class Constant<V> extends Binding<V> {
		protected final Class<? extends V> type;
		
		private V value;
		
		public Constant( V value, Class<? extends V> type, SourceLocation sLoc ) {
			super( sLoc );
			this.type = type;
			this.value = value;
		}
		
		@Override public V getValue() throws Exception {
			return value;
		}
		
		@Override public boolean isConstant() {
			return true;
		}
		
		@Override public Class<? extends V> getValueType() {
			return type;
		}
		
		@Override public String toSource() {
			return Parser.toLiteral(value);
		}
		
		@Override public RegisterID<?> toVectorProgram(
			ExpressionVectorProgramCompiler compiler
		) throws CompileError {
			return compiler.compileConstant(value, sLoc);
		}
	}
	
	/**
	 * Used to wrap a binding so that the source location
	 * points to another expression.
	 */
	public static class Delegated<V> extends Binding<V> {
		protected Binding<? extends V> delegate;
		
		public Delegated( SourceLocation sLoc ) {
			super(sLoc);
		}
		public Delegated( Binding<? extends V> delegate, SourceLocation sLoc ) {
			super(sLoc);
			this.delegate = delegate;
		}
		
		@Override public boolean isConstant() throws CompileError {
			return delegate.isConstant();
		}
		
		@Override public V getValue() throws Exception {
			return delegate.getValue();
		}
		
		@Override public Class<? extends V> getValueType() throws CompileError {
			return delegate.getValueType();
		}
		
		@Override public String toSource() throws CompileError {
			return delegate.toSource();
		}
	}
	
	/**
	 * Similar to Delegated but doesn't even generate the delegated
	 * binding until it is needed.  This is used when constructing
	 * blocks since the name -> Binding table may not be completely
	 * set up when the outer binding is created.
	 */
	public static abstract class Deferred<V> extends Binding<V> {
		protected Binding<? extends V> delegate;
		private EvaluationState state = EvaluationState.UNEVALUATED;
		protected CompileError error;
		
		public Deferred( SourceLocation sLoc ) {
			super(sLoc);
		}
		
		protected abstract Binding<? extends V> generateDelegate() throws CompileError;
		protected final Binding<? extends V> getDelegate() throws CompileError {
			switch( state ) {
			case UNEVALUATED:
				state = EvaluationState.EVALUATING;
				try {
					delegate = generateDelegate();
					state = EvaluationState.EVALUATED;
				} catch( CompileError e ) {
					state = EvaluationState.ERRORED;
					error = e;
					throw error;
				}
				return delegate;
			case EVALUATING:
				throw new CompileError( "Circular definition encountered", sLoc );
			case EVALUATED:
				return delegate;
			case ERRORED:
				throw error;
			default:
				throw new RuntimeException("Invalid state: "+state);
			}
		}
		
		@Override public boolean isConstant() throws CompileError {
			return getDelegate().isConstant();
		}
		
		@Override public V getValue() throws Exception {
			return getDelegate().getValue();
		}
		
		@Override public Class<? extends V> getValueType() throws CompileError {
			return getDelegate().getValueType();
		}
		
		@Override public String toSource() throws CompileError {
			return getDelegate().toSource();
		}
		
		@Override public RegisterID<?> toVectorProgram(
			ExpressionVectorProgramCompiler compiler
		) throws CompileError {
			return getDelegate().toVectorProgram(compiler);
		}
	}
	
	/**
	 * Wraps another binding so that its isConstant, getValue, and getValueType
	 * fields will only ever need to be called once.
	 * @author stevens
	 *
	 * @param <T>
	 */
	public static class Memoizing<T> extends Binding<T> {
		protected EvaluationState isConstantState = EvaluationState.UNEVALUATED;
		protected EvaluationState valueState      = EvaluationState.UNEVALUATED;
		protected EvaluationState valueTypeState  = EvaluationState.UNEVALUATED;
		protected EvaluationState toSourceState = EvaluationState.UNEVALUATED;
		
		protected CompileError isConstantError;
		protected Exception valueError;
		protected CompileError valueTypeError;
		protected CompileError toSourceError;
		
		protected boolean isConstant;
		protected T value;
		protected Class<? extends T> valueType;
		protected String source;
		
		final Binding<T> delegate;
		
		public Memoizing( Binding<T> other, SourceLocation sLoc ) {
			super( sLoc );
			this.delegate = other;
		}
		
		@Override
		public boolean isConstant() throws CompileError {
			switch( isConstantState ) {
			case UNEVALUATED:
				isConstantState = EvaluationState.EVALUATING;
				try {
					isConstant = delegate.isConstant();
					isConstantState = EvaluationState.EVALUATED;
				} catch( CompileError e ) {
					isConstantState = EvaluationState.ERRORED;
					isConstantError = e;
					throw isConstantError;
				}
				return isConstant;
			case EVALUATING:
				throw new CompileError( "Circular definition encountered", sLoc );
			case EVALUATED:
				return isConstant;
			case ERRORED:
				throw isConstantError;
			default:
				throw new RuntimeException("Invalid state: "+isConstantState);
			}
		}
		
		@Override
		public T getValue() throws Exception {
			switch( valueState ) {
			case UNEVALUATED:
				valueState = EvaluationState.EVALUATING;
				try {
					value = delegate.getValue();
					valueState = EvaluationState.EVALUATED;
				} catch( CompileError e ) {
					valueState = EvaluationState.ERRORED;
					valueError = e;
					throw valueError;
				}
				return value;
			case EVALUATING:
				throw new CompileError( "Circular definition encountered", sLoc );
			case EVALUATED:
				return value;
			case ERRORED:
				throw valueError;
			default:
				throw new RuntimeException("Invalid state: "+valueState);
			}
		}
		
		@Override
		public Class<? extends T> getValueType() throws CompileError {
			switch( valueTypeState ) {
			case UNEVALUATED:
				valueTypeState = EvaluationState.EVALUATING;
				try {
					valueType = delegate.getValueType();
					valueTypeState = EvaluationState.EVALUATED;
				} catch( CompileError e ) {
					valueTypeState = EvaluationState.ERRORED;
					valueTypeError = e;
					throw valueTypeError;
				}
				return valueType;
			case EVALUATING:
				throw new CompileError( "Circular definition encountered", sLoc );
			case EVALUATED:
				return valueType;
			case ERRORED:
				throw valueTypeError;
			default:
				throw new RuntimeException("Invalid state: "+valueTypeState);
			}
		}
		
		@Override
		public RegisterID<?> toVectorProgram( ExpressionVectorProgramCompiler compiler ) throws CompileError {
			// Don't memoize because different compilers may be passed in,
			// and the compiler will optimize out duplicate code, anyway.
			return compiler.compile(delegate);
		}
		
		@Override
		public String toSource() throws CompileError {
			switch( toSourceState ) {
			case UNEVALUATED:
				isConstantState = EvaluationState.EVALUATING;
				try {
					source = delegate.toSource();
					toSourceState = EvaluationState.EVALUATED;
				} catch( CompileError e ) {
					toSourceState = EvaluationState.ERRORED;
					toSourceError = e;
					throw e;
				}
				return source;
			case EVALUATING:
				throw new CompileError( "Circular definition encountered", sLoc );
			case EVALUATED:
				return source;
			case ERRORED:
				throw toSourceError;
			default:
				throw new RuntimeException("Invalid state: "+isConstantState);
			}
		}
	}
	
	////
	
	public final SourceLocation sLoc;
	
	public Binding( SourceLocation sLoc ) {
		this.sLoc = sLoc;
    }
	
	public abstract boolean isConstant() throws CompileError;
	public abstract V getValue() throws Exception;
	public abstract Class<? extends V> getValueType() throws CompileError;
	
	public abstract String toSource() throws CompileError;
	
	public RegisterID<?> toVectorProgram(
		ExpressionVectorProgramCompiler compiler
	) throws CompileError {
		throw new UnvectorizableError("toVectorProgram not supported for "+getClass(), sLoc);
	}
}
