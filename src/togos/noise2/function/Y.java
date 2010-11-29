package togos.noise2.function;

public class Y implements FunctionDaDaDa_Da
{
	public static final Y instance = new Y();
	
	public void apply(int count, double[] inX, double[] inY, double[] inZ, double[] out) {
		for( int i=0; i<count; ++i ) {
			out[i] = inY[i];
		}
	}
}
