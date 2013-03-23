package togos.noise.v3.asyncstream;

public interface StreamDestination<T>
{
	public void data( T value ) throws Exception;
	public void end() throws Exception;
}
