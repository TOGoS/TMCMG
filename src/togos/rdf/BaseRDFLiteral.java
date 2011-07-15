package togos.rdf;

import togos.lang.SourceLocation;

public class BaseRDFLiteral implements RDFLiteral
{
	Object value;
	SourceLocation sloc;
	public BaseRDFLiteral( Object value, SourceLocation sloc ) {
		this.value = value;
		this.sloc = sloc;
	}
	public Object getValue() {
		return value;
	}
	public SourceLocation getSourceLocation() {
		return sloc;
	}
}
