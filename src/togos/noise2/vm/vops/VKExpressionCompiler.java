package togos.noise2.vm.vops;

import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import togos.noise2.function.LFunctionDaDaDa_Da;
import togos.noise2.rdf.BaseRDFApplyExpression;
import togos.noise2.rdf.RDFApplyExpression;
import togos.noise2.rdf.TNLNamespace;
import togos.noise2.vm.ExpressionToOpCompiler;

public class VKExpressionCompiler
{
	protected void findVars( RDFApplyExpression e, Set dest ) {
		String typeName = e.getTypeName();
		if( TNLNamespace.X.equals(typeName) || TNLNamespace.Y.equals(typeName) || TNLNamespace.Z.equals(typeName) ) {
			dest.add(typeName);
		}
		for( Iterator i=e.getAttributeEntries().iterator(); i.hasNext(); ) {
			Map.Entry en = (Map.Entry)i.next();
			if( en.getValue() instanceof RDFApplyExpression ) {
				findVars( (RDFApplyExpression)en.getValue(), dest );
			}
		}
	}
	
	public LFunctionDaDaDa_Da expressionToFunction( RDFApplyExpression e ) {
		HashSet variables = new HashSet();
		findVars( e, variables );
		
		String xVar=null, yVar=null, zVar=null, resVar;
		
		StringWriter sw = new StringWriter();
		VKOpWriter stvkow = new VKOpWriter(sw);
		ExpressionToOpCompiler etopc = new ExpressionToOpCompiler(stvkow);
		if( variables.contains(TNLNamespace.X) ) {
			xVar = stvkow.declareVar("double", "x");
			etopc.bind( new BaseRDFApplyExpression(TNLNamespace.X, Collections.EMPTY_LIST), xVar );
		}
		if( variables.contains(TNLNamespace.Y) ) {
			yVar = stvkow.declareVar("double", "y");
			etopc.bind( new BaseRDFApplyExpression(TNLNamespace.Y, Collections.EMPTY_LIST), yVar );
		}
		if( variables.contains(TNLNamespace.Z) ) {
			zVar = stvkow.declareVar("double", "z");
			etopc.bind( new BaseRDFApplyExpression(TNLNamespace.Z, Collections.EMPTY_LIST), zVar );
		}
		resVar = etopc.compile( e );
		
		return new VectorKernel(sw.toString(), "generated stvk script", xVar, yVar, zVar, resVar);
	}
}
