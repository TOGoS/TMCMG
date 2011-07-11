package togos.noise2.rdf;

import togos.lang.SourceLocation;
import togos.rdf.RDFLiteral;

public class BaseRDFLiteralExpression implements RDFLiteral
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
