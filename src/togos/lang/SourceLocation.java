package togos.lang;

/**
 * Implemented by anything that knows the location in source
 * code that described it/that it was read from.
 */
public interface SourceLocation
{
	/** Usually a filename or URI or "(no source)" */
	public String getSourceFilename();
	/** 0 for unknown; the first line of a file is line 1 */
	public int getSourceLineNumber();
	/** 0 for unknown; the first character on a line is in column 1 */
	public int getSourceColumnNumber();
}
