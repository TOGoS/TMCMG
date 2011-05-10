package togos.jobkernel.mf;

import java.util.Collection;
import java.util.Map;

import togos.jobkernel.uri.ActiveRef;
import togos.mf.api.Response;

public interface ActiveFunction
{
	/**
	 * Return a collection of Ref objects
	 * @param ref
	 * @return
	 */
	public abstract Collection getRequiredResourceRefs( ActiveRef ref );
	
	/**
	 * If the response can be calculated trivially, this 
	 * function may return it.  Otherwise it should return null.
	 * @param resources map of URIs (not Refs) to resource content
	 */
	public abstract Response runFast( ActiveRef ref, Map resources );
	
	/**
	 * @param resources map of URIs (not Refs) to resource content
	 */
	public abstract Response run( ActiveRef ref, Map resources );
}
