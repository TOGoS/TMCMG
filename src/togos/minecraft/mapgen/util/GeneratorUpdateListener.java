package togos.minecraft.mapgen.util;

import togos.minecraft.mapgen.world.gen.MinecraftWorldGenerator;

public interface GeneratorUpdateListener
{
	public void generatorUpdated( MinecraftWorldGenerator mwg );
}
