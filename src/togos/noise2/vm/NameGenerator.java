package togos.noise2.vm;

import java.util.HashSet;

class NameGenerator {
	HashSet<String> usedNames = new HashSet<String>();
	
	public String nextVar(String suggest) {
		if( suggest == null ) suggest = "var";
		int i=1;
		if( usedNames.contains(suggest) ) {
			usedNames.add(suggest);
			return suggest;
		}
		String ns = suggest+i;
		while( usedNames.contains(ns) ) ++i;
		usedNames.add(ns);
		return ns;
	}
}
