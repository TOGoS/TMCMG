package togos.noise2.lang;

/**
 * Implemented by anything that knows the location in source
 * code that described it/that it was read from.
 */
public interface SourceLocation
{
	public String getSourceFilename();
	public int getSourceLineNumber();
	public int getSourceColumnNumber();
}
