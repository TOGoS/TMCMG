package togos.minecraft.mapgen.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import togos.jobkernel.Service;
import togos.jobkernel.job.JobService;
import togos.jobkernel.mf.ActiveCallable;
import togos.jobkernel.mf.ActiveFunction;
import togos.jobkernel.mf.AggregatingAsyncCallable;
import togos.jobkernel.mf.AsyncCallAggregatePool;
import togos.jobkernel.mf.AsyncMultiDispatch;
import togos.jobkernel.mf.DataURICallable;
import togos.jobkernel.mf.LimitingAsyncCallable;
import togos.jobkernel.uri.ActiveRef;
import togos.mf.api.AsyncCallable;
import togos.mf.api.Callable;
import togos.mf.api.Request;
import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.api.ResponseHandler;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.world.gen.af.CompileTNLScript;
import togos.minecraft.mapgen.world.gen.af.GenerateTNLChunk;
import togos.minecraft.mapgen.world.gen.af.SerializeChunk;

public class TMCMGActiveKernel implements Callable, AsyncCallable, Service
{
	// this is to help test!!!
	public static class ReverseBytes implements ActiveFunction {
		public static final String FUNCNAME = "ReverseTheDangBytes";
		public static final ReverseBytes instance = new ReverseBytes();
		
		public Collection getRequiredResourceRefs( ActiveRef ref ) {
			HashSet s = new HashSet();
			s.add(ref.getArgument("operand"));
			return s;
        }
		
		public Response run( ActiveRef ref, Map resources ) {
			byte[] data = (byte[])resources.get(ref.getArgument("operand").getUri());
			byte[] re = new byte[data.length];
			for( int i=0; i<re.length; ++i ) {
				re[i] = data[data.length-i-1];
			}
			return new BaseResponse(ResponseCodes.NORMAL, re);
        }
		
		public Response runFast( ActiveRef ref, Map resources ) {
	        return run(ref,resources);
        }
	}
	
	Map afunx = new HashMap();
	
	DataCacheCallable dcc = new DataCacheCallable(); 
	AsyncMultiDispatch mdac = new AsyncMultiDispatch(new HashSet());
	LimitingAsyncCallable lac = new LimitingAsyncCallable(10, mdac);
	AggregatingAsyncCallable agg = new AggregatingAsyncCallable(new AsyncCallAggregatePool(), lac);
	JobService jobServ = new JobService();
	ActiveCallable ac = new ActiveCallable( afunx, this, jobServ.getJobQueue() );
	
	public TMCMGActiveKernel() {
		afunx.put(CompileTNLScript.FUNCNAME, CompileTNLScript.instance);
		afunx.put(GenerateTNLChunk.FUNCNAME, GenerateTNLChunk.instance);
		afunx.put(SerializeChunk.FUNCNAME, SerializeChunk.instance);
		afunx.put(ReverseBytes.FUNCNAME, ReverseBytes.instance);
		
		mdac.add(ac);
		mdac.add(new DataCacheCallable());
		mdac.add(DataURICallable.instance);
	}
	
	public void callAsync( Request req, ResponseHandler rHandler ) {
		agg.callAsync(req, rHandler);
	}
	
	public Response call( Request req ) {
		final ArrayBlockingQueue q = new ArrayBlockingQueue(1);
		callAsync( req, new ResponseHandler() {
			public void setResponse( Response res ) {
	            try {
	                q.put(res);
                } catch( InterruptedException e ) {
                	// Not much we can do about this!
	                Thread.currentThread().interrupt();
	                throw new RuntimeException(e);
                }
            }
		});
		try {
	        return (Response)q.take();
        } catch( InterruptedException e ) {
        	Thread.currentThread().interrupt();
	        throw new RuntimeException(e);
        }
	}
	
	public void start() {
		System.err.println(jobServ.getThreadCount()+ " job threads");
		jobServ.start();
	}
	
	public void halt() {
		jobServ.halt();
	}
}
