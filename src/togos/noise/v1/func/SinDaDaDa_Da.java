package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;

public class SinDaDaDa_Da extends OneArgDaDaDa_Da
{
	public SinDaDaDa_Da( FunctionDaDaDa_Da arg ) {
		super(arg);
	}
	
	public String getMacroName() {
	    return "sin";
    }

	public DataDa apply( DataDaDaDa in ) {
		int vectorSize = in.getLength();
		double[] dat = arg.apply(in).x;
		double[] out = new double[vectorSize];
		for( int i=0; i<vectorSize; i++ ) {
			out[i] = Math.sin(dat[i]);
		}
		return new DataDa(vectorSize,out);
	}
}
