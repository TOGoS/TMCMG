package togos.minecraft.mapgen.bukkit;

import net.minecraft.server.BiomeBase;
import net.minecraft.server.WorldChunkManager;

/**
 * Based on https://raw.github.com/Wickth/PTMBukkit/master/src/com/Khorn/PTMBukkit/Generator/BiomeManagerPTM.java
 */
public class TMCMGWorldChunkManager extends WorldChunkManager
{
	public TMCMGWorldChunkManager()
	{
	}
	
	/**
	 * @param double[] buffer - buffer which may be used to store temperature (?) data
	 * Varying output between 0-1 does not seem to have any effect on ice formation
	 */
	public double[] a(double[] buffer, int x, int z, int width, int depth)
	{
		if( (buffer == null) || (buffer.length < width * depth) ) {
			buffer = new double[width * depth];
		}
		
		for( int i=0,dx=0; dx<width; ++dx ) {
			for( int dz=0; dz<depth; ++dz, ++i ) {
				buffer[i] = (z+dz)/16d;
			}
		}
		return buffer;
	}
	
	/**
	 * Ice formation seems to be driven entirely by this, but
	 * this doesn't seem affect grass color at all:
	 */
	public BiomeBase[] a(BiomeBase[] buffer, int x, int z, int width, int depth)
	{
		if( (buffer == null) || (buffer.length < width * depth) ) {
			buffer = new BiomeBase[width * depth];
		}
		
		if( this.temperature == null || this.temperature.length < width * depth ) {
			this.temperature = new double[width*depth];
		}
		
		if( this.rain == null || this.rain.length < width * depth ) {
			this.rain = new double[width*depth];
		}
		
		// this.temperature = this.a( this.temperature, z, x, depth, width );
		for( int i=0,dx=0; dx<width; ++dx ) {
			for( int dz=0; dz<depth; ++dz, ++i ) {
				/*
				if( x < 0 ) {
					buffer[i] = BiomeBase.TUNDRA;
				} else {
					buffer[i] = BiomeBase.FOREST;
				}
				if( dx+x < 16 ) {
					if( dz+z < 8 ) {
						buffer[i] = BiomeBase.TUNDRA;
					} else {
						buffer[i] = BiomeBase.PLAINS;
					}
				} else {
					buffer[i] = BiomeBase.RAINFOREST;
				}
				*/
				
				double temp = (x+dx)/256d, humidity = (z+dz)/256d;
				if( temp < 0 ) temp = 0;
				if( temp > 1 ) temp = 1;
				if( humidity < 0 ) humidity = 0;
				if( humidity > 1 ) humidity = 1;
				this.temperature[i] = temp;
				this.rain[i] = humidity;
				buffer[i] = BiomeBase.a(temp, humidity);
			}
		}
		
		return buffer;
	}
	
	/**
	 * Called from org.bukkit.craftbukkit.CraftWorld.getHumidity
	 */
	public double getHumidity( int x, int z ) {
		// double h = (z-16)/16d;
		return z*16;
		//return h < 0 ? 0 : h > 1 ? 1 : h;
	}
}
