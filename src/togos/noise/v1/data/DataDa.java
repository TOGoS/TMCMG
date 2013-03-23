package togos.noise.v1.data;

public class DataDa extends DataArray
{
	public final double[] x;
	
	public DataDa( int length, double[] x ) {
		super( length );
		this.x = x;
    }
}
