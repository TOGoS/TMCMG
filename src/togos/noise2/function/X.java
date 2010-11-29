package togos.noise2.function;

public class X implements FunctionDaDaDa_Da
{
	public static final X instance = new X();
	
	public void apply(int count, double[] inX, double[] inY, double[] inZ, double[] out) {
		for( int i=0; i<count; ++i ) {
			out[i] = inX[i];
		}
	}
}
