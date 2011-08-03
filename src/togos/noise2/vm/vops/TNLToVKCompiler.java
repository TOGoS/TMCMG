package togos.noise2.vm.vops;

import java.util.HashMap;
import java.util.Map;

import togos.lang.SourceLocation;
import togos.noise2.lang.ScriptError;
import togos.noise2.vm.Compiler;
import togos.noise2.vm.rdf.TNLExpressionCompiler;
import togos.rdf.RDFDescription;

public class TNLToVKCompiler implements Compiler
{
	public Map macros = new HashMap();
	
	public Object compile( String source, SourceLocation loc, String scriptId, Class preferredType ) throws ScriptError {
		TNLExpressionCompiler tnlec = new TNLExpressionCompiler();
		RDFDescription rdfd = (RDFDescription)tnlec.compile( source, loc, macros );
		VKExpressionCompiler vkec = new VKExpressionCompiler();
		return vkec.expressionToFunction( rdfd );
	}
}
