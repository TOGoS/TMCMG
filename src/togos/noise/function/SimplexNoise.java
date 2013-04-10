package togos.noise.function;

import togos.noise.v3.vector.function.LFunctionDaDaDa_Da;

// Based on http://stephencarmody.wikispaces.com/Simplex+Noise 

/**
 * Need to create a new instance for each thread
 * since computation state is stored
 * in instance variables.
 */
public final class SimplexNoise implements LFunctionDaDaDa_Da
{
    private int i, j, k, A[] = {0, 0, 0};
    private float u, v, w, s;
    private final static float onethird = 0.333333333f;
    private final static float onesixth = 0.166666667f;
    private final static int T[] = {0x15, 0x38, 0x32, 0x2c, 0x0d, 0x13, 0x07, 0x2a};
 
    private static final int fastfloor(float n) {
        return n > 0 ? (int) n : (int) n - 1;
    }
 
    private static final int shuffle(int i, int j, int k) {
        return b(i, j, k, 0) + b(j, k, i, 1) + b(k, i, j, 2) + b(i, j, k, 3) +
               b(j, k, i, 4) + b(k, i, j, 5) + b(i, j, k, 6) + b(j, k, i, 7);
    }
 
    private static final int b(int i, int j, int k, int B) {
        return T[b(i, B) << 2 | b(j, B) << 1 | b(k, B)];
    }
 
    private static final int b(int N, int B) {
        return N >> B & 1;
    }
    
    private final float k(int a) {
        s = (A[0] + A[1] + A[2]) * onesixth;
        float x = u - A[0] + s;
        float y = v - A[1] + s;
        float z = w - A[2] + s;
        float t = 0.6f - x * x - y * y - z * z;
        int h = shuffle(i + A[0], j + A[1], k + A[2]);
        A[a]++;
        if (t < 0) return 0;
        int b5 = h >> 5 & 1;
        int b4 = h >> 4 & 1;
        int b3 = h >> 3 & 1;
        int b2 = h >> 2 & 1;
        int b = h & 3;
        float p = b == 1 ? x : b == 2 ? y : z;
        float q = b == 1 ? y : b == 2 ? z : x;
        float r = b == 1 ? z : b == 2 ? x : y;
        p = b5 == b3 ? -p : p;
        q = b5 == b4 ? -q: q;
        r = b5 != (b4^b3) ? -r : r;
        t *= t;
        return 8 * t * t * (p + (b == 0 ? q + r : b2 == 0 ? q : r));
    }
    
    public final float apply(float x, float y, float z) {
        // Skew input space to relative coordinate in simplex cell
        s = (x + y + z) * onethird;
        i = fastfloor(x+s);
        j = fastfloor(y+s);
        k = fastfloor(z+s);
 
        // Unskew cell origin back to (x, y , z) space
        s = (i + j + k) * onesixth;
        u = x - i + s;
        v = y - j + s;
        w = z - k + s;
 
        A[0] = A[1] = A[2] = 0;
 
        // For 3D case, the simplex shape is a slightly irregular tetrahedron.
        // Determine which simplex we're in
        int hi = u >= w ? u >= v ? 0 : 1 : v >= w ? 1 : 2;
        int lo = u < w ? u < v ? 0 : 1 : v < w ? 1 : 2;
 
        return k(hi) + k(3 - hi - lo) + k(lo) + k(0);
    }

	public void apply( int vectorSize, double[] x, double[] y, double[] z, double[] dest ) {
		for( int j=vectorSize-1; j>=0; --j ) {
			dest[j] = apply( (float)x[j], (float)y[j], (float)z[j] );
		}
    }
}
