package togos.noise.v3.program.runtime;

import togos.lang.CompileError;

public interface Function<V>
{
	/**
	 * Should do any possible compile-time checks, then return
	 * a Binding.  Binding should be defined in terms of arguments
	 * so that they can be further compiled, later, rather than
	 * calculated immediately in this method.
	 */
	public Binding<? extends V> apply( BoundArgumentList args ) throws CompileError;
}
