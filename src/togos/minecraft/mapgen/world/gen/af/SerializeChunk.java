package togos.minecraft.mapgen.world.gen.af;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import togos.jobkernel.mf.Active;
import togos.jobkernel.mf.ActiveFunction;
import togos.jobkernel.uri.ActiveRef;
import togos.jobkernel.uri.ActiveRequestBuilder;
import togos.jobkernel.uri.BaseRef;
import togos.mf.api.Response;
import togos.mf.base.BaseResponse;
import togos.mf.value.URIRef;
import togos.minecraft.mapgen.TMCMGNamespace;
import togos.minecraft.mapgen.io.ChunkWriter;
import togos.minecraft.mapgen.io.RegionFile;
import togos.minecraft.mapgen.util.Util;
import togos.minecraft.mapgen.world.structure.ChunkData;

public class SerializeChunk implements ActiveFunction
{
	public static final SerializeChunk instance = new SerializeChunk();
	public static final String FUNCNAME = TMCMGNamespace.NS+"/Functions/SerializeChunk";
	public static final String ARG_CHUNK = FUNCNAME+"chunk";
	public static final String ARG_FORMAT = FUNCNAME+"format";
	public static final String FORMAT_GZIP = "gzip";
	public static final String FORMAT_DEFLATE = "deflate";
	
	public static final String formatIdToName( int formatId ) {
		switch( formatId ) {
		case( RegionFile.VERSION_GZIP ): return FORMAT_GZIP;
		case( RegionFile.VERSION_DEFLATE ): return FORMAT_DEFLATE;
		default: throw new RuntimeException("Unknown chunk serialization format number: "+formatId );
		}
	}
	
	public static final ActiveRequestBuilder buildRef( URIRef chunkRef, String formatName ) {
		return Active.build( FUNCNAME ).with(ARG_CHUNK, chunkRef).with(ARG_FORMAT, new BaseRef("data:,"+formatName) );	
	}
	
	public static final URIRef makeRef( URIRef chunkRef, String formatName ) {
		return buildRef( chunkRef, formatName ).toRef();
	}
	
	public Collection getRequiredResourceRefs( ActiveRef ref ) {
		ArrayList args = new ArrayList(2);
		args.add( ref.requireArgument(ARG_CHUNK) );
		args.add( ref.requireArgument(ARG_FORMAT) );
		return args;
	}
	
	public Response runFast( ActiveRef ref, Map resources ) {
	    return null;
	}
	
	public Response run( ActiveRef ref, Map resources ) {
		Object chunk = resources.get(ref.getArgument(ARG_CHUNK).getUri());
		String format = Util.string(resources.get(ref.getArgument(ARG_FORMAT).getUri()));
		if( chunk instanceof byte[] ) return BaseResponse.forValue(chunk);
		if( chunk instanceof ChunkData ) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				OutputStream gzos;
				if( "gzip".equals(format) ) {
					gzos = new GZIPOutputStream(baos);
				} else {
					gzos = new DeflaterOutputStream(baos);
				}
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
