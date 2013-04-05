package togos.noise.v3.asyncstream;

import java.io.Reader;
import java.util.Arrays;

public final class StreamUtil
{
	private StreamUtil() { }
	
	public static void pipe( Reader r, StreamDestination<char[]> d, boolean includeEnd ) throws Exception {
		char[] buffer = new char[2048];
		int i;
		while( (i = r.read(buffer)) > 0 ) {
			d.data( Arrays.copyOf(buffer, i) );
		}
		if( includeEnd ) d.end();
	}
	
	public static void pipe( Reader r, StreamDestination<char[]> d ) throws Exception {
		pipe( r, d, true );
	}
}
