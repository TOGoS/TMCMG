package togos.noise2.rdf;

import togos.noise2.lang.SourceLocation;

public class BaseRDFLiteralExpression implements RDFLiteralExpression
{
	Object value;
	SourceLocation sloc;
	public BaseRDFLiteralExpression( Object value, SourceLocation sloc ) {
		this.sloc = sloc;
	}
	public Object getValue() {
		return value;
	}
	public SourceLocation getSourceLocation() {
		return sloc;
	}
}
