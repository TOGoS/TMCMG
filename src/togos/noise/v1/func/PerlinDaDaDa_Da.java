package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;

public class PerlinDaDaDa_Da extends ThreeArgDaDaDa_Da
{
	public PerlinDaDaDa_Da( FunctionDaDaDa_Da inX, FunctionDaDaDa_Da inY, FunctionDaDaDa_Da inZ ) {
		super( inX, inY, inZ );
	}
	
	public PerlinDaDaDa_Da() {
		this(X.instance, Y.instance, Z.instance);
	}
	
	public String getMacroName() {  return "perlin";  }
	
	public DataDa apply( DataDaDaDa in ) {
		final int vectorSize = in.getLength();
		double[] out = new double[vectorSize];
		D5_2Perlin.instance.apply( vectorSize, inX.apply(in).x, inY.apply(in).x, inZ.apply(in).x, out );
		return new DataDa(vectorSize,out);
	}
}
