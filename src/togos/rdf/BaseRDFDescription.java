package togos.rdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.lang.SourceLocation;
import togos.noise2.lang.BaseSourceLocation;

public class BaseRDFDescription implements RDFDescription
{
	protected final SourceLocation sourceLoc;
	protected final String typeName;
	protected final List attributeEntries;
	
	public BaseRDFDescription( String typeName, List attributeEntries, SourceLocation sloc ) {
		this.sourceLoc = sloc;
		this.typeName = typeName;
		this.attributeEntries = attributeEntries;
	}
	
	public BaseRDFDescription( String typeName, List attributeEntries ) {
		this( typeName, attributeEntries, BaseSourceLocation.NONE );
	}
	
	public BaseRDFDescription( String typeName, SourceLocation sloc ) {
		this( typeName, Collections.EMPTY_LIST, sloc );
	}
	
	public BaseRDFDescription( String typeName ) {
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
		if( identifier == null ) identifier = RDFExpressionUtil.generateIdentifier(this);
		return identifier;
	}
	
	public boolean equals( Object oth ) {
		if( oth instanceof RDFDescription ) {
			return RDFExpressionUtil.equals( this, (RDFDescription)oth );
		}
		return false;
	}
	
	public int hashCode() {
		return RDFExpressionUtil.hashCode(this);
	}
	
	public String toString() {
		return RDFExpressionUtil.toString(this, true);
	}
}
