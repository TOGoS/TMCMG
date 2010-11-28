package togos.noise2.lang.macro;

import java.util.HashMap;

import togos.noise2.function.AddOutDaDaDa_Da;
import togos.noise2.function.DivideOutDaDaDa_Da;
import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.function.MultiplyOutDaDaDa_Da;
import togos.noise2.function.PerlinDaDaDa_Da;
import togos.noise2.function.ScaleInDaDaDa_Da;
import togos.noise2.function.SubtractOutDaDaDa_Da;
import togos.noise2.function.TranslateInDaDaDa_Da;
import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.TNLCompiler;

public class NoiseMacros
{
	static abstract class BaseMacroType implements MacroType {
		protected int requiredArgCount = -1;
		
		protected abstract Object instantiate( ASTNode node, ASTNode[] argNodes, Object[] compiledArgs );
		
		public Object instantiate( TNLCompiler c, ASTNode sn ) {
			if( requiredArgCount >= 0 && sn.arguments.size() != requiredArgCount ) {
				throw new CompileError( sn.macroName + "requires "+requiredArgCount+" arguments, given "+sn.arguments.size()+".", sn );
			}
			Object[] compiledArgs = new Object[sn.arguments.size()];
			ASTNode[] argNodes = new ASTNode[sn.arguments.size()];
			for( int i=0; i<compiledArgs.length; ++i ) {
				argNodes[i] = (ASTNode)sn.arguments.get(i);
				compiledArgs[i] = c.compile(argNodes[i]);
			}
			return instantiate( sn, argNodes, compiledArgs );
		}
	}
	
	static abstract class DcDcDcDfMacroType extends BaseMacroType {
		protected abstract Object instantiate( double x, double y, double z, FunctionDaDaDa_Da next );
		
		protected int requiredArgCount = 4; 
		
		public Object instantiate( ASTNode n, ASTNode[] argNodes, Object[] compiledArgs ) {
			return instantiate(
				FunctionUtil.toDouble(compiledArgs[0], argNodes[0]),
				FunctionUtil.toDouble(compiledArgs[1], argNodes[1]),
				FunctionUtil.toDouble(compiledArgs[2], argNodes[2]),
				FunctionUtil.toDaDaDa_Da(compiledArgs[3], argNodes[3])
			);
		}
	}
	
	public static HashMap stdNoiseMacros = new HashMap();
	public static void add( String name, MacroType mt ) {
		stdNoiseMacros.put(name,mt);
	}
	static MacroType dddaamt(Class functionClass) {
		return new DaDaDa_DaArrayArgMacroType(functionClass);
	}
	static {
		add("+", dddaamt(AddOutDaDaDa_Da.class));
		add("*", dddaamt(MultiplyOutDaDaDa_Da.class));
		add("-", dddaamt(SubtractOutDaDaDa_Da.class));
		add("/", dddaamt(DivideOutDaDaDa_Da.class));
		add("scale-in", new DcDcDcDfMacroType() {
			public Object instantiate( double x, double y, double z, FunctionDaDaDa_Da next ) {
				return new ScaleInDaDaDa_Da(x,y,z,next);
			}
		});
		add("translate-in", new DcDcDcDfMacroType() {
			public Object instantiate( double x, double y, double z, FunctionDaDaDa_Da next ) {
				return new TranslateInDaDaDa_Da(x,y,z,next);
			}
		});
		add("perlin", new ConstantMacroType(PerlinDaDaDa_Da.instance));
	}
}
