package togos.noise.v1;

import java.text.DecimalFormat;
import java.util.Random;

import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.func.FunctionDaDaDa_Da;
import togos.lang.ScriptError;
import togos.noise.v1.lang.TNLCompiler;
import togos.noise.v1.lang.macro.NoiseMacros;

public class CachePerformanceTest
{
	protected long testPerformance( FunctionDaDaDa_Da func, int iter, int iter2, int count ) {
		long time = 0;
		Random r = new Random();
		double[] x = new double[count];
		double[] y = new double[count];
		double[] z = new double[count];
		for( int j=0; j<iter; ++j ) {
			for( int i=0; i<count; ++i ) {
				x[i] = r.nextDouble();
				y[i] = r.nextDouble();
				z[i] = r.nextDouble();
			}
			long begin = System.currentTimeMillis();
			for( int k=0; k<iter2; ++k ) {
				z[0] += 1;
				func.apply(new DataDaDaDa(count,x,y,z));
			}
			long end = System.currentTimeMillis();
			time += (end - begin);
		}
		return time;
	}
	
	DecimalFormat fmt = new DecimalFormat("########");
	
	protected String pad(String s, int width) {
		String r = "";
		for( int i=width-s.length(); i>0; --i ) {
			r += ' ';
		}
		r += s;
		return r;
	}
	
	protected void reportPerformance( String expr, int iter, int iter2, int count ) throws ScriptError {
		TNLCompiler c = new TNLCompiler();
		c.macroTypes.putAll(NoiseMacros.stdNoiseMacros);
		FunctionDaDaDa_Da func = (FunctionDaDaDa_Da)c.compile(expr);
		long t = testPerformance(func, iter, iter2, count);
		System.out.println( expr );
		System.out.println( "  "+pad(Long.toString(t),8)+"ms" );
	}
	
	public void run() throws ScriptError {
		int iter = 50;
		int iter2 = 50;
		int count = 1024;
		System.out.println( "Outer iterations: "+iter );
		System.out.println( "Inner iterations: "+iter2 );
		System.out.println( "Vector size:      "+count );
		
		/*
		reportPerformance( "cache(simplex)", iter, iter2, count );
		reportPerformance( "simplex", iter, iter2, count );
		reportPerformance( "cache(simplex)", iter, iter2, count );
		reportPerformance( "simplex", iter, iter2, count );
		
		reportPerformance( "simplex + 2 * simplex", iter, iter2, count );
		reportPerformance( "cache(simplex + 2 * simplex)", iter, iter2, count );
		reportPerformance( "cache(simplex) + 2 * cache(simplex)", iter, iter2, count );
		reportPerformance( "cache(simplex) + cache(2 * cache(simplex))", iter, iter2, count );
		
		reportPerformance( "simplex + simplex + simplex", iter, iter2, count );
		reportPerformance( "cache(simplex) + cache(simplex) + cache(simplex)", iter, iter2, count );
		*/
		
		String fract = "fractal(3,6,3,3,3,-1,perlin)";
		
		reportPerformance( fract+" + "+fract+" * 2 + "+fract+" * 3", iter, iter2, count );
		reportPerformance( "cache("+fract+") + cache("+fract+") * 2 + cache("+fract+") * 3", iter, iter2, count );
	}
	
	public static void main(String[] args) {
		try {
			new CachePerformanceTest().run();
		} catch( ScriptError e ) {
			throw new RuntimeException(e);
		}
	}
}
