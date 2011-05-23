package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;

public class SimplexDaDaDa_Da extends ThreeArgDaDaDa_Da
{
	public SimplexDaDaDa_Da( FunctionDaDaDa_Da inX, FunctionDaDaDa_Da inY, FunctionDaDaDa_Da inZ ) {
		super( inX, inY, inZ );
	}
	
	public SimplexDaDaDa_Da() {
		this(X.instance, Y.instance, Z.instance);
	}
	
	public String getMacroName() {  return "simplex";  }
	
	public DataDa apply( DataDaDaDa in ) {
		SimplexNoise sn = new SimplexNoise();
		
		double[] out = new double[in.getLength()];
		double[] x = inX.apply(in).x;
		double[] y = inY.apply(in).x;
		double[] z = inZ.apply(in).x;
	    for( int i=in.getLength()-1; i>=0; --i ) {
	    	out[i] = sn.apply((float)x[i], (float)y[i], (float)z[i]);
	    }
	    return new DataDa(out);
	}
}
