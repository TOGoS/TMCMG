package togos.noise.v3.vm;

import togos.noise.v3.vm.Program.RT;
import togos.noise.v3.vm.Program.RegisterID;
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
}
