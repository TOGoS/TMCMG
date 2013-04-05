package togos.noise.v3.program.runtime;

import togos.lang.BaseSourceLocation;
import togos.lang.CompileError;
import togos.lang.RuntimeError;
import togos.lang.SourceLocation;

/**
 * Represents the result of applying an expression with a context.
 */
public abstract class Binding<V>
{
	public static <V> Binding<V> forValue( V v, Class<V> valueType, SourceLocation sLoc ) {
		return new Binding.Constant<V>( v, valueType, sLoc );
	}

	public static <V> Binding<? extends V> forValue( V v, SourceLocation sLoc ) {
		return new Binding.Constant<V>( v, (Class<? extends V>)v.getClass(), sLoc );
	}
	
	public static <V> Binding<V> cast( final Binding<?> b, final Class<V> targetClass ) throws CompileError {
		if( b.getValueType() == null ) {
			return new Binding<V>( b.sLoc ) {
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

				@Override
                public Class<? extends V> getValueType() {
	                return targetClass;
                }
			};
		} else if( targetClass.isAssignableFrom(b.getValueType()) ) {
			return (Binding<V>)b;
		} else {
			throw new CompileError(targetClass+" required, but expression returns "+b.getValueType(), b.sLoc);
		}
	}
	
	public abstract boolean isConstant() throws CompileError;
	public abstract V getValue() throws Exception;
	public abstract Class<? extends V> getValueType() throws CompileError;
	public final SourceLocation sLoc;
	
	public Binding( SourceLocation sLoc ) {
		this.sLoc = sLoc;
    }
	
	public static class Variable<ID, V> extends Binding<V> {
		public final ID id;
		protected final Class<? extends V> type;
		public Variable( ID id, Class<? extends V> type ) {
			super( BaseSourceLocation.NONE );
			this.id = id;
			this.type = type;
		}
		public boolean isConstant() {
			return false;
		}
		public V getValue() {
			throw new RuntimeException("Cannot evaluate "+id+"; it is a variable");
		}
		public Class<? extends V> getValueType() {
			return type;
		}
	}
	
	public static class Constant<V> extends Binding<V> {
		enum State { UNEVALUATED, EVALUATING, EVALUATED, ERRORED };
		
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
		
		public boolean isConstant() throws CompileError {
			return delegate.isConstant();
		}
		
		public V getValue() throws Exception {
			return delegate.getValue();
		}
		
		public Class<? extends V> getValueType() throws CompileError {
			return delegate.getValueType();
		}
	}
	
	/**
	 * Similar to Delegated but doesn't even generate the delegated
	 * binding until it is needed.  This is used when constructing
	 * blocks since the name -> Binding table may not be completely
	 * set up when the outer binding is created.
	 */
	public static abstract class Deferred<V> extends Binding<V> {
		enum State {
			UNEVALUATED, EVALUATING, EVALUATED, ERRORED;
		}
		
		protected Binding<? extends V> delegate;
		private State state = State.UNEVALUATED;
		protected CompileError error;
		
		public Deferred( SourceLocation sLoc ) {
			super(sLoc);
		}
		
		protected abstract Binding<? extends V> generateDelegate() throws CompileError;
		protected final Binding<? extends V> getDelegate() throws CompileError {
			switch( state ) {
			case UNEVALUATED:
				state = State.EVALUATING;
				try {
					delegate = generateDelegate();
					state = State.EVALUATED;
				} catch( CompileError e ) {
					state = State.ERRORED;
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
				throw new RuntimeException("Invalid ValueHandle state: "+state);
			}
		}
		
		public boolean isConstant() throws CompileError {
			return getDelegate().isConstant();
		}
		
		public V getValue() throws Exception {
			return getDelegate().getValue();
		}
		
		public Class<? extends V> getValueType() throws CompileError {
			return getDelegate().getValueType();
		}
	}
}
