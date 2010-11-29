package togos.noise2.lang.macro;

import java.util.HashMap;

import togos.noise2.function.*;
import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.TNLCompiler;

public class NoiseMacros
{
	static abstract class BaseMacroType implements MacroType {
		protected abstract int getRequiredArgCount();
		
		protected abstract Object instantiate( ASTNode node, ASTNode[] argNodes, Object[] compiledArgs );
		
		public Object instantiate( TNLCompiler c, ASTNode sn ) {
			if( getRequiredArgCount() >= 0 && sn.arguments.size() != getRequiredArgCount() ) {
				throw new CompileError( sn.macroName + "requires "+getRequiredArgCount()+" arguments, given "+sn.arguments.size()+".", sn );
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
		
		protected int getRequiredArgCount() { return 4; } 
		
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
		add("min", dddaamt(MinOutDaDaDa_Da.class));
		add("max", dddaamt(MaxOutDaDaDa_Da.class));
		add("ridge", new BaseMacroType() {
			protected int getRequiredArgCount() { return 3; }
			
			protected Object instantiate(ASTNode node, ASTNode[] argNodes, Object[] compiledArgs) {
				return new RidgeOutDaDaDa_Da(
					FunctionUtil.toDaDaDa_Da(compiledArgs[0], argNodes[0]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[1], argNodes[1]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[2], argNodes[2])
				);
			}
		});
		add("fractal", new BaseMacroType() {
			protected int getRequiredArgCount() { return 7; }
			
			protected Object instantiate(ASTNode node, ASTNode[] argNodes, Object[] compiledArgs) {
				return new FractalDaDaDa_Da(
					FunctionUtil.toInt(compiledArgs[0], argNodes[0]),
					FunctionUtil.toDouble(compiledArgs[1], argNodes[1]),
					FunctionUtil.toDouble(compiledArgs[2], argNodes[2]),
					FunctionUtil.toDouble(compiledArgs[3], argNodes[3]),
					FunctionUtil.toDouble(compiledArgs[4], argNodes[4]),
					FunctionUtil.toDouble(compiledArgs[5], argNodes[5]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[6], argNodes[6])
				);
			}
		});
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
		add("xf", new BaseMacroType() {
			protected int getRequiredArgCount() { return 4; }
			
			protected Object instantiate(ASTNode node, ASTNode[] argNodes, Object[] compiledArgs) {
				return new TransformInDaDaDa_Da(
					FunctionUtil.toDaDaDa_Da(compiledArgs[0], argNodes[0]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[1], argNodes[1]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[2], argNodes[2]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[3], argNodes[3])
				);
			}
		});
		add("x", new ConstantMacroType(X.instance));
		add("y", new ConstantMacroType(Y.instance));
		add("z", new ConstantMacroType(Z.instance));
		add("perlin", new ConstantMacroType(PerlinDaDaDa_Da.instance));
	}
}