package togos.noise.v1.lang.macro;

import java.util.HashMap;

import togos.noise.v1.func.AddOutDaDaDa_Da;
import togos.noise.v1.func.AndOutDaDaDa_Da;
import togos.noise.v1.func.ArcTanDaDaDa_Da;
import togos.noise.v1.func.CacheDaDaDa_Da;
import togos.noise.v1.func.ClampOutDaDaDa_Da;
import togos.noise.v1.func.CosDaDaDa_Da;
import togos.noise.v1.func.DivideOutDaDaDa_Da;
import togos.noise.v1.func.EqualDaDaDa_Da;
import togos.noise.v1.func.ExponentiateOutDaDaDa_Da;
import togos.noise.v1.func.FractalDaDaDa_Da;
import togos.noise.v1.func.FunctionDaDaDa_Da;
import togos.noise.v1.func.GreaterThanDaDaDa_Da;
import togos.noise.v1.func.GreaterThanOrEqualDaDaDa_Da;
import togos.noise.v1.func.IfDaDaDa_Da;
import togos.noise.v1.func.LessThanDaDaDa_Da;
import togos.noise.v1.func.LessThanOrEqualDaDaDa_Da;
import togos.noise.v1.func.MaxOutDaDaDa_Da;
import togos.noise.v1.func.MinOutDaDaDa_Da;
import togos.noise.v1.func.ModOutDaDaDa_Da;
import togos.noise.v1.func.MultiplyOutDaDaDa_Da;
import togos.noise.v1.func.NotEqualDaDaDa_Da;
import togos.noise.v1.func.OrOutDaDaDa_Da;
import togos.noise.v1.func.PerlinDaDaDa_Da;
import togos.noise.v1.func.RidgeOutDaDaDa_Da;
import togos.noise.v1.func.ScaleInDaDaDa_Da;
import togos.noise.v1.func.SimplexDaDaDa_Da;
import togos.noise.v1.func.SinDaDaDa_Da;
import togos.noise.v1.func.SqrtDaDaDa_Da;
import togos.noise.v1.func.SubtractOutDaDaDa_Da;
import togos.noise.v1.func.TransformInDaDaDa_Da;
import togos.noise.v1.func.TranslateInDaDaDa_Da;
import togos.noise.v1.func.X;
import togos.noise.v1.func.Y;
import togos.noise.v1.func.Z;
import togos.noise.v1.lang.ASTNode;
import togos.noise.v1.lang.CompileError;
import togos.noise.v1.lang.FunctionUtil;

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
	/** Returns a macro type that instantiates functionClass with one argument, defaulting to X */
	static MacroType xArgMacroType(Class functionClass) {
		return new FixedDaDaDa_DaArgMacroType(functionClass, 1, new FunctionDaDaDa_Da[]{X.instance});
	}
	/** Returns a macro type that instantiates functionClass with 3 arguments, defaulting to X,Y,Z */
	static MacroType xyzArgMacroType(Class functionClass) {
		return new FixedDaDaDa_DaArgMacroType(functionClass, 3, new FunctionDaDaDa_Da[]{X.instance,Y.instance,Z.instance});
	}
	static MacroType tdddmt(Class functionClass) {
		return new FixedDaDaDa_DaArgMacroType(functionClass, 2, null);
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
		add("*", dddaamt(MultiplyOutDaDaDa_Da.class));
		add("**", dddaamt(ExponentiateOutDaDaDa_Da.class));
		add("/", dddaamt(DivideOutDaDaDa_Da.class));
		add("%", dddaamt(ModOutDaDaDa_Da.class));
		add("+", dddaamt(AddOutDaDaDa_Da.class));
		add("-", dddaamt(SubtractOutDaDaDa_Da.class));
		
		// Power fimctopms
		add("sqrt", xArgMacroType(SqrtDaDaDa_Da.class));
		
		// Trigonometric functions
		add("sin", xArgMacroType(SinDaDaDa_Da.class));
		add("cos", xArgMacroType(CosDaDaDa_Da.class));
		add("atan", xArgMacroType(ArcTanDaDaDa_Da.class));
		
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
		add("perlin", xyzArgMacroType(PerlinDaDaDa_Da.class));
		add("simplex", xyzArgMacroType(SimplexDaDaDa_Da.class));
		
		// Utility
		add("cache", new BaseMacroType() {
			protected int getRequiredArgCount() { return 1; }			
			protected Object instantiate( ASTNode node, ASTNode[] argNodes, Object[] compiledArgs ) throws CompileError {
				return new CacheDaDaDa_Da(
					CacheDaDaDa_Da.GLOBAL_CACHE,
					FunctionUtil.toDaDaDa_Da(compiledArgs[0], argNodes[0])
				);
			}
		});
	}
}
