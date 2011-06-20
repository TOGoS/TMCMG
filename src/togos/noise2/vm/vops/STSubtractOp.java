package togos.noise2.vm.vops;

public final class STSubtractOp implements STVectorOp
{
	final double[] a, b, dest;
	
	public STSubtractOp( final double[] dest, final double[] a, final double[] b ) {
		this.a = a;
		this.b = b;
		this.dest = dest;
	}
	
	public final void invoke( final int vectorSize ) {
		for( int i=0; i<vectorSize; ++i ) {
			dest[i] = a[i] - b[i];
		}
	}
}
