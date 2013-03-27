package togos.noise.v3.program.runtime;

import java.util.concurrent.Callable;

import togos.lang.ScriptError;
import togos.lang.SourceLocation;

public abstract class ValueHandle<V> implements Callable<V>
{
	enum State { UNEVALUATED, EVALUATING, EVALUATED, ERRORED };
	
	public final SourceLocation sLoc;
	private V value;
	private State state = State.UNEVALUATED;
	private Exception error;
	
	public ValueHandle( SourceLocation sLoc ) {
		this.sLoc = sLoc;
	}
	
	protected abstract V evaluate() throws Exception;
	
	@Override
    public final V call() throws Exception {
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
