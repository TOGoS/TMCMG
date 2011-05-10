package togos.jobkernel.uri;

public class BaseRef extends AbstractRef
{
	protected final String uri;
	
	public BaseRef( String uri ) {
		this.uri = uri;
	}
	
	public String getUri() {
		return uri;
	}
}
