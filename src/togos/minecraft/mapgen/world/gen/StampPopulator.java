package togos.minecraft.mapgen.world.gen;

import java.util.Collection;

import togos.minecraft.mapgen.world.structure.Stamp;

public interface StampPopulator
{
	public static class StampInstance {
		/** World coordinates (i.e. those used by entities) of the stamp's origin */
		long wx, wy, wz;
		Stamp stamp;
		
		public StampInstance( Stamp stamp, long wx, long wy, long wz ) {
			this.stamp = stamp;
			this.wx = wx;
			this.wy = wy;
			this.wz = wz;
		}
	}
	
	public Collection getStampInstances( int cx, int cz );
}
