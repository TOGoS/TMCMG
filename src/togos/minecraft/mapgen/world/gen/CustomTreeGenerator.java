package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.Material;
import togos.minecraft.mapgen.world.Materials;
import togos.minecraft.mapgen.world.structure.MiniChunkData;
import togos.minecraft.mapgen.world.structure.Stamp;

/**
 * This thing sucks.
 */
public class CustomTreeGenerator implements StampGenerator
{
	static class Boundser {
		public int minX = Integer.MAX_VALUE;
		public int minY = Integer.MAX_VALUE;
		public int minZ = Integer.MAX_VALUE;
		public int maxX = Integer.MIN_VALUE;
		public int maxY = Integer.MIN_VALUE;
		public int maxZ = Integer.MIN_VALUE;
		public boolean empty = true;
		
		public void plot( int x, int y, int z ) {
			empty = false;
			if( x < minX ) minX = x;
			if( x > maxX ) maxX = x;
			if( y < minY ) minY = y;
			if( y > maxY ) maxY = y;
			if( z < minZ ) minZ = z;
			if( z > maxZ ) maxZ = z;
		}
		
		public void plot( int x, int y, int z, int w, int h, int d ) {
			plot(x,y,z);
			plot(x+w,y+h,z+d);
		}
		
		public int getWidth()  { return empty ? 0 : maxX-minX; }
		public int getHeight() { return empty ? 0 : maxY-minY; }
		public int getDepth()  { return empty ? 0 : maxZ-minZ; }
		
		public Stamp toStamp() {
			return new Stamp( getWidth(), getHeight(), getDepth(), -minX, -minY, -minZ );
		}
	}
	
	static class PlotCommand {
		static final int CUBOID = 1;
		static final int SPHEROID = 2;
		public int x, y, z, w, h, d;
		public Material material;
		public int shape;
		
		public PlotCommand( Material m, int s, int x, int y, int z, int w, int h, int d ) {
			this.material = m;
			this.shape = s;
			this.x = x; this.y = y; this.z = z;
			this.w = w; this.h = h; this.d = d;
		}
	}
	
	static class Plotter {
		public List commands = new ArrayList();
		public Boundser bounds;
		public Plotter( Boundser b ) {
			this.bounds = b;
		}
		public void plot( PlotCommand cmd ) {
			bounds.plot( cmd.x, cmd.y, cmd.z, cmd.w, cmd.h, cmd.d );
			commands.add(cmd);
		}
		public void plotCenteredCuboid( Material m, int x, int y, int z, int w, int h, int d ) {
			plot( new PlotCommand( m, PlotCommand.CUBOID, x-w/2, y-h/2, z-d/2, w, h, d ) );
		}
		public void plotCenteredSpheroid( Material m, int x, int y, int z, int w, int h, int d ) {
			plot( new PlotCommand( m, PlotCommand.SPHEROID, x-w/2, y-h/2, z-d/2, w, h, d ) );
		}
		
		public void execute( MiniChunkData mcd ) {
			if( mcd.width  != bounds.getWidth()  ) throw new RuntimeException("Bounds no match X!!1");
			if( mcd.height != bounds.getHeight() ) throw new RuntimeException("Bounds no match Y!!1!");
			if( mcd.depth  != bounds.getDepth()  ) throw new RuntimeException("Bounds no match Z!!1!1");
			
			for( Iterator i=commands.iterator(); i.hasNext(); ) {
				PlotCommand pc = (PlotCommand)i.next();
				
				int startX = pc.x - bounds.minX;
				int startY = pc.y - bounds.minY;
				int startZ = pc.z - bounds.minZ;
				
				double radX = pc.w/2.0; 
				double radY = pc.h/2.0;
				double radZ = pc.d/2.0;
				
				for( int z=0; z<pc.d; ++z ) {
					for( int y=0; y<pc.h; ++y ) {
						for( int x=0; x<pc.w; ++x ) {
							boolean place;
							if( pc.shape == PlotCommand.SPHEROID ) {
								double cx = (x-radX)/radX;
								double cy = (y-radY)/radY;
								double cz = (z-radZ)/radZ;
								place = cx*cx + cy*cy + cz*cz < 1;
							} else {
								place = true;
							}
							if( place ) {
								mcd.setBlock( x+startX, y+startY, z+startZ,
									pc.material.blockType, pc.material.blockExtraBits );
							}
						}
					}
				}
			}
		}
	}
	
	protected static int rand( Random r, int min, int max ) {
		return min + r.nextInt(max+1-min);	
	}
	
	// Default to the vanilla ones:
	Material leafMaterial = Materials.byBlockType[Blocks.LEAVES];
	Material trunkMaterial = Materials.byBlockType[Blocks.LOG];
	
	int minTrunkLength = 3;
	int maxTrunkLength = 10;
	int minBallSize = 3;
	int maxBallSize = 6;
	int minBallCount = 6;
	int maxBallCount = 16;
	double ballHeightRatio = 0.75;
	double leafHorizontalDispersionRatio = 0.5;
	
	public Stamp generateStamp( int seed ) {
		Boundser b = new Boundser();
		Plotter trunkPlotter = new Plotter(b);
		Plotter leafPlotter = new Plotter(b);
		
		Random r = new Random(seed * 999999999);
		r.nextInt(); r.nextInt(); r.nextInt(); // more random!
		int trunkLength = rand(r, minTrunkLength, maxTrunkLength);
		int ballCount = rand(r, minTrunkLength, maxTrunkLength);
		
		for( int i=0; i<trunkLength; ++i ) {
			trunkPlotter.plotCenteredCuboid( trunkMaterial, 0, i, 0, 1, 1, 1 );
		}
		for( int i=0; i<ballCount; ++i ) {
			double hang = r.nextDouble()*Math.PI*2;
			double vang = r.nextDouble()*Math.PI;
			double dist = Math.random()*(trunkLength*0.5);
			double by = trunkLength*0.75 + dist * Math.cos(vang);
			double hdist = dist* Math.sin(vang)*leafHorizontalDispersionRatio;
			double bx = hdist * Math.sin(hang);
			double bz = hdist * Math.cos(hang);
			trunkPlotter.plotCenteredSpheroid( leafMaterial, (int)bx, (int)by, (int)bz, 1, 1, 1 );
			int ballSize = rand(r, minBallSize, maxBallSize);
			leafPlotter.plotCenteredSpheroid( leafMaterial, (int)bx, (int)by, (int)bz, ballSize, (int)(ballSize*ballHeightRatio), ballSize );
		}
		
		Stamp s = b.toStamp();
		leafPlotter.execute( s );
		trunkPlotter.execute( s );
		
		return s;
	}
}
