package togos.jobkernel.uri;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.mf.value.URIRef;

public abstract class BaseActiveRef extends AbstractRef implements ActiveRef
{
	protected final String functionName;
	protected final List arguments;
	
	protected BaseActiveRef( String functionName, List arguments ) {
		this.functionName = functionName;
		this.arguments = arguments;
	}
	
	public String getFunctionName() {
		return functionName;
	}
	
	public List getArgumentPairs() {
		return arguments;
	}
	
	public URIRef getArgument( String name ) {
		for( Iterator i=arguments.iterator(); i.hasNext(); ) {
			Map.Entry kv = (Map.Entry)i.next();
			if( name.equals(kv.getKey()) ) return (URIRef)kv.getValue();
		}
		return null;
	}
	
	public URIRef requireArgument( String name ) {
		URIRef r = getArgument(name);
		if( r == null ) {
			throw new RuntimeException("Argument <"+name+"> required for <"+functionName+">");
		}
		return r;
	}
	
	public List getArguments( String name ) {
		List values = new ArrayList();
		for( Iterator i=arguments.iterator(); i.hasNext(); ) {
			Map.Entry kv = (Map.Entry)i.next();
			if( name.equals(kv.getKey()) ) values.add( kv.getValue() );
		}
		return values;
	}
}
