package togos.noise2.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import togos.lang.SourceLocation;
import togos.noise2.rdf.TNLNamespace;
import togos.rdf.BaseRDFDescription;
import togos.rdf.RDFDescription;
import togos.rdf.SimpleEntry;

public class TNLExpressionCompilerTest extends TestCase
{
	TNLBlockExpression block;
	TNLExpressionCompiler comp;
	
	protected void addsm( TNLBlockExpression block, String shortName, String longName, String[] args ) {
		block.definitions.put( shortName, new TNLExpressionCompiler.SimpleArgListMacro( longName, args ) );
	}
	
	public void setUp() {
		block = new TNLBlockExpression(BaseSourceLocation.NONE, null);
		//block.definitions.put("x", TNLSymbolExpression.primitive(TNLNamespace.X));
		
		addsm( block, "x", TNLNamespace.X, new String[0] );
		addsm( block, "y", TNLNamespace.Y, new String[0] );
		addsm( block, "z", TNLNamespace.Z, new String[0] );
		addsm( block, "+", TNLNamespace.ADD,      TNLNamespace.ADD_ARGS      );
		addsm( block, "-", TNLNamespace.SUBTRACT, TNLNamespace.SUBTRACT_ARGS );
		addsm( block, "*", TNLNamespace.MULTIPLY, TNLNamespace.MULTIPLY_ARGS );
		addsm( block, "/", TNLNamespace.DIVIDE,   TNLNamespace.DIVIDE_ARGS   );
		
		comp = new TNLExpressionCompiler();
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
		expectedAttrs.add( new SimpleEntry(TNLNamespace.FACTOR, new BaseRDFDescription(TNLNamespace.X, nsl)) );
		expectedAttrs.add( new SimpleEntry(TNLNamespace.FACTOR, new BaseRDFDescription(TNLNamespace.Y, nsl)) );
		assertEquals( expectedAttrs, rExp.getAttributeEntries() );
	}
}
