package togos.minecraft.mapgen.uri;

import togos.mf.value.URIRef;

public interface ActiveRequestBuilder
{
	public ActiveRequestBuilder create(String functionName);
	public ActiveRequestBuilder with(String argName, URIRef value);
	public ActiveRef toRef();
}
