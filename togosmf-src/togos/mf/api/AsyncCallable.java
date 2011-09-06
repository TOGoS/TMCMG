package togos.mf.api;

public interface AsyncCallable
{
	public void callAsync( Request req, ResponseHandler rHandler );
}
