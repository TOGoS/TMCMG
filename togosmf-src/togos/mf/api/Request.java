package togos.mf.api;

import java.util.Map;

public interface Request extends ContentAndMetadata {
	public String getVerb();
	public String getResourceName();
	public Object getContent();
	public Map getContentMetadata();
	public Map getMetadata();
}
