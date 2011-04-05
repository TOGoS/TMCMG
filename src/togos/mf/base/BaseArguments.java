package togos.mf.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import togos.mf.value.Arguments;

public class BaseArguments implements Arguments {
	public static BaseArguments EMPTY = new BaseArguments();
	
	public static Object single( Object value ) {
		BaseArguments args = new BaseArguments();
		args.addPositionalArgument(value);
		return args;
	}
	
	protected List positionalArguments = Collections.EMPTY_LIST;
	protected boolean positionalArgumentsClean = true;
	protected Map namedArguments = Collections.EMPTY_MAP;
	protected boolean namedArgumentsClean = true;
	
	public BaseArguments() { }
	
	public BaseArguments( Arguments args ) {
		this.positionalArguments = args.getPositionalArguments();
		this.positionalArgumentsClean = true;
		this.namedArguments = args.getNamedArguments();
		this.namedArgumentsClean = true;
	}
	
	public BaseArguments( List positionalArguments, Map namedArguments ) {
		if( positionalArguments != null ) this.positionalArguments = positionalArguments;
		if( namedArguments != null ) this.namedArguments = namedArguments;
	}

	public void addPositionalArgument( Object value ) {
		if( positionalArgumentsClean ) positionalArguments = new ArrayList(positionalArguments);
		positionalArguments.add(value);
		positionalArgumentsClean = false;
	}
	
	public void putNamedArgument( String key, Object value ) {
		if( namedArgumentsClean ) namedArguments = new HashMap(namedArguments);
		namedArguments.put(key, value);
		namedArgumentsClean = false;
	}
	
	public Map getNamedArguments() {
		return namedArguments;
	}

	public List getPositionalArguments() {
		return positionalArguments;
	}

}
