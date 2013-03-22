package togos.noise3.asyncstream;

public interface StreamSource<T>
{
	public void pipe( StreamDestination<T> dest );
}
