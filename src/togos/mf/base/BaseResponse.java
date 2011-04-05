package togos.mf.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;

public class BaseResponse implements Response {
	public static final String DC_NS                    = "http://purl.org/dc/terms/";
	public static final String DC_FORMAT                = DC_NS + "format";	
	public static final BaseResponse RESPONSE_UNHANDLED = new BaseResponse(ResponseCodes.RESPONSE_UNHANDLED, "Not handled", "text/plain");
	public static final BaseResponse RESPONSE_NOTFOUND  = new BaseResponse(ResponseCodes.RESPONSE_NOTFOUND, "Not found", "text/plain");
	
	public int status;
	public Object content;

	public Map contentMetadata = Collections.EMPTY_MAP;
	protected boolean contentMetadataClean = true;
	
	public Map metadata = Collections.EMPTY_MAP;
	protected boolean metadataClean = true;


	public BaseResponse() {
		this(ResponseCodes.RESPONSE_NORMAL, null);
	}
	
	public BaseResponse(Response r) {
		this.status = r.getStatus();
		this.content = r.getContent();
		this.contentMetadata = r.getContentMetadata();
		this.contentMetadataClean = true;
		this.metadata = r.getMetadata();
		this.metadataClean = true;
	}

	public BaseResponse( int status, Object content ) {
		this.status = status;
		this.content = content;
	}

	public BaseResponse( int status, Object content, Response inheritFrom ) {
		this(status, content);
		this.metadata = inheritFrom.getMetadata();
		this.metadataClean = true;
	}
	
	public BaseResponse( int status, Object content, String contentType ) {
		this(status,content);
		putContentMetadata(DC_FORMAT, contentType);
	}

	public int getStatus() {  return status;  }
	public Object getContent() {  return content;  }
	public Map getContentMetadata() {  return contentMetadata;  }
	public Map getMetadata() {  return metadata;  }
	
	public void putMetadata(String key, Object value) {
		if( metadataClean ) {
			metadata = new HashMap(metadata);
			metadataClean = false;
		}
		metadata.put(key, value);
	}

	public void putContentMetadata(String key, Object value) {
		if( contentMetadataClean ) {
			contentMetadata = new HashMap(contentMetadata);
			contentMetadataClean = false;
		}
		contentMetadata.put(key, value);
	}
}
