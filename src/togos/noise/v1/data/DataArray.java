package togos.noise.v1.data;

public abstract class DataArray
{
	public final int length;
	
	public DataArray( int length ) {
		this.length = length;
	}
	
	public final int getLength() {
		return length;
	}
}
