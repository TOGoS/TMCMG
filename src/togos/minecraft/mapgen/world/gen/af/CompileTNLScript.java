package togos.minecraft.mapgen.world.gen.af;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import togos.jobkernel.mf.Active;
import togos.jobkernel.mf.ActiveFunction;
import togos.jobkernel.uri.ActiveRef;
import togos.mf.api.Response;
import togos.mf.base.BaseResponse;
import togos.mf.value.URIRef;
import togos.minecraft.mapgen.TMCMGNamespace;
import togos.minecraft.mapgen.util.ByteUtil;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.noise2.lang.ScriptError;

public class CompileTNLScript implements ActiveFunction
{
	public static final CompileTNLScript instance = new CompileTNLScript();
	public static final String FUNCNAME = TMCMGNamespace.NS+"/Functions/CompileTNLScript";
	public static final String SCRIPT_ARGNAME = FUNCNAME+"script";
	
	public static final ActiveRef makeRef( URIRef scriptRef ) {
		ArrayList args = new ArrayList(2);
		args.add( new Active.Arg(SCRIPT_ARGNAME, scriptRef));
		return Active.mkActiveRef(FUNCNAME,args);
	}
	
	public Collection getRequiredResourceRefs( ActiveRef ref ) {
		ArrayList args = new ArrayList(2);
		args.add( ref.requireArgument(SCRIPT_ARGNAME) );
		return args;
	}
	
	public Response runFast( ActiveRef ref, Map resources ) {
	    return null;
	}
	
	public Response run( ActiveRef ref, Map resources ) {
		String scriptUri = ref.requireArgument(SCRIPT_ARGNAME).getUri();
		Object script = resources.get(scriptUri);
		if( script instanceof WorldGenerator ) {
			return BaseResponse.forValue(script);
		} else {
			String source = ByteUtil.string(script);
			TNLWorldGeneratorCompiler compiler = new TNLWorldGeneratorCompiler();
			Object compiled;
            try {
            	// System.err.println("Compiling {"+source+"}");
	            compiled = compiler.compile(source, scriptUri);
            } catch( ScriptError e ) {
            	throw new RuntimeException(e);
            }
			return BaseResponse.forValue(compiled);
		}
	}
}
