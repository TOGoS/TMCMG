package togos.mf.api;

public class ResponseCodes {
	/**
	 * 000 and 004 are non-authoritative responses.
	 * They indicate that the named resource could not be found or produced
	 * by the handler, but without stating that there was an actual error or
	 * that the resource necessarily does not exist. 
	 * i.e. some other handler may be able to produce the resource.
	 * 
	 * RESPONSE_UNHANDLED indicates that this server does not even attempt to
	 * resolve the requested resource.
	 * 
	 * RESPONSE_NOTFOUND indicates that the server normally can handle the type
	 * of URI given but was unable to handle this one specifically.
	 */
	public static final int RESPONSE_UNHANDLED = 000;
	public static final int RESPONSE_NOTFOUND = 004;
	
	/** Returned when the request was fulfilled successfully. */
	public static final int RESPONSE_NORMAL = 200;
	
	/** Returned when the request was fulfilled successfully
	 * but the response is empty or does not exist. */
	public static final int RESPONSE_NONE = 204;
	
	public static final int RESPONSE_CALLER_ERROR = 400;
	public static final int RESPONSE_DOESNOTEXIST = 404;
	
	/**
	 * The handler encountered an unexpected internal error.
	 */
	public static final int RESPONSE_ERROR = 500;
}
