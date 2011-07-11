package togos.noise2.lang;

import java.util.Collections;
import java.util.List;

import togos.lang.SourceLocation;

/**
 * Holds information about a node in a TNL program.
 * For example...
 * 
 *   foo( bar(baz), quux )
 *   
 * would be compiled to an ASTNode with macroName="foo",
 *   arguments = [
 *     ASTNode( macroName="bar", arguments=[
 *       ASTNode( macroName="baz", arguments=[] ),
 *     ],
 *     ASTNode( macroName="quux", arguments=[] )
 *   ] 
 */
public class ASTNode implements SourceLocation
{
	public String sourceFilename = "(unknown)";
	public int sourceLine, sourceColumn;
	
	public String macroName;
	public List arguments;
	
	public ASTNode( String macroName, List arguments, SourceLocation sloc ) {
		this.macroName = macroName;
		this.arguments = arguments;
		this.sourceFilename = sloc.getSourceFilename();
		this.sourceLine = sloc.getSourceLineNumber();
		this.sourceColumn = sloc.getSourceColumnNumber();
	}
	
	public ASTNode( String macroName, SourceLocation sloc ) {
		this( macroName, Collections.EMPTY_LIST, sloc );
	}
	
	public boolean equals( Object o ) {
		if( o instanceof ASTNode ) {
			ASTNode osn = (ASTNode)o;
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
	
	public String getSourceFilename() {
		return sourceFilename;
	}
	public int getSourceLineNumber() {
		return sourceLine;
	}
	public int getSourceColumnNumber() {
		return sourceColumn;
	}
}