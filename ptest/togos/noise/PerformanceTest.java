package togos.noise;

import togos.lang.ScriptError;
import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.func.FunctionDaDaDa_Da;
import togos.noise.v1.lang.TNLCompiler;
import togos.noise.v1.lang.macro.LanguageMacros;
import togos.noise.v1.lang.macro.NoiseMacros;
import togos.noise.v3.vectorvm.Program;
import togos.noise.v3.vectorvm.ProgramBuilder;
import togos.noise.v3.vectorvm.Program.RegisterID;

/**
 * Calculates
 * 
 *   (1 + x) * y + (2 + y) * y + (3 + z) * y
 *
 * using different engines to compare their speed.
 */
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
	NativeCalculator nc;
	Program.Instance pi;
	@SuppressWarnings("unchecked")
	RegisterID<Program.RegisterBankID.DVar>[] piVars = new RegisterID[4];
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
		} catch( ScriptError e ) {
			throw new RuntimeException(e);
		}
		
		ProgramBuilder pm = new ProgramBuilder();
		piVars[0] = pm.newDVar();
		piVars[1] = pm.newDVar();
		piVars[2] = pm.newDVar();
		piVars[3] = pm.dd_d( Program.ADD,
			pm.dd_d( Program.ADD,
				pm.dd_d( Program.MULTIPLY,
					pm.dd_d( Program.ADD, pm.getVariable(1), piVars[0] ),
					piVars[1]
				),
				pm.dd_d( Program.MULTIPLY,
					pm.dd_d( Program.ADD, pm.getVariable(2), piVars[1] ),
					piVars[1]
				)
			),
			pm.dd_d( Program.MULTIPLY,
				pm.dd_d( Program.ADD, pm.getVariable(3), piVars[2] ),
				piVars[1]
			)
		);
		pi = pm.toProgram().getInstance(vectorSize);
		
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
		DataDaDaDa dat = new DataDaDaDa(vectorSize, x, y, z);
		return tree.apply(dat);
	}
	
	/**
	 * Native (Java) vector calculator
	 */
	public double[] runNV( double startx, double starty, double startz, int vectorSize ) {
		initData( x, y, z, startx, starty, startz, vectorSize );
		nc.calculate(vectorSize, dest, x, y, z);
		return dest;
	}
	
	/**
	 * Native (Java) scalar calculator
	 */
	public double[] runNS( double startx, double starty, double startz, int vectorSize ) {
		initData( x, y, z, startx, starty, startz, vectorSize );
		for( int i=0; i<vectorSize; ++i ) {
			dest[i] = nc.calculate( x[i], y[i], z[i] );
		}
		nc.calculate(vectorSize, dest, x, y, z);
		return dest;
	}
	
	public double[] runPi( double startx, double starty, double startz, int vectorSize ) {
		initData( x, y, z, startx, starty, startz, vectorSize );
		pi.setDVar( piVars[0].number, x, vectorSize );
		pi.setDVar( piVars[1].number, y, vectorSize );
		pi.setDVar( piVars[2].number, z, vectorSize );
		pi.run( vectorSize );
		return pi.getDVar( piVars[3].number );
	}
	
	long totalTreeTime;
	long totalNVTime;
	long totalNSTime;
	long totalPiTime;
	int innerIter  = 100;
	int outerIter  = 500;
	int vectorSize = 256; // A realistic number for generating Minecraft chunks!
	
	protected String format( long num, int places ) {
		String s = Long.toString(num);
		while( s.length() < places ) {
			s = " "+s;
		}
		return s;
	}
	
	public void run() {
		totalTreeTime   = 0;
		totalNVTime = 0;
		totalPiTime = 0;
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
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				int k = o*innerIter+i;
				runPi( 0, k, 1, vectorSize );
			}
			et = System.currentTimeMillis();
			totalPiTime += (et - bt);
		}
	}
	
	public void printReport() {
		System.err.println("Iterations  = " + (innerIter*outerIter));
		System.err.println("Vector size = " + vectorSize);
		System.err.println("Tree time   = " + format(totalTreeTime, 6) + "ms" );
		System.err.println("NV time     = " + format(totalNVTime, 6) + "ms" );
		System.err.println("NS time     = " + format(totalNSTime, 6) + "ms" );
		System.err.println("PI time     = " + format(totalPiTime, 6) + "ms" );
		//System.err.println("Improvement   = " + ((double)totalTreeTime / totalStvkTime));
	}
	
	public static void main(String[] args) {
		PerformanceTest t = new PerformanceTest();
		t.run();
		t.printReport();
	}
}
