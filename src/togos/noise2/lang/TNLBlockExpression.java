package togos.noise2.lang;

import java.util.List;

public class TNLBlockExpression extends TNLExpression
{
	List definitions;
	TNLExpression value;
	
	public TNLBlockExpression( SourceLocation sloc, TNLExpression parent ) {
		super( sloc, parent );
	}
}
