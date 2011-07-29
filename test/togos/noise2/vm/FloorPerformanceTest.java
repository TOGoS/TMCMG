package togos.noise2.vm;

import togos.noise2.vm.dftree.func.SimplexDaDaDa_Da;
import togos.noise2.vm.dftree.func.SimplexNoise;

public class FloorPerformanceTest
{
	long totalFFTime;
	long totalFDITime;
	long totalFDLTime;
	long totalSFTime;
	long totalSDTime;
	int innerIter  = 100;
	int outerIter  = 500;
	final int vectorSize = 256; 
	
	double[] dvals = new double[vectorSize];
	float[] fvals = new float[vectorSize];
	
	SimplexDaDaDa_Da hlCalculator = new SimplexDaDaDa_Da();
	SimplexNoise llCalculator = new SimplexNoise();
	
	protected String format( long num, int places ) {
		String s = Long.toString(num);
		while( s.length() < places ) {
			s = " "+s;
		}
		return s;
	}
	
	/*
	 * Apparently fastfloor really is faster...
	 * 
	 * Iterations  = 50000
	 * Vector size = 256
	 * FD time   =     29ms
	 * SD time   =    549ms
	 * FF time   =      3ms
	 * SF time   =    566ms
	 */
	
    private static final int fastfloor(double n) {
        return n > 0 ? (int) n : (int) n - 1;
    }
	
    private static final long fastfloorlong(double n) {
        return n > 0 ? (long) n : (long) n - 1;
    }

    private static final int fastfloor(float n) {
        return n > 0 ? (int) n : (int) n - 1;
    }

    public void run() {
		for( int i=0; i<vectorSize; ++i ) {
			dvals[i] = Math.random()*1024;
			fvals[i] = (float)dvals[i];
		}
		
		totalFFTime = 0;
		totalFDITime = 0;
		totalFDLTime = 0;
		totalSFTime = 0;
		totalSDTime = 0;
		long bt, et;
		for( int o=0; o<outerIter; ++o ) {
			// Doubles
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				for( int j=0; j<vectorSize; ++j ) {
					Math.floor( dvals[i] );
				}
			}
			et = System.currentTimeMillis();
			totalSDTime += (et - bt);
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				for( int j=0; j<vectorSize; ++j ) {
					fastfloor( dvals[i] );
				}
			}
			et = System.currentTimeMillis();
			totalFDITime += (et - bt);
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				for( int j=0; j<vectorSize; ++j ) {
					fastfloorlong( dvals[i] );
				}
			}
			et = System.currentTimeMillis();
			totalFDLTime += (et - bt);
			
			// Floats
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				for( int j=0; j<vectorSize; ++j ) {
					Math.floor( fvals[i] );
				}
			}
			et = System.currentTimeMillis();
			totalSFTime += (et - bt);
			
			bt = System.currentTimeMillis();
			for( int i=0; i<innerIter; ++i ) {
				for( int j=0; j<vectorSize; ++j ) {
					fastfloor( fvals[i] );
				}
			}
			et = System.currentTimeMillis();
			totalFFTime += (et - bt);
		}
	}
	
	public void printReport() {
		System.err.println("Iterations  = " + (innerIter*outerIter));
		System.err.println("Vector size = " + vectorSize);
		System.err.println("FDI time  = " + format(totalFDITime, 6) + "ms" );
		System.err.println("FDL time  = " + format(totalFDLTime, 6) + "ms" );
		System.err.println("SD time   = " + format(totalSDTime, 6) + "ms" );
		System.err.println("FF time   = " + format(totalFFTime, 6) + "ms" );
		System.err.println("SF time   = " + format(totalSFTime, 6) + "ms" );
		//System.err.println("Improvement   = " + ((double)totalTreeTime / totalStvkTime));
	}
	
	public static void main(String[] args) {
		FloorPerformanceTest t = new FloorPerformanceTest();
		t.run();
		t.printReport();
	}
}
