package togos.noise2.vm.vops;

import junit.framework.TestCase;
import togos.noise2.function.LFunctionDaDaDa_Da;
import togos.noise2.lang.CompileError;
import togos.noise2.rdf.RDFExpressionBuilder;
import togos.noise2.rdf.TNLNamespace;
import togos.rdf.BaseRDFDescription;
import togos.rdf.RDFDescription;

public class VKExpressionCompilerTest extends TestCase
{
	public void testCompileAddConstants() throws CompileError {
		RDFDescription add = new RDFExpressionBuilder( TNLNamespace.ADD ).
			with( TNLNamespace.TERM, Double.valueOf(4.0) ).
			with( TNLNamespace.TERM, Double.valueOf(5.0) ).toExpression();
		
		VKExpressionCompiler vkec = new VKExpressionCompiler();
		LFunctionDaDaDa_Da addFunc = vkec.expressionToFunction( add );
		double[] x = new double[]{1};
		double[] y = new double[]{2};
		double[] z = new double[]{3};
		double[] dest = new double[]{3};
		addFunc.apply(1, x, y, z, dest);
		assertEquals( 9, (int)dest[0] );
	}

	public void testCompileAddXY() throws CompileError  {
		RDFDescription add = new RDFExpressionBuilder( TNLNamespace.ADD ).
			with( TNLNamespace.TERM, new BaseRDFDescription(TNLNamespace.X) ).
			with( TNLNamespace.TERM, new BaseRDFDescription(TNLNamespace.Y) ).toExpression();
		
		VKExpressionCompiler vkec = new VKExpressionCompiler();
		LFunctionDaDaDa_Da addFunc = vkec.expressionToFunction( add );
		double[] x = new double[]{4};
		double[] y = new double[]{5};
		double[] z = new double[]{6};
		double[] dest = new double[]{3};
		addFunc.apply(1, x, y, z, dest);
		assertEquals( 9, (int)dest[0] );
	}
}
