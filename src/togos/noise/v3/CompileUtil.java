package togos.noise.v3;

import java.util.Random;

public class CompileUtil
{
	private CompileUtil() { }
	
	protected static Random rand = new Random();
	
	public static String uniqueCalculationId(String prefix) {
		return prefix + "(\"" + rand.nextLong() + "-" + rand.nextLong() + "-" + rand.nextLong() + "\")";
	}
}
