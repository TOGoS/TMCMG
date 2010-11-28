package togos.minecraft.mapgen;

import java.util.HashMap;

import togos.minecraft.mapgen.world.gen.LayerMapper;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.lang.ASTNode;
import togos.noise2.lang.macro.MacroType;

public class MGMacros
{
	static HashMap wgMacros = new HashMap();
	static {
		wgMacros.put("layer", new MacroType() {
			public Object instantiate( TNLCompiler c, ASTNode sn ) {
				if( sn.arguments.size() != 3 ) {
					throw new CompileError( sn.macroName + " requires 3 arguments for type, floor, ceiling; given "+sn.arguments.size(), sn );
				}
				return new LayerMapper.Layer(
					FunctionUtil.toDaDa_Ia(sn.arguments.get(0), sn),
					FunctionUtil.toDaDa_Da(sn.arguments.get(1), sn),
					FunctionUtil.toDaDa_Da(sn.arguments.get(2), sn)
				);
			}
		});
	}
}
