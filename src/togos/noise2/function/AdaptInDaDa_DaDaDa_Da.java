package togos.noise2.function;


public class AdaptInDaDa_DaDaDa_Da implements FunctionDaDa_Da
{
	FunctionDaDa_Da z;
	FunctionDaDaDa_Da next;
	public AdaptInDaDa_DaDaDa_Da( FunctionDaDa_Da z, FunctionDaDaDa_Da next ) {
		this.z = z;
		this.next = next;
	}
	public AdaptInDaDa_DaDaDa_Da( FunctionDaDaDa_Da next ) {
		this( Constant_Da.ZERO, next );
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] out ) {
		double[] inZ = new double[count];
		z.apply(count, inX, inY, inZ);
		next.apply(count, inX, inY, inZ, out);
	}
}
