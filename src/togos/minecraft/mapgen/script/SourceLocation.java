package togos.minecraft.mapgen.script;

public interface SourceLocation
{
	public String getSourceFilename();
	public int getSourceLineNumber();
	public int getSourceColumnNumber();
}
