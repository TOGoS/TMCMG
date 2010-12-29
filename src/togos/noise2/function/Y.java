package togos.noise2.function;

public class Y implements SmartFunctionDaDaDa_Da
{
	public static final Y instance = new Y();
	
	public void apply(int count, double[] inX, double[] inY, double[] inZ, double[] out) {
		for( int i=0; i<count; ++i ) {
			out[i] = inY[i];
		}
	}
	
	public boolean isConstant() {  return false;  }
	public SmartFunctionDaDaDa_Da simplify() {  return this;  }
}
