package togos.noise2.vm.vops;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import togos.noise2.lang.CompileError;
import togos.noise2.rdf.TNLNamespace;
import togos.noise2.vm.ExpressionToOpCompiler;
import togos.noise2.vm.dftree.func.LFunctionDaDaDa_Da;
import togos.rdf.BaseRDFDescription;
import togos.rdf.RDFDescription;

public class VKExpressionCompiler
{
	protected void findVars( RDFDescription e, Set dest ) {
		String typeName = e.getTypeName();
		if( TNLNamespace.X_VAR.equals(typeName) || TNLNamespace.Y_VAR.equals(typeName) || TNLNamespace.Z_VAR.equals(typeName) ) {
			dest.add(typeName);
		}
		for( Iterator i=e.getAttributeEntries().iterator(); i.hasNext(); ) {
			Map.Entry en = (Map.Entry)i.next();
			if( en.getValue() instanceof RDFDescription ) {
				findVars( (RDFDescription)en.getValue(), dest );
			}
		}
	}
	
	public LFunctionDaDaDa_Da expressionToFunction( RDFDescription e ) throws CompileError {
		HashSet variables = new HashSet();
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
