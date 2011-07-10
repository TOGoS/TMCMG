package togos.noise2.rdf;

import java.util.ArrayList;
import java.util.List;

import togos.noise2.lang.SourceLocation;

public class RDFExpressionBuilder
{
	String typeName;
	List attributeEntries = new ArrayList();
	SourceLocation sloc = null;
	
	public RDFExpressionBuilder(String typeName) {
		this.typeName = typeName;
	}
	public RDFExpressionBuilder create(String typeName) {
		return new RDFExpressionBuilder(typeName);
	}
	public RDFExpressionBuilder with(String attrName, Object value) {
		attributeEntries.add(new SimpleEntry(attrName, value));
		return this;
	}
	/** Don't call with(...) any more after this; you'll screw it up! */
	public RDFApplyExpression toExpression() {
		return new BaseRDFApplyExpression(typeName, attributeEntries, sloc);
	}
};
