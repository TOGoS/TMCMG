package togos.noise2.vm.dftree.data;

public class DataDaDa extends DataDa
{
	public final double[] y;
	
	public DataDaDa( int length, double[] x, double[] y ) {
		super( length, x );
		this.y = y;
	}
}
