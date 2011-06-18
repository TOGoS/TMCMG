package togos.noise2.vm.stkernel;

import java.io.IOException;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.lang.ScriptError;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.lang.macro.LanguageMacros;
import togos.noise2.lang.macro.NoiseMacros;

public class STVKPerformanceTest
{
	FunctionDaDaDa_Da tree;
	STVectorKernel stvk;
	
	public void setUp( int vectorSize ) {
		TNLCompiler comp = new TNLCompiler();
		comp.macroTypes.putAll(LanguageMacros.stdLanguageMacros);
		comp.macroTypes.putAll(NoiseMacros.stdNoiseMacros);
		
		try {
			tree = (FunctionDaDaDa_Da)comp.compile("(1 + x) * y + (2 + y) * y + (3 + z) * y", "test");
			stvk = new STVKCompiler().compile(
				"vector double x\n" +
				"vector double y\n" +
				"vector double z\n" +
				"vector double temp\n" +
				"vector double res\n" +
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
	}
	
	protected void initData( double[] x, double[] y, double[] z, double startx, double starty, double startz, int vectorSize ) {
		for( int i=0; i<vectorSize; ++i ) {
			x[i] = startx+i;
			y[i] = starty;
			z[i] = startz;
		}
	}
	
	public DataDa runTree( double startx, double starty, double startz, int vectorSize ) {
		double[] x = new double[vectorSize];
		double[] y = new double[vectorSize];
		double[] z = new double[vectorSize];
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
	
	long totalTreeTime;
	long totalStvkTime;
	int innerIter = 100;
	int outerIter = 100;
	int vectorSize = 1024;
	
	public void run() {
		totalTreeTime = 0;
		totalStvkTime = 0;
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
		}
	}
	
	public void printReport() {
		System.err.println("Iterations  = " + (innerIter*outerIter));
		System.err.println("Vector size = " + vectorSize);
		System.err.println("Tree time   = " + totalTreeTime + "ms" );
		System.err.println("STVK time   = " + totalStvkTime + "ms" );
		System.err.println("Improvement = " + ((double)totalTreeTime / totalStvkTime));
	}
	
	public static void main(String[] args) {
		STVKPerformanceTest t = new STVKPerformanceTest();
		t.run();
		t.printReport();
	}
}
