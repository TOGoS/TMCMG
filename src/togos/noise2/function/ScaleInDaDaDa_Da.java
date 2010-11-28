package togos.noise2.function;


public class ScaleInDaDaDa_Da implements FunctionDaDaDa_Da
{
	FunctionDaDaDa_Da next;
	double scaleX, scaleY, scaleZ;
	public ScaleInDaDaDa_Da( double scaleX, double scaleY, double scaleZ, FunctionDaDaDa_Da next ) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.next = next;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		double[] scaledX = new double[count];
		double[] scaledY = new double[count];
		double[] scaledZ = new double[count];
		for( int i=0; i<count; ++i ) {
			scaledX[i] = inX[i]*scaleX;
			scaledY[i] = inY[i]*scaleY;
			scaledZ[i] = inZ[i]*scaleZ;
		}
		next.apply(count, scaledX, scaledY, scaledZ, out);
	}
}
