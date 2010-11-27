package togos.minecraft.mapgen.script;

import togos.minecraft.mapgen.noise.AdaptDaDa_DaDaDa_Da;
import togos.minecraft.mapgen.noise.AdaptDaDa_Da_Ia;
import togos.minecraft.mapgen.noise.Constant_Da;
import togos.minecraft.mapgen.noise.Constant_Ia;
import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_Ia;

public class Functions
{
	public static FunctionDaDaDa_Da toDaDaDa_Da( Object r, SourceLocation sloc ) {
		if( r instanceof FunctionDaDaDa_Da ) {
			return (FunctionDaDaDa_Da)r;
		} else if( r instanceof Number ) {
			return new Constant_Da( ((Number)r).doubleValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Da", sloc);
		}
	}
	
	public static FunctionDaDa_Da toDaDa_Da( Object r, SourceLocation sloc ) {
		if( r instanceof FunctionDaDa_Da ) {
			return (FunctionDaDa_Da)r;
		} else if( r instanceof FunctionDaDaDa_Da ) {
			return new AdaptDaDa_DaDaDa_Da( (FunctionDaDaDa_Da)r );
		} else if( r instanceof Number ) {
			return new Constant_Da( ((Number)r).doubleValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Da", sloc);
		}		
	}

	public static FunctionDaDa_Ia toDaDa_Ia( Object r, SourceLocation sloc ) {
		if( r instanceof FunctionDaDaDa_Da ) {
			r = new AdaptDaDa_DaDaDa_Da( (FunctionDaDaDa_Da)r );
		}
		
		if( r instanceof FunctionDaDa_Ia ) {
			return (FunctionDaDa_Ia)r;
		} else if( r instanceof FunctionDaDa_Da ) {
			return new AdaptDaDa_Da_Ia( (FunctionDaDa_Da)r );
		} else if( r instanceof Number ) {
			return new Constant_Ia( ((Number)r).intValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Da", sloc);
		}		
	}
}
