package togos.minecraft.mapgen.server;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import togos.jobkernel.mf.DataURICallable;
import togos.jobkernel.uri.URIUtil;
import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.RequestVerbs;
import togos.mf.api.Response;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.mf.ActiveCallable;
import togos.minecraft.mapgen.mf.MultiDispatch;
import togos.minecraft.mapgen.resource.ResourceHandle;
import togos.minecraft.mapgen.util.DataCacheCallable;
import togos.minecraft.mapgen.util.TMCMGActiveKernel.ReverseBytes;
import togos.minecraft.mapgen.world.gen.af.CompileTNLScript;
import togos.minecraft.mapgen.world.gen.af.GenerateTNLChunk;
import togos.minecraft.mapgen.world.gen.af.SerializeChunk;

public class TMCMGActiveWebServer extends WebServer
{
	static class N2RTranslator implements Callable {
		Callable next;
		
		public N2RTranslator( Callable next ) {
			this.next = next;
		}
		
		public Response call( Request req ) {
			if( req.getResourceName().startsWith("/N2R?") ) {
				String urn = URIUtil.uriDecode( req.getResourceName().substring(5) );
				return next.call( new BaseRequest( req.getVerb(), urn, req.getContent(), req.getContentMetadata() ) );
			} else {
				return BaseResponse.RESPONSE_UNHANDLED;
			}
        }
	}
	
	class RequestUnifier implements Callable {
		Callable next;
		WeakHashMap handling = new WeakHashMap();
		
		public RequestUnifier( Callable next ) {
			this.next = next;
		}
		
		protected synchronized ResourceHandle getHandle( String name ) {
			ResourceHandle h = new ResourceHandle(name);
			
			Reference hr = (Reference)handling.get(h);
			if( hr == null || (hr).get() == null ) {
				handling.put(name, new WeakReference(h));
			}
			return h;
		}
		
		public Response call( Request req ) {
			if( RequestVerbs.GET.equals(req.getVerb()) ) {
				ResourceHandle rh = getHandle(req.getResourceName());
				if( rh.getResolvePermission() ) {
					rh.setValue(next.call(req));
				}
				try {
	                return (Response)rh.waitForValue();
                } catch( InterruptedException e ) {
                	Thread.currentThread().interrupt();
                	throw new RuntimeException(e);
                }
			} else {
				return next.call( req );
			}
		}
	}
	
	public TMCMGActiveWebServer() {
		MultiDispatch md = new MultiDispatch();
		Callable rootCallable = new RequestUnifier(md);
		
		Map afunx = new HashMap();
		afunx.put(CompileTNLScript.FUNCNAME, CompileTNLScript.instance);
		afunx.put(GenerateTNLChunk.FUNCNAME, GenerateTNLChunk.instance);
		afunx.put(SerializeChunk.FUNCNAME, SerializeChunk.instance);
		afunx.put(ReverseBytes.FUNCNAME, ReverseBytes.instance);
		
		md.add(new DataCacheCallable());
		md.add(new ActiveCallable(rootCallable, afunx));
		md.add(DataURICallable.instance);
		
		addRequestHandler( new N2RTranslator( rootCallable ) );
	}
	
	public void start() {
		super.start();
	}
	
	public void halt() {
		super.halt();
	}
	
	public static void main(String[] args) {
		TMCMGActiveWebServer ws = new TMCMGActiveWebServer();
		for( int i=0; i<args.length; ++i ) {
			if( "-port".equals(args[i]) ) {
				ws.port = Integer.parseInt(args[++i]);
			} else {
				throw new RuntimeException("Unrecognised argument: "+args[i]);
			}
		}
		System.err.println("Running web server on port "+ws.port);
		ws.run();
	}
}
