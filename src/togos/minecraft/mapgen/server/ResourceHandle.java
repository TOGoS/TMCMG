package togos.minecraft.mapgen.server;

import togos.mf.api.Response;

public class ResourceHandle
{
	final String chunkId;
	volatile Response target;
	
	public ResourceHandle( String chunkId ) {
		this.chunkId = chunkId;
	}
	
	public void setTarget( Response target ) {
		this.target = target;
	}
	
	public Response getTarget() {
		return this.target;
	}
	
	public int hashCode() {
		return chunkId.hashCode();
	}
	
	public boolean equals( Object other ) {
		if( other instanceof ResourceHandle ) {
			return chunkId.equals(((ResourceHandle)other).chunkId);
		}
		return false;
	}
}
