package togos.noise.v3.program.compiler;

import junit.framework.TestCase;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.parser.TokenizerSettings;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.structure.Expression;
import togos.noise.v3.vector.vm.Program;
import togos.noise.v3.vector.vm.Program.RegisterBankID;
import togos.noise.v3.vector.vm.Program.RegisterBankID.BVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.DVar;
import togos.noise.v3.vector.vm.Program.RegisterBankID.IVar;
import togos.noise.v3.vector.vm.Program.RegisterID;

public class ExpressionVectorProgramCompilerTest extends TestCase
{
	static TokenizerSettings TEST_LOC = TokenizerSettings.forBuiltinFunctions(ExpressionVectorProgramCompilerTest.class);
	static ProgramTreeBuilder ptb = new ProgramTreeBuilder();
	static Context CONTEXT = new Context();
	static {
		CONTEXT.putAll( MathFunctions.CONTEXT );
		CONTEXT.put( "a", new Binding.Variable<Boolean>("a", Boolean.class) );
		CONTEXT.put( "b", new Binding.Variable<Boolean>("b", Boolean.class) );
		CONTEXT.put( "c", new Binding.Variable<Boolean>("c", Boolean.class) );
		CONTEXT.put( "i", new Binding.Variable<Integer>("i", Integer.class) );
		CONTEXT.put( "j", new Binding.Variable<Integer>("j", Integer.class) );
		CONTEXT.put( "k", new Binding.Variable<Integer>("k", Integer.class) );
		CONTEXT.put( "x", new Binding.Variable<Double>("x", Double.class) );
		CONTEXT.put( "y", new Binding.Variable<Double>("y", Double.class) );
		CONTEXT.put( "z", new Binding.Variable<Double>("z", Double.class) );
	}
	
	protected Expression<?> parse( String source ) throws Exception {
		return ptb.parseExpression(Parser.parse(source, TEST_LOC));
	}
	
	protected void assertResultEquals( Object expected, Object actual, String source, Program vectorProgram, Program.Instance instance ) {
		if( !expected.equals(actual) ) {
			System.err.println( "---- "+source+" ----");
			vectorProgram.dump( System.err );
			System.err.println();
			System.err.println("Instance dump:");
			instance.dumpData( System.err );
			System.err.println();
			System.err.println();
		}
		assertEquals( source, expected, actual );
	}
	
	public void assertFunctionResult( boolean expected, String body, boolean a, boolean b, boolean c ) throws Exception {
		ExpressionVectorProgramCompiler compiler = new ExpressionVectorProgramCompiler();
		compiler.declareVariable("a", BVar.INSTANCE);
		compiler.declareVariable("b", BVar.INSTANCE);
		compiler.declareVariable("c", BVar.INSTANCE);
		RegisterID<?> resultRegister = compiler.compile( parse(body).bind(CONTEXT), Boolean.class );
		assertEquals( RegisterBankID.BVar.INSTANCE, resultRegister.bankId );
		Program p = compiler.pb.toProgram();
		Program.Instance pi = p.getInstance(1);
		pi.booleanVectors[compiler.getVariableRegister("a").number][0] = a;
		pi.booleanVectors[compiler.getVariableRegister("b").number][0] = b;
		pi.booleanVectors[compiler.getVariableRegister("c").number][0] = c;
		pi.run( 1 );
		assertResultEquals( expected, pi.booleanVectors[resultRegister.number][0], body + " ("+a+", "+b+", "+c+")", p, pi );
	}
	
	public void assertFunctionResult( int expected, String body, int i, int j, int k ) throws Exception {
		ExpressionVectorProgramCompiler compiler = new ExpressionVectorProgramCompiler();
		compiler.declareVariable("i", IVar.INSTANCE);
		compiler.declareVariable("j", IVar.INSTANCE);
		compiler.declareVariable("k", IVar.INSTANCE);
		RegisterID<?> resultRegister = compiler.compile( parse(body).bind(CONTEXT),Integer.class );
		assertEquals( RegisterBankID.IVar.INSTANCE, resultRegister.bankId );
		Program p = compiler.pb.toProgram();
		Program.Instance pi = p.getInstance(1);
		pi.integerVectors[compiler.getVariableRegister("i").number][0] = i;
		pi.integerVectors[compiler.getVariableRegister("j").number][0] = j;
		pi.integerVectors[compiler.getVariableRegister("k").number][0] = k;
		pi.run( 1 );
		assertResultEquals( expected, pi.integerVectors[resultRegister.number][0], body + " ("+i+", "+j+", "+k+")", p, pi );
	}
	
