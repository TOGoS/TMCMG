package togos.minecraft.mapgen.job;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import togos.mf.api.RequestVerbs;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.base.BaseRequest;
import togos.minecraft.mapgen.http.HTTPClient;
import togos.minecraft.mapgen.server.URIUtil;
import togos.minecraft.mapgen.util.ServiceManager;
import togos.minecraft.mapgen.util.Util;
import togos.service.Service;

public class RemoteJobService extends ServiceManager
{
	static class RemoteJobRunner implements Runnable, Service
	{
		BlockingQueue jobQueue;
		String resolverUrl;
		HTTPClient client = new HTTPClient();
		
		public RemoteJobRunner( BlockingQueue jobQueue, String url ) {
			this.jobQueue = jobQueue;
			this.resolverUrl = url;
		}
		
		protected String n2l( String urn ) {
			return resolverUrl+"?"+URIUtil.uriEncode(urn);
		}
		
		public void run() {
			HashSet sentResources = new HashSet();
			
			while( true ) {
				RemoteJob job;
				try {
					job = (RemoteJob)jobQueue.take();
				} catch( InterruptedException e ) {
					// Then I guess we're done!
					Thread.currentThread().interrupt();
					return;
				}
				
				for( Iterator i=job.getRequiredResources().entrySet().iterator(); i.hasNext(); ) {
					Map.Entry e = (Map.Entry)i.next();
					String resUri = (String)e.getKey();
					if( !sentResources.contains(resUri) ) {
						client.call( new BaseRequest(RequestVerbs.PUT,n2l(resUri),e.getValue(),Collections.EMPTY_MAP) );
						sentResources.add(resUri);
					}
				}
				
				String resourceUrn = job.getResourceRef().getUri();
				String resourceUrl = n2l(resourceUrn);
				Response res = client.call( new BaseRequest(RequestVerbs.GET,resourceUrl) );
				if( res.getStatus() != ResponseCodes.NORMAL ) {
					System.err.println("Remote job at "+resolverUrl+" failed: "+res.getStatus()+" ("+resourceUrl+")");
					System.err.println("Putting the job back in the queue and quitting...");
					jobQueue.add(job);
					return;
				}
				
				job.setResourceData( Util.byteBuffer(res.getContent()) );
			}
		}
		
		Thread t;
		
		public synchronized void halt() {
			if( t == null ) return;
			t.interrupt();
			t = null;
		}
		
		public void start() {
			if( t != null ) return;
			t = new Thread(this);
			t.start();
		}
	}
	
	BlockingQueue jobQueue;
	
	public RemoteJobService( BlockingQueue jobQueue ) {
		super();
		this.jobQueue = jobQueue;
	}
	
	public void addWebResolver( String url, int threads ) {
		for( int i=0; i<threads; ++i ) {
			add( new RemoteJobRunner(jobQueue,url) );
		}
	}
}
