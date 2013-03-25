package togos.noise.v3.vectorvm;

import togos.noise.v3.vectorvm.Program;
import togos.noise.v3.vectorvm.ProgramBuilder;
import togos.noise.v3.vectorvm.Program.RT;
import togos.noise.v3.vectorvm.Program.RegisterID;
import junit.framework.TestCase;

public class ProgramTest extends TestCase
{
	public void testIndicateConstant() {
		ProgramBuilder pb = new ProgramBuilder();
		pb.getConstant( 100.0 );
		Program p = pb.toProgram();
		assertEquals( 1, p.constants.length );
		assertEquals( 0, p.runInstructions.length );
		assertEquals( 0, p.booleanVarCount );
		assertEquals( 0, p.doubleVarCount );
		assertEquals( 100.0, p.constants[0] );
		
		p.getInstance(123).run(123);
	}

	public void testIndicateConstants() {
		ProgramBuilder pb = new ProgramBuilder();
		pb.getConstant( 100.0 );
		pb.getConstant( 300.0 );
		pb.getConstant( 100.0 );
		Program p = pb.toProgram();
		assertEquals( 2, p.constants.length );
		assertEquals( 0, p.runInstructions.length );
		assertEquals( 0, p.booleanVarCount );
		assertEquals( 0, p.doubleVarCount );
		assertEquals( 100.0, p.constants[0] );
		assertEquals( 300.0, p.constants[1] );
		
		p.getInstance(123).run(123);
	}
	
	public void testLoadConstants() {
		ProgramBuilder pb = new ProgramBuilder();
		RegisterID<RT.DVar> c100 = pb.getVariable( 100.0 );
		RegisterID<RT.DVar> c200 = pb.getVariable( 200.0 );
		RegisterID<RT.DVar> added = pb.dd_d( Program.ADD, c100, c200 );
		Program p = pb.toProgram();
		assertEquals( 2, p.constants.length );
		assertEquals( 2, p.initInstructions.length ); // 2 constant loads
		assertEquals( 1, p.runInstructions.length );  // 1 addition
		assertEquals( 0, p.booleanVarCount );
		assertEquals( 3, p.doubleVarCount );
		assertEquals( 100.0, p.constants[0] );
		assertEquals( 200.0, p.constants[1] );

		Program.Instance pi = p.getInstance(123);
		pi.run(123);
		assertEquals( 300.0, pi.dVars[added.number][0] );
	}
	
	protected void assertProgramResults( double expectedResult, Program program, int resultRegister ) {
		Program.Instance pi = program.getInstance(1);
		pi.run(1);
		assertEquals( expectedResult, pi.dVars[resultRegister][0] );
	}
	
	public void testArithmetic() {
		// 1 - 2 * (3 + 4) / 4 = -2.5
		ProgramBuilder pb = new ProgramBuilder();
		RegisterID<RT.DVar> one   = pb.getVariable(1);
		RegisterID<RT.DVar> two   = pb.getVariable(2);
		RegisterID<RT.DVar> three = pb.getVariable(3);
		RegisterID<RT.DVar> four  = pb.getVariable(4);
		RegisterID<RT.DVar> fourB = pb.getVariable(4);
		
		// Twos should share a register
		assertEquals( four.number, fourB.number );
		
		RegisterID<RT.DVar> threePlusFour = pb.dd_d( Program.ADD, three, four );
		RegisterID<RT.DVar> twoTimesSeven = pb.dd_d( Program.MULTIPLY, two, threePlusFour );
		RegisterID<RT.DVar> fourteenDividedByFour = pb.dd_d( Program.DIVIDE, twoTimesSeven, fourB );
		RegisterID<RT.DVar> oneMinusThreeAndAHalf = pb.dd_d( Program.SUBTRACT, one, fourteenDividedByFour );
		
		assertProgramResults( -2.5, pb.toProgram(), oneMinusThreeAndAHalf.number );
	}
}
