package togos.minecraft.mapgen.ui;

import togos.minecraft.mapgen.world.gen.MinecraftWorldGenerator;

public interface WorldExplorerView
{
	public double getWorldX();
	public double getWorldY();
	public double getZoom();
	public double getWorldXStepSize();
	public double getWorldYStepSize();
	
	public void setWorldGenerator( MinecraftWorldGenerator wg );
	public void setWorldPos( double wx, double wy, double zoom );
}
