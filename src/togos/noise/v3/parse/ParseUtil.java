package togos.noise.v3.parse;

import togos.lang.SourceLocation;
import togos.lang.ScriptError;

public class ParseUtil
{
	public static String formatLocation(SourceLocation sloc) {
    	if( sloc.getSourceLineNumber() == -1 ) {
    		return sloc.getSourceFilename();
    	} else {
    		return sloc.getSourceFilename()+":"+sloc.getSourceLineNumber()+","+sloc.getSourceColumnNumber();
    	}
    }

	public static String formatScriptError( ScriptError e ) {
    	SourceLocation sloc = e.sourceLocation;
    	String locMsg = "";
    	if( sloc != null ) {
    		if( sloc.getSourceLineNumber() == -1 ) {
    			locMsg = "\nIn "+sloc.getSourceFilename();
    		} else {
    			locMsg = "\nAt "+sloc.getSourceFilename()+":"+sloc.getSourceLineNumber()+","+sloc.getSourceColumnNumber();
    		}
    	}
    	return "Compile error: "+e.getRawMessage() + locMsg; 
    }
}
