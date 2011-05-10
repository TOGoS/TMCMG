package togos.jobkernel.mf;

import togos.mf.api.Request;

public interface PossibleRequestHandler
{
	/**
	 * @return false if this definitely CANNOT handle the given request.
	 *   If there is a chance that it can be handled, this should return true,
	 *   as the result of this function will be used to determine which
	 *   request handlers in a chain may be skipped.
	 */
	public boolean canHandle( Request req );
}
