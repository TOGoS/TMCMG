package togos.minecraft.mapgen.server;

import java.util.HashMap;
import java.util.Map;

import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.mf.ActiveCallable;
import togos.minecraft.mapgen.mf.DataURICallable;
import togos.minecraft.mapgen.mf.MultiDispatch;
import togos.minecraft.mapgen.mf.UnifyingCallable;
import togos.minecraft.mapgen.uri.URIUtil;
import togos.minecraft.mapgen.util.DataCacheCallable;
import togos.minecraft.mapgen.util.ReverseBytes;
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
	
	public TMCMGActiveWebServer() {
		MultiDispatch md = new MultiDispatch();
		Callable rootCallable = new UnifyingCallable(md);
		
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
