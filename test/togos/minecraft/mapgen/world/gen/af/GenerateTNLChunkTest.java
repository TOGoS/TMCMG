package togos.minecraft.mapgen.world.gen.af;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import junit.framework.TestCase;
import togos.mf.api.RequestVerbs;
import togos.mf.api.Response;
import togos.mf.base.BaseRequest;
import togos.mf.value.URIRef;
import togos.minecraft.mapgen.mf.ActiveCallable;
import togos.minecraft.mapgen.mf.DataURICallable;
import togos.minecraft.mapgen.mf.MultiDispatch;
import togos.minecraft.mapgen.mf.UnifyingCallable;
import togos.minecraft.mapgen.uri.BaseRef;
import togos.minecraft.mapgen.uri.URIUtil;
import togos.minecraft.mapgen.world.Materials;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class GenerateTNLChunkTest extends TestCase
{
	Map afunx = new HashMap();
	
	MultiDispatch mdac = new MultiDispatch(new HashSet());
	UnifyingCallable agg = new UnifyingCallable(mdac);
	ActiveCallable ac = new ActiveCallable( agg, afunx );
	
	public void setUp() {
		afunx.put(CompileTNLScript.FUNCNAME, CompileTNLScript.instance);
		afunx.put(GenerateTNLChunk.FUNCNAME, GenerateTNLChunk.instance);
		
		mdac.add(ac);
		mdac.add(DataURICallable.instance);
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
			q1.add(mdac.call(new BaseRequest(RequestVerbs.GET,chunkRef.getUri())));
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
