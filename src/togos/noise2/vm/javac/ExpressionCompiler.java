package togos.noise2.vm.javac;

import java.io.Writer;

import togos.noise2.rdf.TNLNamespace;
import togos.rdf.RDFDescription;

public class ExpressionCompiler
{
	public String compileExpression( RDFDescription e, Writer w ) {
		// TODO: Implement properly?
		if( TNLNamespace.ADD.equals(e.getTypeName()) ) {
			return "eee";
		}
		throw new RuntimeException("I dunno how to do this");
	}
}
