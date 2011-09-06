package togos.mf.io;

import java.io.IOException;

import togos.mf.api.Message;

public interface MessageReader
{
	public Message readMessage() throws IOException ;
}
