package togos.minecraft.mapgen.world.gen.af;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import togos.mf.api.Response;
import togos.mf.base.BaseResponse;
import togos.mf.value.URIRef;
import togos.minecraft.mapgen.TMCMGNamespace;
import togos.minecraft.mapgen.mf.ActiveFunction;
import togos.minecraft.mapgen.uri.Active;
import togos.minecraft.mapgen.uri.ActiveRef;
import togos.minecraft.mapgen.uri.ActiveRequestBuilder;
import togos.minecraft.mapgen.uri.BaseRef;
import togos.minecraft.mapgen.uri.URIUtil;
import togos.minecraft.mapgen.util.Util;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class GenerateTNLChunk implements ActiveFunction
{
	public static final GenerateTNLChunk instance = new GenerateTNLChunk();
	public static final String FUNCNAME = TMCMGNamespace.NS+"/Functions/GenerateTNLChunk";
	public static final String SCRIPT_ARGNAME = FUNCNAME+"script";
	public static final String COORDS_ARGNAME = FUNCNAME+"chunkCoords";
	
	public static final ActiveRequestBuilder buildRef( URIRef scriptRef, URIRef coordsRef ) {
		return Active.build( FUNCNAME ).with(SCRIPT_ARGNAME, scriptRef).with(COORDS_ARGNAME, coordsRef);	
	}
	
	public static final URIRef makeRef( URIRef scriptRef, URIRef coordsRef ) {
		return buildRef( scriptRef, coordsRef ).toRef();
	}
	
	public static final URIRef makeCoordsRef( long x, long y, long z, int w, int h, int d ) {
		return new BaseRef(URIUtil.makeDataUri(x+","+y+","+z+","+w+","+h+","+d));
	}
	
	public static final URIRef makeRef( URIRef scriptRef, long x, long y, long z, int w, int h, int d ) {
		return makeRef( scriptRef, makeCoordsRef(x,y,z,w,h,d) );
	}
	
	protected static final URIRef compiledScriptRef( ActiveRef ref ) {
		return CompileTNLScript.makeRef(ref.requireArgument(SCRIPT_ARGNAME));
	}
	
	public Collection getRequiredResourceRefs( ActiveRef ref ) {
		ArrayList args = new ArrayList(2);
		args.add( compiledScriptRef(ref) );
		args.add( ref.requireArgument(COORDS_ARGNAME) );
		return args;
	}
	
	public Response runFast( ActiveRef ref, Map resources ) {
	    return null;
	}
	
	public Response run( ActiveRef ref, Map resources ) {
		Object script = resources.get( compiledScriptRef(ref).getUri() );
		String coords = Util.string( resources.get(ref.requireArgument(COORDS_ARGNAME).getUri()) );
		String[] coordStrs = coords.split(",");
		long x = Long.parseLong(coordStrs[0]);
		long y = Long.parseLong(coordStrs[1]);
		long z = Long.parseLong(coordStrs[2]);
		int w = Integer.parseInt(coordStrs[3]);
		int h = Integer.parseInt(coordStrs[4]);
		int d = Integer.parseInt(coordStrs[5]);
		
		ChunkMunger cm;
		if( script instanceof WorldGenerator ) {
			cm = ((WorldGenerator)script).getChunkMunger();
		} else {
			throw new RuntimeException("Don't know how to get ChunkMunger from a "+script.getClass());
		}
		
		ChunkData cd = new ChunkData(x,y,z,w,h,d);
		cm.mungeChunk(cd);
		
		// System.err.println("Munging chunk at "+x+","+y+","+z);
	    return BaseResponse.forValue(cd);
    }
}
