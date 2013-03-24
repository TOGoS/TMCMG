package togos.noise.v1.lang.macro;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import togos.lang.SourceLocation;
import togos.noise.v1.lang.ASTNode;
import togos.lang.CompileError;
import togos.noise.v1.lang.TNLCompiler;

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
			public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError {
				throw new CompileError("Unexpected '=' (must only occur at top level of program and with exactly one 'main' (non-'=') statement)", sn);
			}
		});
		add(";", new MacroType() {
			public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError {
				ASTNode mainNode = null;
				Map newContext = new HashMap(c.macroTypes);
				int mainCount = 0;
				for( Iterator i=sn.arguments.iterator(); i.hasNext(); ) {
					ASTNode n = (ASTNode)i.next();
					if( "=".equals(n.macroName) ) {
						if( n.arguments.size() != 2 ) {
							throw new CompileError("'=' should have exactly 2 arguments, but this one has "+n.arguments.size(), n);
						}
						ASTNode lNode = (ASTNode)n.arguments.get(1);
						ASTNode rNode = (ASTNode)n.arguments.get(0);
						String[] argNames = new String[rNode.arguments.size()];
						for( int pi=0; pi<argNames.length; ++pi ) {
							ASTNode paramNode = (ASTNode)rNode.arguments.get(pi);
							if( paramNode.arguments.size() > 0 ) {
								throw new CompileError("Parameter ('"+paramNode.macroName+"') cannot itself have parameters", paramNode);
							}
							argNames[pi] = paramNode.macroName;
						}
						if( c.getMacroType(rNode.macroName) != null || newContext.containsKey(rNode.macroName) ) {
							throw new CompileError("Duplicate definition of '"+rNode.macroName+"'", rNode);
						}
						newContext.put( rNode.macroName, new UserMacroType(rNode.macroName, argNames, lNode, newContext) );
					} else {
						mainNode = n;
						++mainCount;
					}
				}
				if( mainCount == 1 ) {
					return new TNLCompiler(newContext).compile(mainNode);
				} else {
					throw getWrongMainCountError( mainCount, sn );
				}
			}
		});
	}
}
