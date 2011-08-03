package togos.noise2.vm.vops;

public final class STSqrtOp implements STVectorOp
{
	final double[] a, dest;
	
	public STSqrtOp( final double[] dest, final double[] a ) {
		this.a = a;
		this.dest = dest;
	}
	
	public final void invoke( final int vectorSize ) {
		for( int i=0; i<vectorSize; ++i ) {
			dest[i] = Math.sqrt(a[i]);
		}
	}
}
