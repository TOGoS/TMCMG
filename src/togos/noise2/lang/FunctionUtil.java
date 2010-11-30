package togos.noise2.lang;

import togos.noise2.function.AdaptInDaDa_DaDaDa_Da;
import togos.noise2.function.AdaptOutDaDa_Da_Ia;
import togos.noise2.function.Constant_Da;
import togos.noise2.function.Constant_Ia;
import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.function.FunctionDaDa_Da;
import togos.noise2.function.FunctionDaDa_Ia;

public class FunctionUtil
{
	public static FunctionDaDaDa_Da toDaDaDa_Da( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof FunctionDaDaDa_Da ) {
			return (FunctionDaDaDa_Da)r;
		} else if( r instanceof Number ) {
			return new Constant_Da( ((Number)r).doubleValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Da", sloc);
		}
	}
	
	public static FunctionDaDa_Da toDaDa_Da( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof FunctionDaDa_Da ) {
			return (FunctionDaDa_Da)r;
		} else if( r instanceof FunctionDaDaDa_Da ) {
			return new AdaptInDaDa_DaDaDa_Da( (FunctionDaDaDa_Da)r );
		} else if( r instanceof Number ) {
			return new Constant_Da( ((Number)r).doubleValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Da", sloc);
		}		
	}

	public static FunctionDaDa_Ia toDaDa_Ia( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof FunctionDaDaDa_Da ) {
			r = new AdaptInDaDa_DaDaDa_Da( (FunctionDaDaDa_Da)r );
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
}
