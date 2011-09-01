package togos.minecraft.mapgen.job;

import java.util.Map;

import togos.mf.value.ByteChunk;
import togos.mf.value.URIRef;

public interface RemoteJob
{
	public URIRef getResourceRef();
	/**
	 * Return map of URI:String => data:ByteBuffer of resources that must
	 * be sent to the remote server for it to be able to calculate
	 * */
	public Map getRequiredResources();
	public void setResourceData( ByteChunk data );
}
