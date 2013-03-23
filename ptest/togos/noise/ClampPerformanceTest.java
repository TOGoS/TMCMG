package togos.noise;

public class ClampPerformanceTest
{
	public void clamp1( int vectorSize, double[] lower, double[] upper, double[] in, double[] out ) {
		for( int i=vectorSize-1; i>=0; --i ) {
			if( in[i] < lower[i] ) out[i] = lower[i];
			else if( in[i] > upper[i] ) out[i] = upper[i];
			else out[i] = in[i];
		}
	}
	
	public void clamp2( int vectorSize, double[] lower, double[] upper, double[] in, double[] out ) {
		for( int i=vectorSize-1; i>=0; --i ) {
			out[i] = (in[i] < upper[i]) ? (in[i] > lower[i]) ? in[i] : lower[i] : upper[i];
		}
	}
	
	public void clamp3( int vectorSize, double[] lower, double[] upper, double[] in, double[] out ) {
		for( int i=vectorSize-1; i>=0; --i ) {
			out[i] = Math.max(Math.min(in[i],upper[i]),lower[i]);
		}
	}
	
	protected static String pad( String s, int places ) {
		while( s.length() < places ) {
			s = " "+s;
		}
		return s;
	}
	
	protected static String format( long num, int places ) {
		return pad(Long.toString(num), places);
	}
	
	int outerIterations = 200;
	int innerIterations = 200;
	int vectorSize = 256;
	
	public void run() {
		double[] in = new double[vectorSize];
		double[] lower = new double[vectorSize];
		double[] upper = new double[vectorSize];
		double[] out = new double[vectorSize];
		
		long clamp1Time = 0;
		long clamp2Time = 0;
		long clamp3Time = 0;
		
		for( int i=0; i<outerIterations; ++i ) {
			for( int j=0; j<vectorSize; ++j ) {
				in[j] = Math.random()*1024-512;
				lower[j] = Math.random()*1024-512;
				upper[j] = Math.random()*1024-512;
			}
			
			long beginTime, endTime;
			
			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				clamp1( vectorSize, lower, upper, in, out );
			}
			endTime = System.currentTimeMillis();
			clamp1Time += (endTime-beginTime);

			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				clamp2( vectorSize, lower, upper, in, out );
			}
			endTime = System.currentTimeMillis();
			clamp2Time += (endTime-beginTime);
			
			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				clamp3( vectorSize, lower, upper, in, out );
			}
			endTime = System.currentTimeMillis();
			clamp3Time += (endTime-beginTime);
		}
		
		System.out.println("clamp1: "+format(clamp1Time,8)+"ms");
		System.out.println("clamp2: "+format(clamp2Time,8)+"ms");
		System.out.println("clamp3: "+format(clamp3Time,8)+"ms");
	}
	
	public static void main(String[] args) {
		new ClampPerformanceTest().run();
	}
}