	public void assertFunctionResult( double expected, String body, double x, double y, double z ) throws Exception {
		ExpressionVectorProgramCompiler compiler = new ExpressionVectorProgramCompiler();
		compiler.declareVariable("x", DVar.INSTANCE);
		compiler.declareVariable("y", DVar.INSTANCE);
		compiler.declareVariable("z", DVar.INSTANCE);
		RegisterID<?> resultRegister = compiler.compile( parse(body).bind(CONTEXT), Double.class );
		assertEquals( RegisterBankID.DVar.INSTANCE, resultRegister.bankId );
		Program p = compiler.pb.toProgram();
		Program.Instance pi = p.getInstance(1);
		pi.doubleVectors[compiler.getVariableRegister("x").number][0] = x;
		pi.doubleVectors[compiler.getVariableRegister("y").number][0] = y;
		pi.doubleVectors[compiler.getVariableRegister("z").number][0] = z;
		pi.run( 1 );
		assertResultEquals( expected, pi.doubleVectors[resultRegister.number][0], body + " ("+x+", "+y+", "+z+")", p, pi );
	}
	
	public void testCompileConstant() throws Exception {
		assertFunctionResult( 42.0, "42", 99, 99, 99 );
	}

	public void testCompileXPlusYTimesZ() throws Exception {
		assertFunctionResult( 14.0, "x + y * z", 2, 3, 4 );
	}
	
	protected void assertSameDouble( String script, double x, double y, double z ) throws Exception {
		String scriptWithConstants = "x = "+x+"; y = "+y+"; z = "+z+"; "+script;
		double scalarValue = Binding.cast(parse(scriptWithConstants).bind(CONTEXT), Number.class).getValue().doubleValue();
		
		assertFunctionResult( scalarValue, script, x, y, z );
	}
	
	protected void assertSameBoolean( String script, boolean a, boolean b, boolean c ) throws Exception {
		String scriptWithConstants = "a = "+a+"; b = "+b+"; c = "+c+"; "+script;
		boolean scalarValue = Binding.cast(parse(scriptWithConstants).bind(CONTEXT), Boolean.class).getValue().booleanValue();
		
		assertFunctionResult( scalarValue, script, a, b, c );
	}
	
	protected void assertSameInt( String script, int i, int j, int k ) throws Exception {
		String scriptWithConstants = "i = "+i+"; j = "+j+"; k = "+k+"; "+script;
		int scalarValue = Binding.cast(parse(scriptWithConstants).bind(CONTEXT), Number.class).getValue().intValue();
		
		assertFunctionResult( scalarValue, script, i, j, k );
	}
	
	public void testBooleanVariable() throws Exception {
		assertSameBoolean("a", true, false, true);
		assertSameBoolean("b", true, false, true);
		assertSameBoolean("c", true, false, true);
	}
	
	public void testIntegerVariable() throws Exception {
		assertSameInt("i", 10, 11, 12);
		assertSameInt("j", 10, 11, 12);
		assertSameInt("k", 10, 11, 12);
	}
	
	public void testDoubleVariable() throws Exception {
		assertSameDouble("x", 10, 11, 12);
		assertSameDouble("y", 10, 11, 12);
		assertSameDouble("z", 10, 11, 12);
	}
	
	static final String[] ARITHMETIC_OPS = "+ - * / ** %".split(" ");
	static final String[] LOGICAL_OPS = "|| && ^^".split(" ");
	static final String[] BITWISE_OPS = "& | ^".split(" ");
	static final String[] COMPARISON_OPS = "< <= == != >= >".split(" ");
	
	protected void testBooleanOp( String op ) throws Exception {
		assertSameBoolean("a "+op+" b", false, false, false);
		assertSameBoolean("a "+op+" b", false, true,  false);
		assertSameBoolean("a "+op+" b", true,  false, false);
		assertSameBoolean("a "+op+" b", true,  true,  false);
	}
	
	public void testArithmeticOperatorsBehaveTheSame() throws Exception {
		for( String op : ARITHMETIC_OPS ) {
			assertSameDouble("x "+op+" y", 60, 3, 999);
		}
	}
	public void testLogicalOperatorsBehaveTheSame() throws Exception {
		for( String op : LOGICAL_OPS ) {
			testBooleanOp( op );
		}
	}
	public void testBitwiseOperatorsBehaveTheSame() throws Exception {
		for( String op : BITWISE_OPS ) {
			assertSameInt("i "+op+" j", 60, 3, 999);
		}
	}
	public void testComparisonOperatorsBehaveTheSame() throws Exception {
		for( String op : COMPARISON_OPS ) {
			assertSameInt(   "if(i "+op+" j, 1   , 0   )", 60, 3, 999);
			assertSameInt(   "if(i "+op+" j, 5   , 4   )", 60, 3, 999);
			assertSameDouble("if(x "+op+" y, 1.25, 0   )", 60, 3, 999);
			assertSameDouble("if(x "+op+" y, 1.25, 0.50)", 60, 3, 999);
		}
	}
}
