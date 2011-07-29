package togos.noise2.vm.dftree.func;

// Based on http://stephencarmody.wikispaces.com/Simplex+Noise 

/**
 * Totally static and double!
 * Also 40% slower than SimplexNoise :(
 * And I'm not sure if it even works.
 * That A[a] stuff was tricky (see switches).
 */
public final class SimplexNoise2 implements LFunctionDaDaDa_Da
{
    private final static double onethird = 0.333333333;
    private final static double onesixth = 0.166666667;
    private final static int T[] = {0x15, 0x38, 0x32, 0x2c, 0x0d, 0x13, 0x07, 0x2a};
 
    private static final long fastfloor(double n) {
        return n > 0 ? (long) n : (long) n - 1;
    }
 
    private static final int shuffle(long i, long j, long k) {
        return b(i, j, k, 0) + b(j, k, i, 1) + b(k, i, j, 2) + b(i, j, k, 3) +
               b(j, k, i, 4) + b(k, i, j, 5) + b(i, j, k, 6) + b(j, k, i, 7);
    }
 
    private static final int b(long i, long j, long k, int B) {
        return T[b(i, B) << 2 | b(j, B) << 1 | b(k, B)];
    }
 
    private static final int b(long N, int B) {
        return (int)(N >> B & 1);
    }
    
    private static final double k(double u, double v, double w, int a0, int a1, int a2, long i, long j, long k ) {
        double s = (a0 + a1 + a2) * onesixth;
        double x = u - a0 + s;
        double y = v - a1 + s;
        double z = w - a2 + s;
        double t = 0.6f - x * x - y * y - z * z;
        int h = shuffle(i + a0, j + a1, k + a2);
        if (t < 0) return 0;
        int b5 = h >> 5 & 1;
        int b4 = h >> 4 & 1;
        int b3 = h >> 3 & 1;
        int b2 = h >> 2 & 1;
        int b = h & 3;
        double p = b == 1 ? x : b == 2 ? y : z;
        double q = b == 1 ? y : b == 2 ? z : x;
        double r = b == 1 ? z : b == 2 ? x : y;
        p = b5 == b3 ? -p : p;
        q = b5 == b4 ? -q: q;
        r = b5 != (b4^b3) ? -r : r;
        t *= t;
        return 8 * t * t * (p + (b == 0 ? q + r : b2 == 0 ? q : r));
    }
    
    public static final double apply(double x, double y, double z) {
        // Skew input space to relative coordinate in simplex cell
        double s = (x + y + z) * onethird;
        long i = fastfloor(x+s);
        long j = fastfloor(y+s);
        long k = fastfloor(z+s);
 
        // Unskew cell origin back to (x, y , z) space
        s = (i + j + k) * onesixth;
        double u = x - i + s;
        double v = y - j + s;
        double w = z - k + s;
        
        // For 3D case, the simplex shape is a slightly irregular tetrahedron.
        // Determine which simplex we're in
        int hi = u >= w ? u >= v ? 0 : 1 : v >= w ? 1 : 2;
        int lo = u < w ? u < v ? 0 : 1 : v < w ? 1 : 2;
        
        int a0=0, a1=0, a2=0;
        double res = k(u,v,w,a0,a1,a2,i,j,k);
        switch( hi ) {
        case 0: ++a0; break;
        case 1: ++a1; break;
        case 2: ++a2; break;
        }
        
        res += k(u,v,w,a0,a1,a2,i,j,k);
        switch( 3 - hi - lo ) {
        case 0: ++a0; break;
        case 1: ++a1; break;
        case 2: ++a2; break;
        }
        
        res += k(u,v,w,a0,a1,a2,i,j,k);
        switch( lo ) {
        case 0: ++a0; break;
        case 1: ++a1; break;
        case 2: ++a2; break;
        }
        
        res += k(u,v,w,a0,a1,a2,i,j,k);
        return res;
    }

	public void apply( int vectorSize, double[] x, double[] y, double[] z, double[] dest ) {
		for( int i=0; i<vectorSize; ++i ) {
			dest[i] = apply( (double)x[i], (double)y[i], (double)z[i] );
		}
    }
}
