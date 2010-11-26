package togos.minecraft.mapgen.script;

import java.util.Collections;
import java.util.List;

class ScriptNode {
	public String macroName;
	public List arguments;
	
	public ScriptNode( String macroName, List arguments ) {
		this.macroName = macroName;
		this.arguments = arguments;
	}
	
	public ScriptNode( String macroName ) {
		this( macroName, Collections.EMPTY_LIST );
	}
	
	public boolean equals( Object o ) {
		if( o instanceof ScriptNode ) {
			ScriptNode osn = (ScriptNode)o;
			return macroName.equals( osn.macroName ) &&
				arguments.equals( osn.arguments );
		}
		return false;
	}
	
	public String toString() {
		String str = macroName;
		if( arguments.size() > 0 ) {
			boolean first = true;
			str += "( ";
			for( int i=0; i<arguments.size(); ++i ) {
				if( !first ) str += ", ";
				str += arguments.get(i);
				first = false;
			}
			str += " )";
		}
		return str;
	}
}