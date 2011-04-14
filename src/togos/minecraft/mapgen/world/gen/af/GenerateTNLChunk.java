package togos.minecraft.mapgen.world.gen.af;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import togos.jobkernel.mf.ActiveFunction;
import togos.jobkernel.uri.ActiveRef;
import togos.jobkernel.uri.Ref;
import togos.jobkernel.uri.URIUtil;
import togos.mf.api.Response;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.TMCMGNamespace;
import togos.minecraft.mapgen.util.ByteUtil;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class GenerateTNLChunk implements ActiveFunction
{
	public static final GenerateTNLChunk instance = new GenerateTNLChunk();
	public static final String FUNCNAME = TMCMGNamespace.NS+"/Functions/GenerateTNLChunk";
	public static final String SCRIPT_ARGNAME = FUNCNAME+"script";
	public static final String COORDS_ARGNAME = FUNCNAME+"chunkCoords";
	
	public static final ActiveRef makeRef( Ref scriptRef, Ref coordsRef ) {
		ArrayList args = new ArrayList(2);
		args.add( new ActiveRef.Arg(SCRIPT_ARGNAME, scriptRef));
		args.add( new ActiveRef.Arg(COORDS_ARGNAME, coordsRef));
		return ActiveRef.create(FUNCNAME,args);
	}
	
	public static final Ref makeCoordsRef( long x, long y, long z, int w, int h, int d ) {
		return new Ref(URIUtil.makeDataUri(x+","+y+","+z+","+w+","+h+","+d));
	}
	
	public static final ActiveRef makeRef( Ref scriptRef, long x, long y, long z, int w, int h, int d ) {
		return makeRef( scriptRef, makeCoordsRef(x,y,z,w,h,d) );
	}
	
	protected static final Ref compiledScriptRef( ActiveRef ref ) {
		return CompileTNLScript.makeRef(ref.requireArgument(SCRIPT_ARGNAME));
	}
	
	public Collection getRequiredResourceRefs( ActiveRef ref ) {
		ArrayList args = new ArrayList(2);
		args.add( compiledScriptRef(ref) );
		args.add( ref.requireArgument(COORDS_ARGNAME) );
		return args;
	}

	public Response run( ActiveRef ref, Map resources ) {
		Object script = resources.get( compiledScriptRef(ref).getUri() );
		String coords = ByteUtil.string( resources.get(ref.requireArgument(COORDS_ARGNAME).getUri()) );
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
