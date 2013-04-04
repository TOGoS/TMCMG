package togos.noise.v1.lang;

import togos.lang.CompileError;
import togos.lang.SourceLocation;
import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.data.DataIa;
import togos.noise.v1.func.AdaptInXZDaDaDa_DaDa_Da;
import togos.noise.v1.func.AdaptOutDaDaDa_Da_Ia;
import togos.noise.v1.func.AdaptOutDaDa_Da_Ia;
import togos.noise.v1.func.Constant_Da;
import togos.noise.v1.func.Constant_Ia;
import togos.noise.v1.func.FunctionDaDaDa_Da;
import togos.noise.v1.func.FunctionDaDaDa_Ia;
import togos.noise.v1.func.FunctionDaDa_Da;
import togos.noise.v1.func.FunctionDaDa_Ia;
import togos.noise.v1.func.LFunctionDaDaDa_Ia;
import togos.noise.v1.func.LFunctionDaDa_Da;
import togos.noise.v1.func.PossiblyConstant;
import togos.noise.v1.func.TNLFunctionDaDaDa_Da;

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
			return new AdaptInXZDaDaDa_DaDa_Da( (FunctionDaDaDa_Da)r );
		} else if( r instanceof Number ) {
			return new Constant_Da( ((Number)r).doubleValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Da", sloc);
		}
	}
	
	public static FunctionDaDa_Ia toDaDa_Ia( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof FunctionDaDaDa_Da ) {
			r = new AdaptInXZDaDaDa_DaDa_Da( (FunctionDaDaDa_Da)r );
		}
		
		if( r instanceof FunctionDaDa_Ia ) {
			return (FunctionDaDa_Ia)r;
		} else if( r instanceof FunctionDaDa_Da ) {
			return new AdaptOutDaDa_Da_Ia( (FunctionDaDa_Da)r );
		} else if( r instanceof Number ) {
			return new Constant_Ia( ((Number)r).intValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDa_Ia", sloc);
		}		
	}
	
	public static FunctionDaDaDa_Ia toDaDaDa_Ia( Object r, SourceLocation sloc ) throws CompileError {
		if( r instanceof FunctionDaDaDa_Ia ) {
			return (FunctionDaDaDa_Ia)r;
		} else if( r instanceof FunctionDaDaDa_Da ) {
			return new AdaptOutDaDaDa_Da_Ia((FunctionDaDaDa_Da)r);
		} else if( r instanceof Number ) {
			return new Constant_Ia( ((Number)r).intValue() );
		} else {
			throw new CompileError("Can't convert "+r.getClass()+" to FunctionDaDaDa_Ia", sloc);
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

	public static double getValue( FunctionDaDa_Da da, double x, double y ) {
		double[] ax = new double[]{x};
		double[] ay = new double[]{y};
		DataDa out = da.apply(new DataDaDa(1,ax,ay));
		return out.x[0];
	}

	public static double getValue( FunctionDaDaDa_Da da, double x, double y, double z ) {
		double[] ax = new double[]{x};
		double[] ay = new double[]{y};
		double[] az = new double[]{z};
		DataDa out = da.apply(new DataDaDaDa(1,ax,ay,az));
		return out.x[0];
	}

	public static int getValue( FunctionDaDaDa_Ia da, double x, double y, double z ) {
		double[] ax = new double[]{x};
		double[] ay = new double[]{y};
		double[] az = new double[]{z};
		DataIa out = da.apply(new DataDaDaDa(1,ax,ay,az));
		return out.v[0];
	}

	public static double getConstantValue( FunctionDaDaDa_Da da ) {
		return getValue( da, 0, 0, 0 );
	}
	
	public static int getConstantValue( FunctionDaDaDa_Ia da ) {
		return getValue( da, 0, 0, 0 );
	}

	public static Constant_Da getConstantFunction( FunctionDaDaDa_Da f ) {
		return new Constant_Da( getConstantValue(f) );
	}
	
	public static Constant_Ia getConstantFunction( FunctionDaDaDa_Ia f ) {
		return new Constant_Ia( getConstantValue(f) );
	}
	
	public static TNLFunctionDaDaDa_Da collapseIfConstant( TNLFunctionDaDaDa_Da f ) {
		if( f.isConstant() ) return getConstantFunction(f);
		return f;
	}
	
	public static String toTnl( Object o ) {
		if( o instanceof Expression ) {
			return ((Expression)o).toTnl();
		} else {
			throw new RuntimeException("Object is not expression, cannot toTnl it: "+o.getClass());
		}
	}
	
	/**
	 * Return true if the object definitely represents
	 * a function that will return the same value
	 * regardless of inputs.
	 */
	public static boolean isConstant( Object o ) {
		if( o instanceof PossiblyConstant ) {
			return ((PossiblyConstant)o).isConstant();
		} else {
			return false;
		}
	}
	
	public static LFunctionDaDaDa_Ia toLDaDaDa_Ia( final FunctionDaDaDa_Ia thing ) {
		if( thing instanceof LFunctionDaDaDa_Ia ) {
			return (LFunctionDaDaDa_Ia)thing;
		}
		return new LFunctionDaDaDa_Ia() {
			@Override
            public void apply( int vectorSize, double[] x, double[] y, double[] z, int[] dest ) {
				DataIa rez = thing.apply(new DataDaDaDa( vectorSize, x, y, z ));
				for( int i=vectorSize-1; i>=0; --i ) {
					dest[i] = rez.v[i];
				}
            }
		};
	}
	
	public static LFunctionDaDa_Da toLDaDa_Da( final FunctionDaDa_Da thing ) {
		if( thing instanceof LFunctionDaDa_Da ) {
			return (LFunctionDaDa_Da)thing;
		}
		return new LFunctionDaDa_Da() {
			@Override
            public void apply( int vectorSize, double[] x, double[] y, double[] dest ) {
				DataDa rez = thing.apply(new DataDaDa( vectorSize, x, y ));
				for( int i=vectorSize-1; i>=0; --i ) {
					dest[i] = rez.x[i];
				}
            }
		};
	}

}
