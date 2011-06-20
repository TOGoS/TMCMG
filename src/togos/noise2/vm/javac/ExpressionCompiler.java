package togos.noise2.vm.javac;

import java.io.Writer;

import togos.noise2.rdf.Expression;
import togos.noise2.rdf.TNLNamespace;

public class ExpressionCompiler
{
	public String compileExpression( Expression e, Writer w ) {
		if( TNLNamespace.ADD.equals(e.getTypeName()) ) {
			return "eee";
		}
		throw new RuntimeException("I dunno how to do this");
	}
}
