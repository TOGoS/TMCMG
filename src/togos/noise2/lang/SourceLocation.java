package togos.noise2.lang;

public interface SourceLocation
{
	public String getSourceFilename();
	public int getSourceLineNumber();
	public int getSourceColumnNumber();
}
