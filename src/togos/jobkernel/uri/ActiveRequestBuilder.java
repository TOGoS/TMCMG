package togos.jobkernel.uri;

import togos.mf.api.Request;
import togos.mf.value.URIRef;

public interface ActiveRequestBuilder
{
	public ActiveRequestBuilder create(String functionName);
	public ActiveRequestBuilder with(String argName, URIRef value);
	public ActiveRef toRef();
	public Request toRequest();
}
