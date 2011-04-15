package togos.minecraft.mapgen.world.gen.af;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;
import togos.jobkernel.job.JobService;
import togos.jobkernel.mf.ActiveCallable;
import togos.jobkernel.mf.Active;
import togos.jobkernel.mf.AggregatingAsyncCallable;
import togos.jobkernel.mf.AsyncCallAggregatePool;
import togos.jobkernel.mf.DataURICallable;
import togos.jobkernel.mf.MultiDispatchAsyncCallable;
import togos.jobkernel.uri.BaseRef;
import togos.jobkernel.uri.URIUtil;
import togos.mf.api.Response;
import togos.mf.api.ResponseHandler;
import togos.mf.value.URIRef;
import togos.minecraft.mapgen.world.Materials;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class GenerateTNLChunkTest extends TestCase
{
	// TODO: Use TMCMGActiveKernel for this stuffff
	
	Map afunx = new HashMap();
	
	MultiDispatchAsyncCallable mdac = new MultiDispatchAsyncCallable();
	AggregatingAsyncCallable agg = new AggregatingAsyncCallable(new AsyncCallAggregatePool(), mdac);
	JobService jobServ = new JobService( 2 );
	ActiveCallable ac = new ActiveCallable( afunx, agg, jobServ.getJobQueue() );
	
	public void setUp() {
		afunx.put(CompileTNLScript.FUNCNAME, CompileTNLScript.instance);
		afunx.put(GenerateTNLChunk.FUNCNAME, GenerateTNLChunk.instance);
		
		mdac.addCallable(ac);
		mdac.addCallable(DataURICallable.instance);
		
		jobServ.start();
	}
	
	public void tearDown() {
		jobServ.halt();
	}
	
	public void testGenerateSomeChunks() throws InterruptedException {
		int gcount = 12;
		
		final BlockingQueue q1 = new LinkedBlockingQueue();
		
		String script = "seed = 123 ; layered-terrain( " +
			"  layer(materials.stone, 0, 64 + fractal(6,2,2,2,2,seed,simplex)),\n" +
			"  layer(materials.bedrock, 0, 1),\n" +
			")";
		BaseRef scriptRef = new BaseRef(URIUtil.makeDataUri(script));
		
		for( int i=0; i<gcount; ++i ) {
			URIRef chunkRef = GenerateTNLChunk.makeRef(scriptRef, i*16, 0, i*32, 16, 128, 16);
			mdac.callAsync(Active.mkRequest(chunkRef), new ResponseHandler() {
				public void setResponse( Response res ) {
					q1.add(res);
				}
			});
		}
		
		int stoneNumber   = Materials.getByName("Stone").blockType;
		int bedrockNumber = Materials.getByName("Bedrock").blockType;
		int airNumber     = Materials.getByName("Air").blockType;
		
		for( int i=0; i<gcount; ++i ) {
			Response res = (Response)q1.take();
			if( res.getContent() instanceof Exception ) {
				throw new RuntimeException((Exception)res.getContent());
			}
			ChunkData cd = (ChunkData)res.getContent();
			assertEquals( 16*16*128, cd.blockData.length );
			assertEquals( 0, cd.getChunkPositionY() );
			assertEquals( cd.getChunkPositionX() * 2, cd.getChunkPositionZ() );
			assertEquals( bedrockNumber, cd.blockData[0] );
			assertEquals( stoneNumber, cd.blockData[1] );
			assertEquals( airNumber, cd.blockData[127] );
		}
	}
}
