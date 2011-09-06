package togos.mf.base;

import togos.mf.api.Message;

public class BaseMessage implements Message
{
	protected int messageType;
	protected long sessionId;
	protected Object payload;
	
	public BaseMessage( int type, long id, Object payload ) {
		this.messageType = type;
		this.sessionId = id;
		this.payload = payload;
	}
	
	public int getMessageType() {
		return messageType;
	}
	
	public long getSessionId() {
		return sessionId;
	}
	
	public Object getPayload() {
		return payload;
	}
	
	public int hashCode() {
		return Util.hashCode(this);
	}
	
	public boolean equals( Object oth ) {
		if( oth instanceof Message ) {
			return Util.equals( this, (Message)oth );
		}
		return false;
	}
}
