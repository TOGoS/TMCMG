package togos.minecraft.mapgen.world.gen.af;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;

import togos.jobkernel.mf.Active;
import togos.jobkernel.mf.ActiveFunction;
import togos.jobkernel.uri.ActiveRef;
import togos.jobkernel.uri.ActiveRequestBuilder;
import togos.mf.api.Response;
import togos.mf.base.BaseResponse;
import togos.mf.value.URIRef;
import togos.minecraft.mapgen.TMCMGNamespace;
import togos.minecraft.mapgen.io.ChunkWriter;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class SerializeChunk implements ActiveFunction
{
	public static final SerializeChunk instance = new SerializeChunk();
	public static final String FUNCNAME = TMCMGNamespace.NS+"/Functions/SerializeChunk";
	public static final String CHUNK_ARGNAME = FUNCNAME+"chunk";
	
	public static final ActiveRequestBuilder buildRef( URIRef chunkRef ) {
		return Active.build( FUNCNAME ).with(CHUNK_ARGNAME, chunkRef);	
	}
	
	public static final URIRef makeRef( URIRef chunkRef ) {
		return buildRef( chunkRef ).toRef();
	}
	
	public Collection getRequiredResourceRefs( ActiveRef ref ) {
		ArrayList args = new ArrayList(2);
		args.add( ref.requireArgument(CHUNK_ARGNAME) );
		return args;
	}
	
	public Response runFast( ActiveRef ref, Map resources ) {
	    return null;
	}
	
	public Response run( ActiveRef ref, Map resources ) {
		Object chunk = resources.get(ref.getArgument(CHUNK_ARGNAME).getUri());
		if( chunk instanceof byte[] ) return BaseResponse.forValue(chunk);
		if( chunk instanceof ChunkData ) {
			try {
				
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//GZIPOutputStream gzos = new GZIPOutputStream(baos);
				DeflaterOutputStream gzos = new DeflaterOutputStream(baos);
				DataOutputStream dos = new DataOutputStream(gzos);
			
				ChunkWriter.writeChunk( (ChunkData)chunk, dos );
				dos.close(); gzos.close();
				
				return BaseResponse.forValue(baos.toByteArray());
			} catch( IOException e ) {
				throw new RuntimeException(e);
			}
		}
		throw new RuntimeException("Don't know how to serialize "+chunk.getClass());
	}
}
