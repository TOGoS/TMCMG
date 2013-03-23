package togos.noise.v3.asyncstream;

public interface StreamSource<T>
{
	public void pipe( StreamDestination<T> dest );
}
