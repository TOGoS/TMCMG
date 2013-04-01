package togos.noise.v3;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import togos.noise.v3.asyncstream.StreamDestination;
import togos.noise.v3.asyncstream.StreamUtil;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.parser.Parser;
import togos.noise.v3.parser.ProgramTreeBuilder;
import togos.noise.v3.parser.Tokenizer;
import togos.noise.v3.parser.ast.ASTNode;
import togos.noise.v3.parser.ast.InfixNode;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.structure.Expression;

public class REPL
{
	public static void main( String[] args ) throws Exception {
		Tokenizer t = new Tokenizer();
		t.setSourceLocation( "REPL input", 1, 1 );
		Parser p = new Parser(true);
		p.pipe( new StreamDestination<ASTNode>() {
			final ProgramTreeBuilder ptb = new ProgramTreeBuilder();
			Map<String,Expression<?>> definitions = new HashMap<String,Expression<?>>();
			
			@Override
            public void data( ASTNode value ) throws Exception {
				if( value instanceof InfixNode && "=".equals(((InfixNode)value).operator) ) {
					ProgramTreeBuilder.Definition def = ptb.parseDefinition( (InfixNode)value );
					definitions.put( def.name, ptb.parseExpression(def.value) );
					System.err.println("Okay");
				} else {
					Expression<?> exp = ptb.parseExpression(value);
					Context ctx = new Context( MathFunctions.CONTEXT );
					for( Map.Entry<String,Expression<?>> def : definitions.entrySet() ) {
						ctx.put( def.getKey(), def.getValue().bind(ctx) );
					}
					Object expValue = exp.bind(ctx).getValue();
					System.out.println(expValue.toString());
				}
            }
			
			@Override
            public void end() throws Exception {
				System.err.println("Goodbye!");
            }
		});
		t.pipe( p );
		StreamUtil.pipe( new InputStreamReader(System.in), t );
	}
}
