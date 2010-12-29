package togos.noise2.lang;

import togos.noise2.function.AdaptInDaDa_DaDaDa_Da;
import togos.noise2.function.AdaptOutDaDa_Da_Ia;
import togos.noise2.function.Constant_Da;
import togos.noise2.function.Constant_Ia;
import togos.noise2.function.SmartFunctionDaDaDa_Da;
import togos.noise2.function.FunctionDaDa_Da;
import togos.noise2.function.FunctionDaDa_Ia;

public class FunctionUtil
{
	static double[] ZERO = new double[]{0};
	
	public static SmartFunctionDaDaDa_Da toDaDaDa_Da( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof SmartFunctionDaDaDa_Da ) {
			return (SmartFunctionDaDaDa_Da)r;
		} else if( r instanceof Number ) {
			return new Constant_Da( ((Number)r).doubleValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Da", sloc);
		}
	}
	
	public static FunctionDaDa_Da toDaDa_Da( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof FunctionDaDa_Da ) {
			return (FunctionDaDa_Da)r;
		} else if( r instanceof SmartFunctionDaDaDa_Da ) {
			return new AdaptInDaDa_DaDaDa_Da( (SmartFunctionDaDaDa_Da)r );
		} else if( r instanceof Number ) {
			return new Constant_Da( ((Number)r).doubleValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Da", sloc);
		}		
	}

	public static FunctionDaDa_Ia toDaDa_Ia( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof SmartFunctionDaDaDa_Da ) {
			r = new AdaptInDaDa_DaDaDa_Da( (SmartFunctionDaDaDa_Da)r );
		}
		
		if( r instanceof FunctionDaDa_Ia ) {
			return (FunctionDaDa_Ia)r;
		} else if( r instanceof FunctionDaDa_Da ) {
			return new AdaptOutDaDa_Da_Ia( (FunctionDaDa_Da)r );
		} else if( r instanceof Number ) {
			return new Constant_Ia( ((Number)r).intValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Da", sloc);
		}		
	}
	
	public static int toInt( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof Number ) {
			return ((Number)r).intValue();
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to double", sloc);
		}
	}
	
	public static double toDouble( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof Number ) {
			return ((Number)r).doubleValue();
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to double", sloc);
		}
	}
	
	public static double getConstantValue( SmartFunctionDaDaDa_Da da ) {
		double[] out = new double[1];
		da.apply(1, ZERO, ZERO, ZERO, out);
		return out[0];
	}
	
	public static Constant_Da getConstantFunction( SmartFunctionDaDaDa_Da f ) {
		return new Constant_Da( getConstantValue(f) );
	}
	
	public static SmartFunctionDaDaDa_Da collapseIfConstant( SmartFunctionDaDaDa_Da f ) {
		if( f.isConstant() ) return getConstantFunction(f);
		return f;
	}
}
