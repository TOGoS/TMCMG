package togos.minecraft.mapgen.util;

import togos.minecraft.mapgen.world.gen.WorldGenerator;

public interface GeneratorUpdateListener
{
	public void generatorUpdated( WorldGenerator wg );
}