package togos.noise.v3.asyncstream;

import java.util.ArrayList;

public class BaseStreamSource<T> implements StreamSource<T>
{
	ArrayList<StreamDestination<T>> pipes = new ArrayList<StreamDestination<T>>();
	
	public void pipe( StreamDestination<T> dest ) {
		pipes.add(dest);
	}
	
	public void _data( T value ) throws Exception {
		for( StreamDestination<T> dest : pipes ) {
			dest.data( value );
		}
	}
	
	public void _end() throws Exception {
		for( StreamDestination<T> dest : pipes ) {
			dest.end();
		}
	}
}
