package togos.minecraft.mapgen.uri;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.mf.value.URIRef;

public class Active
{
	public static class Arg implements Map.Entry {
		final Object k, v;
		public Arg( Object k, Object v ) {
			this.k = k; this.v = v;
		}
		public Object getKey() {
		    return k;
		}
		public Object getValue() {
		    return v;
		}
		public Object setValue( Object v ) {
	        throw new UnsupportedOperationException("Can't set value on me!");
        }
		
		public boolean equals( Object obj ) {
		    if( obj instanceof Map.Entry ) {
		    	Map.Entry e = (Map.Entry)obj;
		    	return k.equals(e.getKey()) && v.equals(e.getValue());
		    }
	    	return false;
		}
	}
	
	public static class LazyActiveRequestBuilder implements ActiveRequestBuilder {
		final String functionName;
		final List args;
		public LazyActiveRequestBuilder(String functionName) {
			this.functionName = functionName;
			this.args = new ArrayList(3);
		}
		public ActiveRequestBuilder create(String functionName) {
			return new LazyActiveRequestBuilder(functionName);
		}
		public ActiveRequestBuilder with( String argName, URIRef argValue ) {
			args.add(new Arg(argName, argValue));
		    return this;
		}
		public ActiveRef toRef() { return new LazyActiveRef(functionName,args); }
	};
	
	public static ActiveRequestBuilder build( String functionName ) {
		return new LazyActiveRequestBuilder( functionName );
	}
	
	////
	
	public static URIRef parseRef( String uri ) {
		String s;
		if( uri.startsWith("active:") ) {
			s = uri.substring(7);
		} else {
			return new BaseRef(uri);
		}
		
		List arguments = new ArrayList();
		String[] parts = s.split("\\+");
		for( int i=1; i<parts.length; ++i ) {
			String[] kv = parts[i].split("@",2);
			arguments.add( new Arg(
				URIUtil.uriDecode( (String)kv[0] ),
				parseRef(URIUtil.uriDecode( (String)kv[1] ))
			));
		}
		
		return new VanillaActiveRef( uri, URIUtil.uriDecode(parts[0]), arguments );
	}
	
	////
	
	public static String mkActiveUri( String funcName, List arguments ) {
		String uri = "active:"+URIUtil.uriEncode(funcName);
		for( Iterator i=arguments.iterator(); i.hasNext(); ) {
			uri += "+";
			Map.Entry e = (Map.Entry)i.next();
			uri += URIUtil.uriEncode(e.getKey().toString()) + "@" +
				URIUtil.uriEncode(e.getValue().toString());
		}
		return uri;
	}
	
	////
	
	public static ActiveRef mkActiveRef( String functionName, List arguments ) {
		return new LazyActiveRef( functionName, arguments );
	}
	
	public static ActiveRef mkActiveRef( String funcName, Map arguments ) {
		List argList = new ArrayList();
		for( Iterator i=arguments.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = (Map.Entry)i.next();
			argList.add( new Object[]{e.getKey(),e.getValue()} );
		}
		return mkActiveRef(funcName,argList);
	}
	
	public static ActiveRef mkActiveRef( String functionName ) {
		return new LazyActiveRef( functionName );
	}

	////
	
	protected Active() {}
}
