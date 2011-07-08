package togos.noise2.rdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.noise2.lang.BaseSourceLocation;
import togos.noise2.lang.SourceLocation;

public class BaseExpression implements Expression
{
	protected final SourceLocation sourceLoc;
	protected final String typeName;
	protected final List attributeEntries;
	
	public BaseExpression( String typeName, List attributeEntries, SourceLocation sloc ) {
		this.sourceLoc = sloc;
		this.typeName = typeName;
		this.attributeEntries = attributeEntries;
	}
	
	public BaseExpression( String typeName, List attributeEntries ) {
		this( typeName, attributeEntries, BaseSourceLocation.NONE );
	}
	
	public BaseExpression( String typeName, SourceLocation sloc ) {
		this( typeName, Collections.EMPTY_LIST, sloc );
	}
	
	public BaseExpression( String typeName ) {
		this( typeName, BaseSourceLocation.NONE );
	}
	
	public SourceLocation getSourceLocation() {
		return sourceLoc;
	}
	
	public String getTypeName() {
		return typeName;
	}
	
	public List getAttributeEntries() {
		return attributeEntries;
	}
	
	public List getAttributeValues( String name ) {
		List values = new ArrayList();
		for( Iterator i=attributeEntries.iterator(); i.hasNext(); ) {
			Map.Entry kv = (Map.Entry)i.next();
			if( name.equals(kv.getKey()) ) values.add( kv.getValue() );
		}
		return values;
	}
	
	protected String identifier;
	public String getIdentifier() {
		if( identifier == null ) identifier = ExprUtil.generateIdentifier(this);
		return identifier;
	}
	
	public String toString() {
		return ExprUtil.toString(this);
	}
}
