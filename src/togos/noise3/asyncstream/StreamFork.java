package togos.noise3.asyncstream;

public class StreamFork<T> extends BaseStreamSource<T> implements StreamDestination<T>
{
	@Override public void data( T value ) throws Exception {
		_data( value );
    }

	@Override public void end() throws Exception {
		_end();
    }
}
