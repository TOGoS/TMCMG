package togos.noise2.lang.macro;

import java.util.HashMap;

import togos.noise2.cache.SoftCache;
import togos.noise2.function.AddOutDaDaDa_Da;
import togos.noise2.function.AndOutDaDaDa_Da;
import togos.noise2.function.CacheDaDaDa_Da;
import togos.noise2.function.ClampOutDaDaDa_Da;
import togos.noise2.function.DivideOutDaDaDa_Da;
import togos.noise2.function.EqualDaDaDa_Da;
import togos.noise2.function.FractalDaDaDa_Da;
import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.function.GreaterThanDaDaDa_Da;
import togos.noise2.function.GreaterThanOrEqualDaDaDa_Da;
import togos.noise2.function.IfDaDaDa_Da;
import togos.noise2.function.LessThanDaDaDa_Da;
import togos.noise2.function.LessThanOrEqualDaDaDa_Da;
import togos.noise2.function.MaxOutDaDaDa_Da;
import togos.noise2.function.MinOutDaDaDa_Da;
import togos.noise2.function.MultiplyOutDaDaDa_Da;
import togos.noise2.function.NotEqualDaDaDa_Da;
import togos.noise2.function.OrOutDaDaDa_Da;
import togos.noise2.function.PerlinDaDaDa_Da;
import togos.noise2.function.RidgeOutDaDaDa_Da;
import togos.noise2.function.ScaleInDaDaDa_Da;
import togos.noise2.function.SimplexDaDaDa_Da;
import togos.noise2.function.SqrtDaDaDa_Da;
import togos.noise2.function.SubtractOutDaDaDa_Da;
import togos.noise2.function.TransformInDaDaDa_Da;
import togos.noise2.function.TranslateInDaDaDa_Da;
import togos.noise2.function.X;
import togos.noise2.function.Y;
import togos.noise2.function.Z;
import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.FunctionUtil;

public class NoiseMacros
{
	static abstract class DcDcDcDfMacroType extends BaseMacroType {
		protected abstract Object instantiate( double x, double y, double z, FunctionDaDaDa_Da next );
		
		protected int getRequiredArgCount() { return 4; } 
		
		public Object instantiate( ASTNode n, ASTNode[] argNodes, Object[] compiledArgs ) throws CompileError {
			return instantiate(
				FunctionUtil.toDouble(compiledArgs[0], argNodes[0]),
				FunctionUtil.toDouble(compiledArgs[1], argNodes[1]),
				FunctionUtil.toDouble(compiledArgs[2], argNodes[2]),
				FunctionUtil.toDaDaDa_Da(compiledArgs[3], argNodes[3])
			);
		}
	}
	
