package togos.mf.api;

import java.util.Map;

public interface Response extends ContentAndMetadata {
	public int getStatus();
	public Object getContent();
	public Map getContentMetadata();
	public Map getMetadata();
}
