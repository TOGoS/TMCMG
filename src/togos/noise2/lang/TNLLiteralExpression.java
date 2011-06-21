package togos.noise2.lang;

public class TNLLiteralExpression extends TNLExpression
{
	Object value;
	
	public TNLLiteralExpression( Object value, SourceLocation sloc, LexicalScope scope ) {
		super( sloc, scope );
		this.value = value;
	}
}
