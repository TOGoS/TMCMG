package togos.noise;

import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.func.SimplexDaDaDa_Da;
import togos.noise.v1.func.SimplexNoise;
import togos.noise.v1.func.SimplexNoise2;

public class SimplexPerformanceTest
{
	long totalLSTime;
	long totalLVTime;
	long totalL2VTime;
	long totalHVTime;
	int innerIter  = 100;
	int outerIter  = 500;
	int vectorSize = 256;
	
	SimplexDaDaDa_Da hlCalculator = new SimplexDaDaDa_Da();
	SimplexNoise llCalculator = new SimplexNoise();
	SimplexNoise2 llCalculator2 = new SimplexNoise2();
	
	protected String format( long num, int places ) {
		String s = Long.toString(num);
		while( s.length() < places ) {
			s = " "+s;
		}
		return s;
	}
	
	double[] x, y, z, dest;
	DataDaDaDa hlInput;
	
	protected void setUp( int vectorSize ) {
		x = new double[vectorSize];
		y = new double[vectorSize];
		z = new double[vectorSize];
		dest = new double[vectorSize];
		for( int i=0; i<vectorSize; ++i ) {
			x[i] = Math.random()*1024;
			y[i] = Math.random()*1024;
			z[i] = Math.random()*1024;
		}
		hlInput = new DataDaDaDa(vectorSize, x,y,z);
	}
	
	public void run() {
		totalLSTime   = 0;
		totalLVTime   = 0;
		totalHVTime = 0;
		setUp(vectorSize);
		long bt, et;
		for( int o=0; o<outerIter; ++o ) {
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				for( int j=0; j<vectorSize; ++j ) {
					dest[j] = llCalculator.apply( (float)x[j], (float)y[j], (float)z[j] );
				}
			}
			et = System.currentTimeMillis();
			totalLSTime += (et - bt);
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				llCalculator.apply( vectorSize, x, y, z, dest );
			}
			et = System.currentTimeMillis();
			totalLVTime += (et - bt);
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				llCalculator2.apply( vectorSize, x, y, z, dest );
			}
			et = System.currentTimeMillis();
			totalL2VTime += (et - bt);

			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				hlCalculator.apply(hlInput);
			}
			et = System.currentTimeMillis();
			totalHVTime += (et - bt);
		}
	}
	
	public void printReport() {
		System.err.println("Iterations  = " + (innerIter*outerIter));
		System.err.println("Vector size = " + vectorSize);
		System.err.println("LS time   = " + format(totalLSTime, 6) + "ms" );
		System.err.println("LV time   = " + format(totalLVTime, 6) + "ms" );
		System.err.println("L2V time  = " + format(totalL2VTime, 6) + "ms" );
		System.err.println("HV time   = " + format(totalHVTime, 6) + "ms" );
		//System.err.println("Improvement   = " + ((double)totalTreeTime / totalStvkTime));
	}
	
	public static void main(String[] args) {
		SimplexPerformanceTest t = new SimplexPerformanceTest();
		t.run();
		t.printReport();
	}
}
