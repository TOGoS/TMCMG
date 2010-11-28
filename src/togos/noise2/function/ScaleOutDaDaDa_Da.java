package togos.noise2.function;


public class ScaleOutDaDaDa_Da implements FunctionDaDaDa_Da
{
	FunctionDaDaDa_Da next;
	double scale;
	public ScaleOutDaDaDa_Da( double scale, FunctionDaDaDa_Da next ) {
		this.next = next;
		this.scale = scale;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		next.apply(count, inX, inY, inZ, out);
		for( int i=0; i<count; ++i ) out[i] *= scale;
	}
}
