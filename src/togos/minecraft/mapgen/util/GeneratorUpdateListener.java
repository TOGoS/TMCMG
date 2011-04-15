package togos.minecraft.mapgen.util;

import togos.mf.value.URIRef;
import togos.minecraft.mapgen.world.gen.WorldGenerator;

public interface GeneratorUpdateListener
{
	public void generatorUpdated( URIRef wgScriptRef, WorldGenerator wg );
}