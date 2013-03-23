package togos.noise;

public class LoopPerformanceTest
{
	public void loop1( int vectorSize, double[] in, double[] out ) {
		for( int i=0; i<vectorSize; ++i ) {
			out[i] = in[i]+1;
		}
	}
	
	// This one is marginally faster when vectorSize = 256,
	// but is 3-4x as fast when vectorSize >= 1024.
	public void loop2( int vectorSize, double[] in, double[] out ) {
		for( int i=vectorSize-1; i>=0; --i ) {
			out[i] = in[i]+1;
		}
	}
	
	public void loop3( int vectorSize, double[] in, double[] out ) {
		for( int i=0; i<in.length; ++i ) {
			out[i] = in[i]+1;
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
	
	int outerIterations = 2000;
	int innerIterations = 1000;
	int vectorSize = 1024;
	
	public void run() {
		double[] in = new double[vectorSize];
		double[] out = new double[vectorSize];
		
		long loop1Time = 0;
		long loop2Time = 0;
		long loop3Time = 0;
		
		for( int i=0; i<outerIterations; ++i ) {
			for( int j=0; j<vectorSize; ++j ) {
				in[j] = Math.random()*1024-512;
			}
			
			long beginTime, endTime;
			
			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				loop1( vectorSize, in, out );
			}
			endTime = System.currentTimeMillis();
			loop1Time += (endTime-beginTime);

			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				loop2( vectorSize, in, out );
			}
			endTime = System.currentTimeMillis();
			loop2Time += (endTime-beginTime);
			
			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				loop3( vectorSize, in, out );
			}
			endTime = System.currentTimeMillis();
			loop3Time += (endTime-beginTime);
		}
		
		System.out.println("loop1: "+format(loop1Time,8)+"ms");
		System.out.println("loop2: "+format(loop2Time,8)+"ms");
		System.out.println("loop3: "+format(loop3Time,8)+"ms");
	}
	
	public static void main(String[] args) {
		new LoopPerformanceTest().run();
	}
}
