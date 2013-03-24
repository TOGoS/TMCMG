package togos.noise;

import togos.lang.SourceLocation;
import togos.lang.ScriptError;

/**
 * Completely generic interface to be implemented by anything that
 * can take program source as a string and produce a compiled object
 * that represents the program.
 */
public interface Compiler
{
	/**
	 * @param source
	 * @param loc
	 * @param scriptId a unique identifier for the script; may be null.
	 *   if non-null, may be used to cache already-compiled scripts.
	 * @param preferredType in cases where the object described by the
	 *   script may be interpreted different ways, this can be used as a hint to the compiler;
	 *   may be null.
	 * @return
	 */
	public Object compile( String source, SourceLocation loc, String scriptId, Class<?> preferredType ) throws ScriptError;
}
