package togos.noise2.vm;

import java.io.IOException;

import togos.noise2.lang.ScriptError;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.lang.macro.LanguageMacros;
import togos.noise2.lang.macro.NoiseMacros;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.func.FunctionDaDaDa_Da;
import togos.noise2.vm.vops.STVKScriptCompiler;
import togos.noise2.vm.vops.STVectorKernel;

public class PerformanceTest
{
	class NativeCalculator {
		public final double calculate( double x, double y, double z ) {
			return (1 + x) * y + (2 + y) * y + (3 + z) * y;
		}
		public final void calculate( final int vectorSize, final double[] dest, final double[] x, final double[] y, final double[] z ) {
			for( int i=0; i<vectorSize; ++i ) {
				//dest[i] = (1 + x[i]) * y[i] + (2 + y[i]) * y[i] + (3 + z[i]) * y[i];
				dest[i] = calculate( x[i], y[i], z[i] );
			}
		}
	}
	
	FunctionDaDaDa_Da tree;
	STVectorKernel stvk;
	NativeCalculator nc;
	double[] x, y, z, dest;
	
	public void setUp( int vectorSize ) {
		x = new double[vectorSize];
		y = new double[vectorSize];
		z = new double[vectorSize];
		dest = new double[vectorSize];
		
		TNLCompiler comp = new TNLCompiler();
		comp.macroTypes.putAll(LanguageMacros.stdLanguageMacros);
		comp.macroTypes.putAll(NoiseMacros.stdNoiseMacros);
		
		try {
			tree = (FunctionDaDaDa_Da)comp.compile("(1 + x) * y + (2 + y) * y + (3 + z) * y", "test");
			stvk = new STVKScriptCompiler().compile(
				"var double x\n" +
				"var double y\n" +
				"var double z\n" +
				"var double temp\n" +
				"var double res\n" +
				"\n" +
				"temp = 1 + x\n" +
				"res  = temp * y\n" +
				"temp = 2 + y\n" +
				"temp = temp * y\n" +
				"res  = res + temp\n" +
				"temp = 3 + z\n" +
				"temp = temp * y\n" +
				"res  = res + temp\n",
				"test", vectorSize);
		} catch( IOException e ) {
			throw new RuntimeException(e);
		} catch( ScriptError e ) {
			throw new RuntimeException(e);
		}
		
		nc = new NativeCalculator();
	}
	
	protected void initData( double[] x, double[] y, double[] z, double startx, double starty, double startz, int vectorSize ) {
		for( int i=0; i<vectorSize; ++i ) {
			x[i] = startx+i;
			y[i] = starty;
			z[i] = startz;
		}
	}
	
	public DataDa runTree( double startx, double starty, double startz, int vectorSize ) {
		initData( x, y, z, startx, starty, startz, vectorSize );
		DataDaDaDa dat = new DataDaDaDa(x, y, z);
		return tree.apply(dat);
	}
	
	public double[] runStvk( double startx, double starty, double startz, int vectorSize ) {
		double[] x = (double[])stvk.vars.get("x");
		double[] y = (double[])stvk.vars.get("y");
		double[] z = (double[])stvk.vars.get("z");
		initData( x, y, z, startx, starty, startz, vectorSize );
		stvk.invoke(vectorSize);
		return (double[])stvk.vars.get("res");
	}
	
	public double[] runNV( double startx, double starty, double startz, int vectorSize ) {
		initData( x, y, z, startx, starty, startz, vectorSize );
		nc.calculate(vectorSize, dest, x, y, z);
		return dest;
	}
	
	public double[] runNS( double startx, double starty, double startz, int vectorSize ) {
		initData( x, y, z, startx, starty, startz, vectorSize );
		for( int i=0; i<vectorSize; ++i ) {
			dest[i] = nc.calculate( x[i], y[i], z[i] );
		}
		nc.calculate(vectorSize, dest, x, y, z);
		return dest;
	}
	
	long totalTreeTime;
	long totalStvkTime;
	long totalNVTime;
	long totalNSTime;
	int innerIter  = 100;
	int outerIter  = 500;
	int vectorSize = 256;
	
	protected String format( long num, int places ) {
		String s = Long.toString(num);
		while( s.length() < places ) {
			s = " "+s;
		}
		return s;
	}
	
	public void run() {
		totalTreeTime   = 0;
		totalStvkTime   = 0;
		totalNVTime = 0;
		setUp(vectorSize);
		long bt, et;
		for( int o=0; o<outerIter; ++o ) {
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				int k = o*innerIter+i;
				runTree( 0, k, 1, vectorSize );
			}
			et = System.currentTimeMillis();
			totalTreeTime += (et - bt);
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				int k = o*innerIter+i;
				runStvk( 0, k, 1, vectorSize );
			}
			et = System.currentTimeMillis();
			totalStvkTime += (et - bt);
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				int k = o*innerIter+i;
				runNV( 0, k, 1, vectorSize );
			}
			et = System.currentTimeMillis();
			totalNVTime += (et - bt);
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				int k = o*innerIter+i;
				runNS( 0, k, 1, vectorSize );
			}
			et = System.currentTimeMillis();
			totalNSTime += (et - bt);
		}
	}
	
	public void printReport() {
		System.err.println("Iterations  = " + (innerIter*outerIter));
		System.err.println("Vector size = " + vectorSize);
		System.err.println("Tree time   = " + format(totalTreeTime, 6) + "ms" );
		System.err.println("STVK time   = " + format(totalStvkTime, 6) + "ms" );
		System.err.println("NV time     = " + format(totalNVTime, 6) + "ms" );
		System.err.println("NS time     = " + format(totalNSTime, 6) + "ms" );
		//System.err.println("Improvement   = " + ((double)totalTreeTime / totalStvkTime));
	}
	
	public static void main(String[] args) {
		PerformanceTest t = new PerformanceTest();
		t.run();
		t.printReport();
	}
}
