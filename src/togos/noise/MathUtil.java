package togos.noise;

public class MathUtil {
	public static final double safeFlooredDivisionModulus( double num, double den ) {
		if( num == 0 || den == 0 ) return 0;
		else {
			// TODO: this is wrong (see unit test results); fix
			boolean invert = false;
			if( num < 0 ) invert = !invert;
			if( den < 0 ) invert = !invert;
			double v1 = invert ? -num : num;
			double factor = Math.floor(v1 / den);
			v1 -= factor*den;
			return invert ? -v1 : v1;
		}
	}
}
