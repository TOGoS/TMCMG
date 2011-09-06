package togos.mf.api;

public class ResponseCodes
{
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
	public static final int UNHANDLED = 000;
	public static final int NOT_FOUND = 004;
	
	/** Returned when the request was fulfilled successfully. */
	public static final int NORMAL = 200;
	
	/** Returned when the request was fulfilled successfully
	 * but the response is empty or does not exist. */
	public static final int NORMAL_NO_RESPONSE = 204;
	
	public static final int CALLER_ERROR = 400;
	public static final int DOES_NOT_EXIST = 404;
	
	/**
	 * The handler encountered an unexpected internal error.
	 */
	public static final int SERVER_ERROR = 500;
}
