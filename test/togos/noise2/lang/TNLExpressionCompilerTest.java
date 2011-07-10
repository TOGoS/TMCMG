package togos.noise2.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import togos.noise2.rdf.BaseRDFApplyExpression;
import togos.noise2.rdf.RDFApplyExpression;
import togos.noise2.rdf.SimpleEntry;
import togos.noise2.rdf.TNLNamespace;
import junit.framework.TestCase;

public class TNLExpressionCompilerTest extends TestCase
{
	TNLBlockExpression block;
	TNLExpressionCompiler comp;
	
	public void setUp() {
		block = new TNLBlockExpression(BaseSourceLocation.NONE, null);
		block.definitions.put("x", TNLSymbolExpression.primitive(TNLNamespace.X));
		
		comp = new TNLExpressionCompiler();
		comp.primitiveSymbols.add(TNLNamespace.X);
	}
	
	public void testXCompiled() throws CompileError {
		SourceLocation sl = new BaseSourceLocation("uhm", 1, 1);
		TNLExpression tExp = new TNLSymbolExpression("x", sl, block);
		RDFApplyExpression rExp = (RDFApplyExpression)comp.compile(tExp);
		assertEquals( TNLNamespace.X, rExp.getTypeName() );
		assertEquals( 0, rExp.getAttributeEntries().size() );
		assertEquals( sl, rExp.getSourceLocation() );
	}
	
	/*
	 * `*`( ...(factors) ) = `http://ns.nuke24.net/TOGoSNoise/Multiply`(
	 *   `http://ns.nuke24.net/TOGoSNoise/factor`@...(factors)
	 * )
	 */
	
	public void testMultiplyCompiled() throws CompileError {
		SourceLocation sl = new BaseSourceLocation("uhm", 1, 1);
		SourceLocation nsl = BaseSourceLocation.NONE;
		List factors = new ArrayList();
		TNLExpression x = new TNLSymbolExpression("x", nsl, null);
		TNLExpression y = new TNLSymbolExpression("y", nsl, null);
		TNLExpression times = new TNLSymbolExpression("*", nsl, null);
		factors.add(x);
		factors.add(y);
		TNLExpression mult = new TNLApplyExpression(times, factors, Collections.EMPTY_LIST, sl, block);
		x.parent = y.parent = times.parent = mult;
		
		RDFApplyExpression rExp = (RDFApplyExpression)comp.compile(mult);
		assertEquals( TNLNamespace.MULTIPLY, rExp.getTypeName() );
		assertEquals( 2, rExp.getAttributeEntries().size() );
		assertEquals( sl, rExp.getSourceLocation() );
		ArrayList expectedAttrs = new ArrayList();
		expectedAttrs.add( new SimpleEntry(TNLNamespace.FACTOR, new BaseRDFApplyExpression(TNLNamespace.X, nsl)) );
		expectedAttrs.add( new SimpleEntry(TNLNamespace.FACTOR, new BaseRDFApplyExpression(TNLNamespace.Y, nsl)) );
		assertEquals( expectedAttrs, rExp.getAttributeEntries() );
	}
}
