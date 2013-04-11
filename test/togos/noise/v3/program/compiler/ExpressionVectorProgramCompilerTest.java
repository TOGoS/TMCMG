package togos.noise.v3.program.compiler;

import junit.framework.TestCase;
import togos.lang.BaseSourceLocation;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.structure.Expression;
import togos.noise.v3.vector.vm.Program;
import togos.noise.v3.vector.vm.Program.RegisterBankID;
import togos.noise.v3.vector.vm.Program.RegisterID;

public class ExpressionVectorProgramCompilerTest extends TestCase
{
	static BaseSourceLocation TEST_LOC = new BaseSourceLocation("test script", 1, 1);
	static ProgramTreeBuilder ptb = new ProgramTreeBuilder();
	static Context CONTEXT = new Context();
	static {
		CONTEXT.putAll( MathFunctions.CONTEXT );
		CONTEXT.put( "x", new Binding.Variable<Double>("x", Double.class) );
		CONTEXT.put( "y", new Binding.Variable<Double>("y", Double.class) );
		CONTEXT.put( "z", new Binding.Variable<Double>("z", Double.class) );
	}
	
	protected Expression<?> parse( String source ) throws Exception {
		return ptb.parseExpression(Parser.parse(source, TEST_LOC));
	}
	
	public void testFunction( double expected, String body, double x, double y, double z ) throws Exception {
		ExpressionVectorProgramCompiler compiler = new ExpressionVectorProgramCompiler();
		compiler.declareVariable("x", Double.class);
		compiler.declareVariable("y", Double.class);
		compiler.declareVariable("z", Double.class);
		RegisterID<?> resultRegister = compiler.compile( parse(body).bind(CONTEXT), Double.class );
		assertEquals( RegisterBankID.DVar.INSTANCE, resultRegister.bankId );
		Program p = compiler.pb.toProgram();
		Program.Instance pi = p.getInstance(1);
		pi.doubleVectors[compiler.getVariableRegister("x").number][0] = x;
		pi.doubleVectors[compiler.getVariableRegister("y").number][0] = y;
		pi.doubleVectors[compiler.getVariableRegister("z").number][0] = z;
		pi.run( 1 );
		assertEquals( expected, pi.doubleVectors[resultRegister.number][0] );
	}
	
	public void testCompileConstant() throws Exception {
		testFunction( 42, "42", 99, 99, 99 );
	}

	public void testCompileXPlusYTimesZ() throws Exception {
		testFunction( 14, "x + y * z", 2, 3, 4 );
	}
}
