package togos.noise;

public class RidgePerformanceTest
{
	private static final long fastfloor(double n) {
		return n > 0 ? (long) n : (long) n - 1;
	}
	
	public void ridge1( int vectorSize, double[] lower, double[] upper, double[] in, double[] out ) {
		for( int i=vectorSize-1; i>=0; --i ) {
			double d = upper[i]-lower[i];

			// TODO: I'm guessing there's a better way to do this
			if( d == 0 ) {
				out[i] = lower[i];
			} else {
				/*
				// coorect but presumably slow:
				while( out[i] > upper[i] || out[i] < lower[i] ) {
					if( out[i] > upper[i] ) {
						out[i] = upper[i]-(out[i]-upper[i]);
					}
					if( out[i] < lower[i] ) {
						out[i] = lower[i]+(lower[i]-out[i]);
					}
				}
				*/
				
				double k = (in[i]-lower[i])/(d*2);
				double c = Math.floor(k);
				k -= c;
				out[i] = lower[i] + d*2*(k - 2*Math.floor(2*k)*(k-0.5));
			}
		}
	}
	
	/**
	 * Replaces both Math.floor(n) calls with fastfloor(n)
	 */
	public void ridge2( int vectorSize, double[] lower, double[] upper, double[] in, double[] out ) {
		for( int i=vectorSize-1; i>=0; --i ) {
			double d = upper[i]-lower[i];

			// TODO: I'm guessing there's a better way to do this
			if( d == 0 ) {
				out[i] = lower[i];
			} else {
				/*
				// coorect but presumably slow:
				while( out[i] > upper[i] || out[i] < lower[i] ) {
					if( out[i] > upper[i] ) {
						out[i] = upper[i]-(out[i]-upper[i]);
					}
					if( out[i] < lower[i] ) {
						out[i] = lower[i]+(lower[i]-out[i]);
					}
				}
				*/
				
				double k = (in[i]-lower[i])/(d*2);
				double c = fastfloor(k);
				k -= c;
				out[i] = lower[i] + d*2*(k - 2*fastfloor(2*k)*(k-0.5));
			}
		}
	}
	
	/**
	 * Change the upper <> lower check
	 */
	public void ridge3( int vectorSize, double[] lower, double[] upper, double[] in, double[] out ) {
		for( int i=vectorSize-1; i>=0; --i ) {
			// TODO: I'm guessing there's a better way to do this
			if( upper[i] > lower[i] ) {
				double d = upper[i]-lower[i];

				/*
				// coorect but presumably slow:
				while( out[i] > upper[i] || out[i] < lower[i] ) {
					if( outf[i] > upper[i] ) {
						out[i] = upper[i]-(out[i]-upper[i]);
					}
					if( out[i] < lower[i] ) {
						out[i] = lower[i]+(lower[i]-out[i]);
					}
				}
				*/
				
				double k = (in[i]-lower[i])/(d*2);
				double c = fastfloor(k);
				k -= c;
				out[i] = lower[i] + d*2*(k - 2*fastfloor(2*k)*(k-0.5));
			} else {
				out[i] = (lower[i] + upper[i]) / 2;
			}
		}
	}
	
	/**
	 * See what happens with no if() at all...
	 */
	public void ridge4( int vectorSize, double[] lower, double[] upper, double[] in, double[] out ) {
		for( int i=vectorSize-1; i>=0; --i ) {
			double d = upper[i]-lower[i];

			double k = (in[i]-lower[i])/(d*2);
			double c = fastfloor(k);
			k -= c;
			out[i] = lower[i] + d*2*(k - 2*fastfloor(2*k)*(k-0.5));
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
		
		long ridge1Time = 0;
		long ridge2Time = 0;
		long ridge3Time = 0;
		long ridge4Time = 0;
		
		for( int i=0; i<outerIterations; ++i ) {
			for( int j=0; j<vectorSize; ++j ) {
				in[j] = Math.random()*1024-512;
				lower[j] = Math.random()*1024-512;
				upper[j] = Math.random()*1024-512;
			}
			
			long beginTime, endTime;
			
			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				ridge1( vectorSize, lower, upper, in, out );
			}
			endTime = System.currentTimeMillis();
			ridge1Time += (endTime-beginTime);

			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				ridge2( vectorSize, lower, upper, in, out );
			}
			endTime = System.currentTimeMillis();
			ridge2Time += (endTime-beginTime);
			
			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				ridge3( vectorSize, lower, upper, in, out );
			}
			endTime = System.currentTimeMillis();
			ridge3Time += (endTime-beginTime);

			beginTime = System.currentTimeMillis();
			for( int j=0; j<innerIterations; ++j ) {
				ridge4( vectorSize, lower, upper, in, out );
			}
			endTime = System.currentTimeMillis();
			ridge4Time += (endTime-beginTime);
		}
		
		System.out.println("ridge1: "+format(ridge1Time,8)+"ms");
		System.out.println("ridge2: "+format(ridge2Time,8)+"ms");
		System.out.println("ridge3: "+format(ridge3Time,8)+"ms");
		System.out.println("ridge4: "+format(ridge4Time,8)+"ms");
	}
	
	public static void main(String[] args) {
		new RidgePerformanceTest().run();
	}
}
