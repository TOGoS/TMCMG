package togos.noise.v3.program.runtime;

import togos.lang.ScriptError;
import togos.lang.SourceLocation;
import togos.noise.v1.lang.BaseSourceLocation;

public abstract class Binding<V>
{
	public abstract boolean isConstant() throws Exception;
	public abstract V getValue() throws Exception;
	public final SourceLocation sLoc;
	
	public Binding( SourceLocation sLoc ) {
		this.sLoc = sLoc;
    }
	
	public static class Variable<ID, V> extends Binding<V> {
		public final ID id;
		public Variable( ID id ) {
			super( BaseSourceLocation.NONE );
			this.id = id;
		}
		public boolean isConstant() {
			return false;
		}
		public V getValue() {
			throw new RuntimeException("Cannot evaluate "+id+"; it is a variable");
		}
	}
	
	public static class Constant<V> extends Binding<V> {
		enum State { UNEVALUATED, EVALUATING, EVALUATED, ERRORED };
		
		private V value;
		private State state = State.UNEVALUATED;
		private Exception error;
		
		public Constant( V value, SourceLocation sLoc ) {
			super( sLoc );
			this.value = value;
			this.state = State.EVALUATED;
		}
		public Constant( SourceLocation sLoc ) {
			super( sLoc );
		}
		
		protected V evaluate() throws Exception {
			throw new RuntimeException("Not implemented");
		}
		
		public boolean isConstant() {
			return true;
		}
		
	    public final V getValue() throws Exception {
			switch( state ) {
			case UNEVALUATED:
				state = State.EVALUATING;
				try {
					value = evaluate();
					state = State.EVALUATED;
				} catch( Exception e ) {
					state = State.ERRORED;
					error = e;
					throw error;
				}
				return value;
			case EVALUATING:
				throw new ScriptError( "Circular definition encountered", sLoc );
			case EVALUATED:
				return value;
			case ERRORED:
				throw error;
			default:
				throw new RuntimeException("Invalid ValueHandle state: "+state);
			}
	    }
	}
	
	public static abstract class Delegated<V> extends Binding<V> {
		protected Binding<? extends V> delegate;
		
		public Delegated( SourceLocation sLoc ) {
			super(sLoc);
		}
		
		protected abstract Binding<? extends V> generateDelegate() throws Exception;
		protected final Binding<? extends V> getDelegate() throws Exception {
			if( delegate == null ) delegate = generateDelegate();
			return delegate;
		}
		
		public boolean isConstant() throws Exception {
			return getDelegate().isConstant();
		}
		
		public V getValue() throws Exception {
			return getDelegate().getValue();
		}
	}
}
