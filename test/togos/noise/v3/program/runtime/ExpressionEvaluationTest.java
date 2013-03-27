package togos.noise.v3.program.runtime;

import togos.lang.RuntimeError;
import togos.noise.v1.lang.BaseSourceLocation;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.program.runtime.BoundArgumentList.BoundArgument;
import togos.noise.v3.program.runtime.Function;
import togos.noise.v3.program.structure.Expression;
import junit.framework.TestCase;

public class ExpressionEvaluationTest extends TestCase
{
	static BaseSourceLocation TEST_LOC = new BaseSourceLocation("test script", 1, 1);
	static BaseSourceLocation BUILTIN_LOC = new BaseSourceLocation("built-in function", 0, 0);
	static ProgramTreeBuilder ptb = new ProgramTreeBuilder();
	
	Context CONTEXT = new Context();
	
	abstract class CoreNumericFunction implements Function<Double> {
		abstract String getName();
		abstract double apply( double a, double b );
		
		@Override
        public Binding<Double> apply( BoundArgumentList input ) throws Exception {
			if( input.arguments.size() != 2 ) {
				throw new RuntimeError( "Too many arguments ("+input.arguments.size()+" to "+getName(), input.sLoc );
			}
			
			int argCount = 0;
			double a=Double.NaN, b=Double.NaN;
			for( BoundArgument<?> arg : input.arguments ) {
				if( !arg.name.isEmpty() ) {
					throw new RuntimeError("+ takes no named arguments, but was given '"+arg.name+"'", arg.value.sLoc);
				}
				Object o = arg.value.getValue();
				if( !(o instanceof Number) ) {
					throw new RuntimeError("Non-numeric argument: "+o, arg.value.sLoc );
				}
				double argValue = ((Number)o).doubleValue();
				switch( argCount ) {
				case 0: a = argValue; break;
				case 1: b = argValue; break;
				default: throw new RuntimeError( "Too many arguments to "+getName(), arg.value.sLoc );
				}
				
				++argCount;
			}
			if( argCount < 2 ) {
				throw new RuntimeError( "Not enough arguments to "+getName(), input.sLoc );
			}
			return new Binding.Constant<Double>( apply(a,b), BUILTIN_LOC );
        }
	}
	
	protected static <V> Binding<V> builtinBinding( V v ) {
		return new Binding.Constant<V>( v, BUILTIN_LOC );
	}
	
	public ExpressionEvaluationTest() {
		super();
		CONTEXT.put("+", builtinBinding(new CoreNumericFunction() {
			@Override String getName() { return "+"; }
			@Override double apply( double a, double b ) { return a + b; }
		}));
		CONTEXT.put("-", builtinBinding(new CoreNumericFunction() {
			@Override String getName() { return "-"; }
			@Override double apply( double a, double b ) { return a - b; }
		}));
		CONTEXT.put("*", builtinBinding(new CoreNumericFunction() {
			@Override String getName() { return "*"; }
			@Override double apply( double a, double b ) { return a * b; }
		}));
		CONTEXT.put("/", builtinBinding(new CoreNumericFunction() {
			@Override String getName() { return "/"; }
			@Override double apply( double a, double b ) { return a / b; }
		}));
	}
	
	protected Expression<?> parse( String source ) throws Exception {
		return ptb.parseExpression(Parser.parse(source, TEST_LOC));
	}
	
	protected Object eval( String source ) throws Exception {
		return parse(source).bind(CONTEXT).getValue();
	}

	public void testEvaluateConstant() throws Exception {
		assertEquals( Long.valueOf(1), eval("1") );
	}
	
	public void testEvaluateAddition() throws Exception {
		assertEquals( Double.valueOf(3), eval("1 + 2") );
	}
	
	public void testEvaluateModeAddition() throws Exception {
		assertEquals( Double.valueOf(10), eval("1 + 2 + 3 + 4") );
	}
	
	public void testArithmetic() throws Exception {
		assertEquals( Double.valueOf(-3), eval("(1 + 2) * (3 - 4)") );
	}
	
	public void testBlock() throws Exception {
		assertEquals( Double.valueOf(9), eval("a = 1 + 2; a * a") );
	}
}
