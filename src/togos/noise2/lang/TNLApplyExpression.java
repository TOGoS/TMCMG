package togos.noise2.lang;

import java.util.List;

public class TNLApplyExpression extends TNLExpression
{
	public TNLExpression functionExpression;
	public List argumentExpressions;
	public List namedArgumentExpressionEntries;
	
	public TNLApplyExpression( TNLExpression functionExpression,
			List argumentExpressions, List namedArgumentExpressionEntries,
			SourceLocation sloc, TNLExpression parent ) {
		super( sloc, parent );
		this.functionExpression = functionExpression;
		this.argumentExpressions = argumentExpressions;
		this.namedArgumentExpressionEntries = namedArgumentExpressionEntries;
	}
}