	public static HashMap stdNoiseMacros = new HashMap();
	protected static void add( String name, MacroType mt ) {
		stdNoiseMacros.put(name,mt);
	}
	static MacroType dddaamt(Class functionClass) {
		return new DaDaDa_DaArrayArgMacroType(functionClass);
	}
	static MacroType tdddmt(Class functionClass) {
		return new TwoDaDaDa_DaArgMacroType(functionClass);
	}
	static {
		// Selection
		add( "if", new BaseMacroType() {
			protected int getRequiredArgCount() { return -1; }
			
			protected Object instantiate( ASTNode node, ASTNode[] argNodes, Object[] compiledArgs ) throws CompileError {
				if( compiledArgs.length % 2 == 0 ) {
					throw new CompileError("if requires an odd number of arguments, "+compiledArgs.length+" given", node);
				}
				FunctionDaDaDa_Da[] funx = new FunctionDaDaDa_Da[compiledArgs.length];
				for( int i=0; i<funx.length; ++i ) {
					funx[i] = FunctionUtil.toDaDaDa_Da(compiledArgs[i], argNodes[i]);
				}
				return new IfDaDaDa_Da( funx );
			}
		});
		
		// Comparison
		add("<",  tdddmt(LessThanDaDaDa_Da.class));
		add(">",  tdddmt(GreaterThanDaDaDa_Da.class));
		add("<=", tdddmt(LessThanOrEqualDaDaDa_Da.class));
		add(">=", tdddmt(GreaterThanOrEqualDaDaDa_Da.class));
		add("==", tdddmt(EqualDaDaDa_Da.class));
		add("!=", tdddmt(NotEqualDaDaDa_Da.class));
		
		// Boolean arithmetic
		add("and", dddaamt(AndOutDaDaDa_Da.class));
		add("or",  dddaamt(OrOutDaDaDa_Da.class));
				
		// Numeric arithmetic
		add("+", dddaamt(AddOutDaDaDa_Da.class));
		add("*", dddaamt(MultiplyOutDaDaDa_Da.class));
		add("-", dddaamt(SubtractOutDaDaDa_Da.class));
		add("/", dddaamt(DivideOutDaDaDa_Da.class));

		add("sqrt", new BaseMacroType() {
			protected int getRequiredArgCount() { return 1; }

			protected Object instantiate(ASTNode node, ASTNode[] argNodes, Object[] compiledArgs) throws CompileError {
				return new SqrtDaDaDa_Da(FunctionUtil.toDaDaDa_Da(compiledArgs[0], argNodes[0]));
			}
		}
		);
		
		// Clamping/folding
		add("min", dddaamt(MinOutDaDaDa_Da.class));
		add("max", dddaamt(MaxOutDaDaDa_Da.class));
		add("clamp", new BaseMacroType() {
			protected int getRequiredArgCount() { return 3; }
			
			protected Object instantiate(ASTNode node, ASTNode[] argNodes, Object[] compiledArgs) throws CompileError {
				return new ClampOutDaDaDa_Da(
					FunctionUtil.toDaDaDa_Da(compiledArgs[0], argNodes[0]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[1], argNodes[1]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[2], argNodes[2])
				);
			}
		});
		add("ridge", new BaseMacroType() {
			protected int getRequiredArgCount() { return 3; }
			
			protected Object instantiate(ASTNode node, ASTNode[] argNodes, Object[] compiledArgs) throws CompileError {
				return new RidgeOutDaDaDa_Da(
					FunctionUtil.toDaDaDa_Da(compiledArgs[0], argNodes[0]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[1], argNodes[1]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[2], argNodes[2])
				);
			}
		});
		add("fractal", new BaseMacroType() {
			protected int getRequiredArgCount() { return 7; }
			
			protected Object instantiate(ASTNode node, ASTNode[] argNodes, Object[] compiledArgs) throws CompileError {
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
		
		// Input transformation
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
			
			protected Object instantiate(ASTNode node, ASTNode[] argNodes, Object[] compiledArgs) throws CompileError {
				return new TransformInDaDaDa_Da(
					FunctionUtil.toDaDaDa_Da(compiledArgs[0], argNodes[0]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[1], argNodes[1]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[2], argNodes[2]),
					FunctionUtil.toDaDaDa_Da(compiledArgs[3], argNodes[3])
				);
			}
		});
		
		// Inputs
		add("x", new ConstantMacroType(X.instance));
		add("y", new ConstantMacroType(Y.instance));
		add("z", new ConstantMacroType(Z.instance));
		
		// Noise
		add("perlin", new ConstantMacroType(PerlinDaDaDa_Da.instance));
		add("simplex", new ConstantMacroType(SimplexDaDaDa_Da.instance));
		add("atan", new ConstantMacroType(SimplexDaDaDa_Da.instance));
		
		// Utility
		add("cache", new BaseMacroType() {
			protected int getRequiredArgCount() { return 1; }			
			protected Object instantiate( ASTNode node, ASTNode[] argNodes, Object[] compiledArgs ) throws CompileError {
				return new CacheDaDaDa_Da(
					SoftCache.getInstance(),
					FunctionUtil.toDaDaDa_Da(compiledArgs[0], argNodes[0])
				);
			}
		});
	}
}
