package togos.noise2.lang;

public class TNLExpression
{
	public String sourceFilename = "(unknown)";
	public int sourceLine, sourceColumn;
	public LexicalScope scope;
	
	public TNLExpression( SourceLocation sloc, LexicalScope scope ) {
		this.sourceFilename = sloc.getSourceFilename();
		this.sourceLine = sloc.getSourceLineNumber();
		this.sourceColumn = sloc.getSourceColumnNumber();
		this.scope = scope;
	}
}
