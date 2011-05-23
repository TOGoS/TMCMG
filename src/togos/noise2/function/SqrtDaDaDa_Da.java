package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;

public class SqrtDaDaDa_Da extends OneArgDaDaDa_Da
{
	public SqrtDaDaDa_Da( FunctionDaDaDa_Da arg) {
		super(arg);
	}
	
	protected String getMacroName() {
		return "sqrt";
	}
	
	public DataDa apply( DataDaDaDa in ) {
		int len = in.getLength();
		double[] dat = arg.apply(in).x;
		double[] out = new double[len];
		for (int i = 0; i < len; i++) {
			out[i] = dat[i] > 0 ? Math.sqrt(dat[i]) : 0;
		}
		return new DataDa( out );
	}
}
