package togos.minecraft.mapgen.world.gen;

import java.util.HashMap;
import java.util.Iterator;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.TNLCompiler;
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
				return new LayerTerrainGenerator.Layer(
					FunctionUtil.toDaDa_Ia(sn.arguments.get(0), sn),
					FunctionUtil.toDaDa_Da(sn.arguments.get(1), sn),
					FunctionUtil.toDaDa_Da(sn.arguments.get(2), sn)
				);
			}
		});
		wgMacros.put("layered-terrain", new MacroType() {
			public Object instantiate( TNLCompiler c, ASTNode sn ) {
				LayerTerrainGenerator lm = new LayerTerrainGenerator();
				for( Iterator i=sn.arguments.iterator(); i.hasNext(); ) {
					ASTNode argNode = (ASTNode)i.next();
					Object node = c.compile(argNode);
					if( node instanceof LayerTerrainGenerator.Layer ) {
						lm.layers.add( node );
					} else {
						throw new CompileError( "Don't know how to incorporate "+node.getClass()+" into world generator", argNode );
					}
				}
				return lm;
			}
		});
	}
}
