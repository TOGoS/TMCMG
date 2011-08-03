package togos.noise2.vm.dftree.func;

public class LinterpolatorDaDaDa_Da implements LFunctionDaDaDa_Da
{
	public final int width, height, depth;
	public final byte[] data;
	
	public LinterpolatorDaDaDa_Da( int w, int h, int d ) {
		width = w;
		height = h;
		depth = d;
		data = new byte[w*h*d];
	}
	
	protected static final int mod(long n, int m) {
		return (int)(n >= 0 ? n % m : m - ((-n)%m));
	}
	
    protected static final long fastfloor(double n) {
        return n > 0 ? (long) n : (long) n - 1;
    }
    
    protected final double at(int x, int y, int z) {
    	final int idx = x + y*width + z*width*height;
    	return data[idx] / 64.0;
    }
    
	public void apply( int vectorSize, double[] x, double[] y, double[] z, double[] dest ) {
		for( int i=0; i<vectorSize; ++i ) {
			long fx = fastfloor(x[i]);
			long fy = fastfloor(y[i]);
			long fz = fastfloor(z[i]);
			float dx = (float)x[i]-fx;
			float ex = 1-dx;
			float dy = (float)y[i]-fy;
			float ey = 1-dy;
			float dz = (float)z[i]-fz;
			float ez = 1-dz;
			int x0 = mod(fx,width);
			int y0 = mod(fy,height);
			int z0 = mod(fz,depth);
			int x1 = mod(x0+1,width);
			int y1 = mod(y0+1,height);
			int z1 = mod(z0+1,depth);
			dest[i] =
				dz * (
					dy * (dx*at(x0,y0,z0) + ex*at(x1,y0,z0)*(1-dx)) +
					ey * (dx*at(x0,y1,z0) + ex*at(x1,y1,z0)*(1-dx))
				) + ez * (
					dy * (dx*at(x0,y0,z1) + ex*at(x1,y0,z1)*(1-dx)) +
					ey * (dx*at(x0,y1,z1) + ex*at(x1,y1,z1)*(1-dx))
				);
		}
	}
}
