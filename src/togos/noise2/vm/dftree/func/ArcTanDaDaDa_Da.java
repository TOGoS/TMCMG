package togos.noise2.vm.dftree.func;

import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;

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
