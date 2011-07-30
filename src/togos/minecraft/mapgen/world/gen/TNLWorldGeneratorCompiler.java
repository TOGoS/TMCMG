package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Material;
import togos.minecraft.mapgen.world.Materials;
import togos.noise2.vm.dftree.lang.TNLCompiler;
import togos.noise2.vm.dftree.lang.macro.ConstantMacroType;
import togos.noise2.vm.dftree.lang.macro.LanguageMacros;
import togos.noise2.vm.dftree.lang.macro.NoiseMacros;

public class TNLWorldGeneratorCompiler extends TNLCompiler
{
	protected void initBuiltins() {
		macroTypes.putAll( LanguageMacros.stdLanguageMacros );
		macroTypes.putAll( NoiseMacros.stdNoiseMacros );
		macroTypes.putAll( WorldGeneratorMacros.wgMacros );
		
		macroTypes.put("materials.none", new ConstantMacroType(new Integer(-1)));
		for( int i=0; i<128; ++i ) {
			Material m = Materials.getByBlockType(i);
			if( m != null ) {
				String nn = "materials." + m.name.replace(' ', '-').toLowerCase();
				macroTypes.put(nn, new ConstantMacroType(new Integer(i)));
			}
		}
	}
}
