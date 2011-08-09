package togos.minecraft.mapgen.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import togos.mf.api.Response;
import togos.mf.api.ResponseCodes;
import togos.mf.base.BaseResponse;
import togos.minecraft.mapgen.mf.ActiveFunction;
import togos.minecraft.mapgen.uri.ActiveRef;

/**
 * Handy for testing the ActiveFunction system
 */
public class ReverseBytes implements ActiveFunction {
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
