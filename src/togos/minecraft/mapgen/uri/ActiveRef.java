package togos.minecraft.mapgen.uri;

import java.util.List;

import togos.mf.value.URIRef;

public interface ActiveRef extends URIRef
{
	public String getFunctionName();
	public List getArgumentPairs();
	public URIRef getArgument( String name );
	public URIRef requireArgument( String name );
	public List getArguments( String name );
}
