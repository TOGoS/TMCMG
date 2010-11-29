package togos.noise2.function;


public class TransformInDaDaDa_Da implements FunctionDaDaDa_Da
{
	FunctionDaDaDa_Da next;
	FunctionDaDaDa_Da xfX, xfY, xfZ;
	public TransformInDaDaDa_Da( FunctionDaDaDa_Da xfX, FunctionDaDaDa_Da xfY, FunctionDaDaDa_Da xfZ, FunctionDaDaDa_Da next ) {
		this.xfX = xfX;
		this.xfY = xfY;
		this.xfZ = xfZ;
		this.next = next;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		double[] transformedX = new double[count];
		double[] transformedY = new double[count];
		double[] transformedZ = new double[count];
		xfX.apply(count, inX, inY, inZ, transformedX);
		xfY.apply(count, inX, inY, inZ, transformedY);
		xfZ.apply(count, inX, inY, inZ, transformedZ);
		next.apply(count, transformedX, transformedY, transformedZ, out);
	}
}
