package togos.noise2.vm.stkernel;

public final class AddOp implements VectorOp
{
	final double[] a, b, dest;
	
	public AddOp( final double[] dest, final double[] a, final double[] b ) {
		this.a = a;
		this.b = b;
		this.dest = dest;
	}
	
	public final void invoke( final int vectorSize ) {
		for( int i=0; i<vectorSize; ++i ) {
			dest[i] = a[i] + b[i];
		}
	}
}
