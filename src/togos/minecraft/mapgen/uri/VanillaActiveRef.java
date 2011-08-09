package togos.minecraft.mapgen.uri;

import java.util.List;

public class VanillaActiveRef extends BaseActiveRef
{
	final String uri;
	
	public String getUri() {
		return uri;
	}
	
	public VanillaActiveRef( String uri, String functionName, List arguments ) {
		super( functionName, arguments );
		this.uri = uri;
	}
}
