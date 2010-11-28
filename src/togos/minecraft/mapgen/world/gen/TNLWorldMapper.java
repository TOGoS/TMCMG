package togos.minecraft.mapgen.world.gen;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.lang.macro.NoiseMacros;

public class TNLWorldMapper
{
	public WorldGenerator compile( ASTNode n ) {
		TNLCompiler comp = new TNLCompiler();
		comp.macroTypes.putAll( NoiseMacros.standardBuiltinMacros );
		comp.macroTypes.putAll( MGMacros.wgMacros );
		return null;
	}
}
