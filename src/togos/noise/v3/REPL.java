package togos.noise.v3;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import togos.lang.ScriptError;
import togos.noise.v1.lang.BaseSourceLocation;
import togos.noise.v1.lang.Operators;
import togos.noise.v3.asyncstream.StreamDestination;
import togos.noise.v3.asyncstream.StreamUtil;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.parser.Tokenizer;
import togos.noise.v3.parser.ast.ASTNode;
import togos.noise.v3.parser.ast.InfixNode;
import togos.noise.v3.program.runtime.Binding;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.structure.Expression;

public class REPL
{
	protected static String INTRO_TEXT =
		"   \u2691 \u2691 \u2691 Welcome to the TNL REPL! \u2691 \u2691 \u2691\n" +
		"Type <name> = <expression>; to define a constant.\n" +
		"Type <name>(<param1>, <param2>, ...) = <expression>; to define a function.\n" +
		"Type <expression>; to evaluate an expression.\n" +
		"Type 'infix-operators'; to see a list of infix operators.\n" +
		"E.g. 'sqrt(x) = x ** 0.5; x;'\n" +
		"Remember your semicolons!  They are not optional.\n" +
		"Also remember that most tokens are whitespace-delimited; 'x+y' is one symbol.\n" +
		"Unlike in non-interactive mode, you may redefine symbols, even predefined\n" +
		"ones such as '+'; doing so will affect all previous and future references.\n" +
		"End input (Ctrl+D on Linux) to exit.\n" +
		"Sorry, no nice console support a-la readline.  :(  I wish terminals would\n" +
		"take care of such things.  Why should every program have to bake that in?";
	
	public static void interactive( String[] args ) throws Exception {
		Tokenizer t = new Tokenizer();
		t.setSourceLocation( "REPL input", 1, 1 );
		Parser p = new Parser(true);
		
		System.err.println();
		System.err.println( INTRO_TEXT );
		System.err.println();
		System.err.print( "TNL$ " );
		
		final Binding<String> operatorListBinding = new Binding.Constant<String>( Operators.dump("\t"), BaseSourceLocation.NONE );
		
		p.pipe( new StreamDestination<ASTNode>() {
			final ProgramTreeBuilder ptb = new ProgramTreeBuilder();
			Map<String,Expression<?>> definitions = new HashMap<String,Expression<?>>();
			
			@Override
            public void data( ASTNode value ) throws Exception {
				if( value instanceof InfixNode && "=".equals(((InfixNode)value).operator) ) {
					ProgramTreeBuilder.Definition def = ptb.parseDefinition( (InfixNode)value );
					Expression<?> exp = ptb.parseExpression(def.value);
					definitions.put( def.name, exp );
					System.err.println("Defined " + def.name + " = " + exp );
				} else {
					Expression<?> exp = ptb.parseExpression(value);
					Context ctx = new Context();
					ctx.putAll( MathFunctions.CONTEXT );
					ctx.put( "infix-operators", operatorListBinding );
					
					for( Map.Entry<String,Expression<?>> def : definitions.entrySet() ) {
						ctx.put( def.getKey(), def.getValue().bind(ctx) );
					}
					Object expValue = null;
					try {
						expValue = exp.bind(ctx).getValue();
					} catch( ScriptError e ) {
						System.err.println( e.getMessage() );
					}
					if( expValue != null ) {
						System.out.println(expValue.toString());
					}
				}
				System.err.print( "TNL$ " );
            }
			
			@Override
            public void end() throws Exception {
				System.err.println("Goodbye!");
            }
		});
		t.pipe( p );
		StreamUtil.pipe( new InputStreamReader(System.in), t );
	}
	
	public static void main( String[] args ) throws Exception {
		interactive(args);
	}
}
