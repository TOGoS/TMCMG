package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.func.FunctionDaDaDa_Da;
import togos.lang.CompileError;
import togos.lang.ScriptError;

public class IfTest extends NoiseFunctionTest
{
	DataDaDaDa in = new DataDaDaDa( 6,
		new double[]{ 0, 1, 2, 3, 4, 5 },
		new double[]{ 0, 0, 4, 4, 8, 8 },
		new double[]{ 0, 0, 0, 0, 4, 4 }
	);
	
	public void testSimpleIf() throws ScriptError {
		FunctionDaDaDa_Da expr = (FunctionDaDaDa_Da)comp.compile("if( x > 2, +2, -2 )","test-script");
		
		DataDa out = expr.apply(in);
		assertEquals( -2, out.x[0] );
		assertEquals( -2, out.x[1] );
		assertEquals( -2, out.x[2] );
		assertEquals( +2, out.x[3] );
		assertEquals( +2, out.x[4] );
	}
	
	public void testIfChain() throws ScriptError {
		FunctionDaDaDa_Da expr = (FunctionDaDaDa_Da)comp.compile("if( x > 4, +4, x > 2, +2, -2 )","test-script");
		
		DataDa out = expr.apply(in);
		assertEquals( -2, out.x[0] );
		assertEquals( -2, out.x[1] );
		assertEquals( -2, out.x[2] );
		assertEquals( +2, out.x[3] );
		assertEquals( +2, out.x[4] );
		assertEquals( +4, out.x[5] );
	}
	
	public void testMalformedIf() throws ScriptError {
		try {
			comp.compile("if( x > 4, +4, x > 2, +2 )","test-script");
		} catch( CompileError e ) {
			return;
		}
		fail("Should have thrown compile error when compiling 'if' with even number of arguments");
	}
}
