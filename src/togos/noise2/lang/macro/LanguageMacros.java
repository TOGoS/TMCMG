package togos.noise2.lang.macro;

import java.util.HashMap;
import java.util.Iterator;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.SourceLocation;
import togos.noise2.lang.TNLCompiler;

public class LanguageMacros
{	
	public static HashMap stdLanguageMacros = new HashMap();
	protected static void add( String name, MacroType mt ) {
		stdLanguageMacros.put(name,mt);
	}
	
	static CompileError getWrongMainCountError( int found, SourceLocation sloc ) {
		return new CompileError("The script should be exactly one 'main' (non-'=') statement, but found "+found, sloc);
	}
	
	static {
		add("=", new MacroType() {
			public Object instantiate( TNLCompiler c, ASTNode sn ) {
				throw new CompileError("Unexpected '=' (must only occur at top level of program and with exactly one 'main' (non-'=') statement)", sn);
			}
		});
		add(";", new MacroType() {
			public Object instantiate( TNLCompiler c, ASTNode sn ) {
				ASTNode mainNode = null;
				int mainCount = 0;
				for( Iterator i=sn.arguments.iterator(); i.hasNext(); ) {
					ASTNode n = (ASTNode)i.next();
					if( "=".equals(n.macroName) ) {
						if( n.arguments.size() != 2 ) {
							throw new CompileError("'=' should have exactly 2 arguments, but this one has "+n.arguments.size(), n);
						}
						ASTNode rNode = (ASTNode)n.arguments.get(0);
						if( rNode.arguments.size() > 0 ) {
							throw new CompileError("Expresstion to right of '=' should have zero arguments, but this one has "+
								rNode.arguments.size(), rNode);
						}
						c.macroDefs.put( rNode.macroName, n.arguments.get(1) );
					} else {
						mainNode = n;
						++mainCount;
					}
				}
				if( mainCount == 1 ) {
					return c.compile(mainNode);
				} else {
					throw getWrongMainCountError( mainCount, sn );
				}
			}
		});
	}
}
