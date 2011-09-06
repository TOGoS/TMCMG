package togos.mf.api;

public interface Message
{
	/**
	 * Indicates the role of this message's payload.  See MessageTypes.
	 **/
	public int getMessageType();
	/**
	 * For linking request messages and response messages.
	 * Should be zero for stand-alone message
	 **/
	public long getSessionId();
	public Object getPayload();
}
