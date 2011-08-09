package togos.noise2.vm.dftree.func;

import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;

public class SqrtDaDaDa_Da extends OneArgDaDaDa_Da
{
	public SqrtDaDaDa_Da( FunctionDaDaDa_Da arg) {
		super(arg);
	}
	
	protected String getMacroName() {
		return "sqrt";
	}
	
	public DataDa apply( DataDaDaDa in ) {
		int vectorSize = in.getLength();
		double[] dat = arg.apply(in).x;
		double[] out = new double[vectorSize];
		for( int i=0; i<vectorSize; i++) {
			out[i] = dat[i] > 0 ? Math.sqrt(dat[i]) : 0;
		}
		return new DataDa(vectorSize,out);
	}
}
