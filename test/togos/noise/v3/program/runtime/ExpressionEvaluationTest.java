package togos.noise.v3.program.runtime;

import togos.lang.RuntimeError;
import togos.noise.Function;
import togos.noise.v1.lang.BaseSourceLocation;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.program.runtime.BoundArgumentList.BoundArgument;
import togos.noise.v3.program.structure.Expression;
import junit.framework.TestCase;

public class ExpressionEvaluationTest extends TestCase
{
	BaseSourceLocation TEST_LOC = new BaseSourceLocation("test script", 1, 1);
	BaseSourceLocation BUILTIN_LOC = new BaseSourceLocation("built-in function", 0, 0);
	ProgramTreeBuilder ptb = new ProgramTreeBuilder();
	
	Context CONTEXT = new Context();
	
	public ExpressionEvaluationTest() {
		super();
		CONTEXT.put("+", new Binding.Constant<Function<BoundArgumentList,Binding<? extends Number>>>(
			new Function<BoundArgumentList,Binding<? extends Number>>() {
				@Override
                public Binding<? extends Number> apply( BoundArgumentList input ) throws Exception {
					double result = 0;
					for( BoundArgument<?> a : input.arguments ) {
						if( !a.name.isEmpty() ) {
							throw new RuntimeError("+ takes no named arguments, but was given '"+a.name+"'", a.value.sLoc);
						}
						Object o = a.value.getValue();
						if( !(o instanceof Number) ) {
							throw new RuntimeError("Non-numeric argument: "+o, a.value.sLoc );
						}
						result += ((Number)o).doubleValue();
					}
					return new Binding.Constant<Number>( result, BUILTIN_LOC );
                }
			},
			BUILTIN_LOC
		));
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
}
