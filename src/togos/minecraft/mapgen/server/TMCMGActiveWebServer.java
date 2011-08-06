package togos.minecraft.mapgen.server;

import togos.jobkernel.uri.URIUtil;
import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.base.BaseRequest;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.util.TMCMGActiveKernel;

/**
 * It handles 
 * @author stevens
 *
 */
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
	
	TMCMGActiveKernel kernel;
	
	public TMCMGActiveWebServer() {
		addRequestHandler( new N2RTranslator( kernel = new TMCMGActiveKernel() ) );
	}
	
	public void start() {
		kernel.start();
		super.start();
	}
	
	public void halt() {
		super.halt();
		kernel.halt();
	}
	
	public static void main(String[] args) {
		TMCMGActiveWebServer ws = new TMCMGActiveWebServer();
		System.err.println("Running web server on port "+ws.port);
		ws.run();
	}
}
