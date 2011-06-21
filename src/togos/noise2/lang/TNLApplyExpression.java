package togos.noise2.lang;

import java.util.List;

public class TNLApplyExpression extends TNLExpression
{
	public TNLExpression functionExpression;
	public List argumentExpressions;
	
	public TNLApplyExpression( TNLExpression functionExpression, List argumentExpressions, SourceLocation sloc, LexicalScope scope ) {
		super( sloc, scope );
		this.functionExpression = functionExpression;
		this.argumentExpressions = argumentExpressions;
	}
}
