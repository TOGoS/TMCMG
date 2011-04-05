package togos.mf.api;

import java.util.Map;

public interface Event extends ContentAndMetadata {
	public String getEventName();
	public Map getMetadata();
}
