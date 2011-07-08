package togos.noise2.rdf;

import java.util.ArrayList;
import java.util.List;

import togos.noise2.lang.SourceLocation;

public class ExpressionBuilder
{
	String typeName;
	List attributeEntries = new ArrayList();
	SourceLocation sloc = null;
	
	public ExpressionBuilder(String typeName) {
		this.typeName = typeName;
	}
	public ExpressionBuilder create(String typeName) {
		return new ExpressionBuilder(typeName);
	}
	public ExpressionBuilder with(String attrName, Object value) {
		attributeEntries.add(new SimpleEntry(attrName, value));
		return this;
	}
	/** Don't call with(...) any more after this; you'll screw it up! */
	public Expression toExpression() {
		return new BaseExpression(typeName, attributeEntries, sloc);
	}
};
