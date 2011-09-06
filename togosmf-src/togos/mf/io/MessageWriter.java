package togos.mf.io;

import java.io.IOException;

import togos.mf.api.Message;

public interface MessageWriter
{
	public void writeMessage( Message message ) throws IOException;
}
