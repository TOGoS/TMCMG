package togos.noise2.lang;

import togos.noise2.lang.macro.BaseMacroType;
import togos.noise2.lang.macro.ConstantMacroType;
import togos.noise2.lang.macro.NoiseMacros;
import junit.framework.TestCase;

public class CacheTest extends TestCase
{
	protected void cached( String nodeId ) {
		
	}
	
	protected Object compile( String source ) throws ScriptError {
		TNLCompiler comp = new TNLCompiler();
		comp.macroTypes.putAll(NoiseMacros.stdNoiseMacros);
		comp.macroTypes.put("p2", new ConstantMacroType(comp.compile("perlin * 2")));
		comp.macroTypes.put("cache", new BaseMacroType() {
			protected int getRequiredArgCount() { return 2; }
			
			protected Object instantiate(ASTNode node, ASTNode[] argNodes, Object[] compiledArgs) throws CompileError {
				return null;
			}
		});
		return comp.compile(source);
	}
}
