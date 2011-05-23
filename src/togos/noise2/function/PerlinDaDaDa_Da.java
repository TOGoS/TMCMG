package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;

public class PerlinDaDaDa_Da extends ThreeArgDaDaDa_Da
{
	public D5_2Perlin perlin = new D5_2Perlin();
	
	public PerlinDaDaDa_Da( FunctionDaDaDa_Da inX, FunctionDaDaDa_Da inY, FunctionDaDaDa_Da inZ ) {
		super( inX, inY, inZ );
	}
	
	public PerlinDaDaDa_Da() {
		this(X.instance, Y.instance, Z.instance);
	}
	
	public String getMacroName() {  return "perlin";  }
	
	public DataDa apply( DataDaDaDa in ) {
		double[] out = new double[in.getLength()];
		double[] x = inX.apply(in).x;
		double[] y = inY.apply(in).x;
		double[] z = inZ.apply(in).x;
	    for( int i=in.getLength()-1; i>=0; --i ) {
	    	out[i] = perlin.get(x[i], y[i], z[i]);
	    }
	    return new DataDa(out);
    }
}
