package togos.noise2.vm.rdf;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import togos.noise2.lang.CompileError;
import togos.noise2.rdf.TNLNamespace;
import togos.noise2.vm.ExpressionToOpCompiler;
import togos.noise2.vm.dftree.func.LFunctionDaDaDa_Da;
import togos.noise2.vm.vops.VKOpWriter;
import togos.noise2.vm.vops.VectorKernel;
import togos.rdf.BaseRDFDescription;
import togos.rdf.RDFDescription;

/**
 * Compiles those RDF expression things to VectorKernels.
 */
public class VKExpressionCompiler
{
	/**
	 * Finds which of the X, Y, and Z variables are used
	 * anywhere in this expression, and adds their names to dest
	 */
	protected void findVars( RDFDescription e, Set<String> dest ) {
		String typeName = e.getTypeName();
		if( TNLNamespace.X_VAR.equals(typeName) || TNLNamespace.Y_VAR.equals(typeName) || TNLNamespace.Z_VAR.equals(typeName) ) {
			dest.add(typeName);
		}
		for( Map.Entry<String, Object> en : e.getAttributeEntries() ) {
			if( en.getValue() instanceof RDFDescription ) {
				findVars( (RDFDescription)en.getValue(), dest );
			}
		}
	}
	
	public LFunctionDaDaDa_Da expressionToFunction( RDFDescription e ) throws CompileError {
		HashSet<String> variables = new HashSet<String>();
		findVars( e, variables );
		
		String xVar=null, yVar=null, zVar=null, resVar;
		
		StringWriter sw = new StringWriter();
		VKOpWriter stvkow = new VKOpWriter(sw);
		ExpressionToOpCompiler etopc = new ExpressionToOpCompiler(stvkow);
		if( variables.contains(TNLNamespace.X_VAR) ) {
			xVar = stvkow.declareVar("double", "x");
			etopc.bind( new BaseRDFDescription(TNLNamespace.X_VAR, Collections.EMPTY_LIST), xVar );
		}
		if( variables.contains(TNLNamespace.Y_VAR) ) {
			yVar = stvkow.declareVar("double", "y");
			etopc.bind( new BaseRDFDescription(TNLNamespace.Y_VAR, Collections.EMPTY_LIST), yVar );
		}
		if( variables.contains(TNLNamespace.Z_VAR) ) {
			zVar = stvkow.declareVar("double", "z");
			etopc.bind( new BaseRDFDescription(TNLNamespace.Z_VAR, Collections.EMPTY_LIST), zVar );
		}
		resVar = etopc.compile( e );
		
		return new VectorKernel(sw.toString(), "generated stvk script", xVar, yVar, zVar, resVar);
	}
}
