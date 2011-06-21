package togos.noise2.lang;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class LexicalScope
{
	public LexicalScope parent;
	public Map lexicalDefs = Collections.EMPTY_MAP;
	
	public boolean isDefined( String key ) {
		LexicalScope s = this;
		while( s != null ) {
			if( s.lexicalDefs.containsKey(s) ) return true;
			s = s.parent;
		}
		return false;
	}
}
