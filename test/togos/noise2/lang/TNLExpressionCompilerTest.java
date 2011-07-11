package togos.noise2.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import togos.lang.SourceLocation;
import togos.noise2.rdf.BaseRDFObjectExpression;
import togos.noise2.rdf.TNLNamespace;
import togos.rdf.RDFDescription;
import togos.rdf.SimpleEntry;
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
		RDFDescription rExp = (RDFDescription)comp.compile(tExp);
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
		
		RDFDescription rExp = (RDFDescription)comp.compile(mult);
		assertEquals( TNLNamespace.MULTIPLY, rExp.getTypeName() );
		assertEquals( 2, rExp.getAttributeEntries().size() );
		assertEquals( sl, rExp.getSourceLocation() );
		ArrayList expectedAttrs = new ArrayList();
		expectedAttrs.add( new SimpleEntry(TNLNamespace.FACTOR, new BaseRDFObjectExpression(TNLNamespace.X, nsl)) );
		expectedAttrs.add( new SimpleEntry(TNLNamespace.FACTOR, new BaseRDFObjectExpression(TNLNamespace.Y, nsl)) );
		assertEquals( expectedAttrs, rExp.getAttributeEntries() );
	}
}
