package togos.noise.v3.program.runtime;

import togos.lang.ScriptError;
import togos.lang.SourceLocation;

public abstract class Binding<V>
{
	public abstract boolean isConstant() throws Exception;
	public abstract V getValue() throws Exception;
	
	public static class Variable<ID, V> extends Binding<V> {
		public final ID id;
		public Variable( ID id ) {
			this.id = id;
		}
		public boolean isConstant() {
			return false;
		}
		public V getValue() {
			throw new RuntimeException("Cannot evaluate "+id+"; it is a variable");
		}
	}
	
	public static abstract class Constant<V> extends Binding<V> {
		enum State { UNEVALUATED, EVALUATING, EVALUATED, ERRORED };
		
		public final SourceLocation sLoc;
		private V value;
		private State state = State.UNEVALUATED;
		private Exception error;
		
		public Constant( SourceLocation sLoc ) {
			this.sLoc = sLoc;
		}
		
		protected abstract V evaluate() throws Exception;
		
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
		protected Binding<V> delegate;
		protected abstract Binding<V> generateDelegate() throws Exception;
		protected final Binding<V> getDelegate() throws Exception {
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
