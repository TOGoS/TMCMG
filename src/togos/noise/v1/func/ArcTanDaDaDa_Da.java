package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;

public class ArcTanDaDaDa_Da extends OneArgDaDaDa_Da
{
	public ArcTanDaDaDa_Da( FunctionDaDaDa_Da arg ) {
		super(arg);
	}
	
	public String getMacroName() {
	    return "atan";
    }

	public DataDa apply( DataDaDaDa in ) {
		int len = in.getLength();
		double[] dat = arg.apply(in).x;
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = Math.atan(dat[i]);
		}
		return new DataDa( len, out );
	}
}
