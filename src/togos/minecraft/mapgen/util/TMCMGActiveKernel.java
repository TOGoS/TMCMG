package togos.minecraft.mapgen.util;

import java.util.HashMap;
import java.util.Map;

import togos.jobkernel.job.JobService;
import togos.jobkernel.mf.ActiveCallable;
import togos.jobkernel.mf.AggregatingAsyncCallable;
import togos.jobkernel.mf.AsyncCallAggregatePool;
import togos.jobkernel.mf.DataURICallable;
import togos.jobkernel.mf.LimitingAsyncCallable;
import togos.jobkernel.mf.MultiDispatchAsyncCallable;
import togos.mf.api.AsyncCallable;
import togos.mf.api.Request;
import togos.mf.api.ResponseHandler;
import togos.minecraft.mapgen.world.gen.af.CompileTNLScript;
import togos.minecraft.mapgen.world.gen.af.GenerateTNLChunk;
import togos.minecraft.mapgen.world.gen.af.SerializeChunk;

public class TMCMGActiveKernel implements AsyncCallable, Service
{
	Map afunx = new HashMap();
	
	MultiDispatchAsyncCallable mdac = new MultiDispatchAsyncCallable();
	LimitingAsyncCallable lac = new LimitingAsyncCallable(10, mdac);
	AggregatingAsyncCallable agg = new AggregatingAsyncCallable(new AsyncCallAggregatePool(), lac);
	JobService jobServ = new JobService();
	ActiveCallable ac = new ActiveCallable( afunx, this, jobServ.getJobQueue() );
	
	public TMCMGActiveKernel() {
		afunx.put(CompileTNLScript.FUNCNAME, CompileTNLScript.instance);
		afunx.put(GenerateTNLChunk.FUNCNAME, GenerateTNLChunk.instance);
		afunx.put(SerializeChunk.FUNCNAME, SerializeChunk.instance);
		
		mdac.addCallable(ac);
		mdac.addCallable(DataURICallable.instance);
	}
	
	public void callAsync( Request req, ResponseHandler rHandler ) {
		//System.err.println(" Handling "+req.getResourceName());
		agg.callAsync(req, rHandler);
	}
	
	public void start() {
		System.err.println(jobServ.getThreadCount()+ " job threads");
		jobServ.start();
	}
	
	public void halt() {
		jobServ.halt();
	}
}
