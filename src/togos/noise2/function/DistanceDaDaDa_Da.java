package togos.noise2.function;

public class DistanceDaDaDa_Da implements SmartFunctionDaDaDa_Da
{
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		for( int i=0; i<count; ++i ) {
			out[i] = Math.sqrt(inX[i]*inX[i]+inY[i]*inY[i]+inZ[i]*inZ[i]);
		}
	}
	
	public boolean isConstant() {  return false;  }
	public SmartFunctionDaDaDa_Da simplify() {  return this;  }
}
