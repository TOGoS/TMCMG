package togos.minecraft.mapgen.uri;

import togos.mf.value.URIRef;

public abstract class AbstractRef implements URIRef
{
	public String toString() {
		return getUri();
	}
	
	public boolean equals( Object o ) {
		return (o instanceof URIRef && getUri().equals(((URIRef)o).getUri()) );
	}
	
	public int hashCode() {
		return getUri().hashCode() + 1;
	}
}
