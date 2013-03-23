package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Material;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.data.DataIa;
import togos.noise.v1.func.Constant_Ia;
import togos.noise.v1.func.FunctionDaDaDa_Ia;
import togos.noise.v1.func.PossiblyConstant;
import togos.noise.v1.lang.Expression;
import togos.noise.v1.lang.FunctionUtil;
import togos.noise.v1.rewrite.ExpressionRewriter;

/**
 * Until I rewrite the compiler to support expressions
 * that return objects, represent materials as (extraBits<<16)|blockType 
 */
public class MaterialDaDaDa_Ia implements FunctionDaDaDa_Ia, Expression, PossiblyConstant
{
	public static final int materialToInt( int blockType, int extraBits ) {
		return blockType | (extraBits << 16);
	}
	public static final Material intToMaterial( int mm ) {
		return new Material( (byte)(mm & 0xFF), (byte)((mm >> 16) & 0xFF) );
	}
	
	public FunctionDaDaDa_Ia blockType = Constant_Ia.ZERO;
	public FunctionDaDaDa_Ia extraBits = Constant_Ia.ZERO;
	
	public MaterialDaDaDa_Ia( FunctionDaDaDa_Ia blockType, FunctionDaDaDa_Ia extraBits ) {
		this.blockType = blockType;
		this.extraBits = extraBits;
	}
	
	public DataIa apply( final DataDaDaDa in ) {
		final int vectorSize = in.getLength(); 
		int[] out = new int[vectorSize];
		DataIa type = blockType.apply(in);
		DataIa extra = extraBits.apply(in);
	    for( int i=vectorSize-1; i>=0; --i ) {
	    	out[i] = materialToInt( type.v[i], extra.v[i] );
	    }
	    return new DataIa(vectorSize, out);
    }
	public Object[] directSubExpressions() {
	    return new Object[]{blockType,extraBits};
    }
	public int getTriviality() {
	    return 0;
    }
	public Object rewriteSubExpressions( ExpressionRewriter v ) {
	    return new MaterialDaDaDa_Ia(
	    	(FunctionDaDaDa_Ia)v.rewrite( blockType ),
	    	(FunctionDaDaDa_Ia)v.rewrite( extraBits )
	    );
    }
	public String toTnl() {
	    return "material("+
	    	FunctionUtil.toTnl(blockType)+", "+
	    	FunctionUtil.toTnl(extraBits)+")";
    }
	public boolean isConstant() {
		return FunctionUtil.isConstant(blockType) && FunctionUtil.isConstant(extraBits);
    }
}
