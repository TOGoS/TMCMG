package togos.noise;

public class MathUtil {
	public static final double safeFlooredDivisionModulus( double num, double den ) {
		if( num == 0 || den == 0 ) return 0;
		
		double s = Math.floor(num / den);
		return num - s*den;
	}
}
