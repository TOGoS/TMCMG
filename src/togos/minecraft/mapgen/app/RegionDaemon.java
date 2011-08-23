package togos.minecraft.mapgen.app;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.io.RegionWriter;
import togos.minecraft.mapgen.job.JobService;
import togos.minecraft.mapgen.util.ByteBlob;
import togos.minecraft.mapgen.util.ByteChunk;
import togos.minecraft.mapgen.util.Script;
import togos.minecraft.mapgen.util.Util;
import togos.minecraft.mapgen.world.gen.ChunkGenerator;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;

public class RegionDaemon
{
	static class Region {
		public int rx, rz;
		
		public Region( int rx, int rz ) {
			this.rx = rx;
			this.rz = rz;
		}
		
		public boolean equals( Object o ) {
			if( !(o instanceof Region) ) return false;
			
			Region or = (Region)o;
			return rx == or.rx && rz == or.rz; 
		}
		
		public int hashCode() {
			return rx | (rz << 16);
		}
		
		public String toString() {
			return "region "+rx+","+rz;
		}
	}
	
	static class RegionGenerationJob implements Runnable {
		static RegionWriter regionWriter = new RegionWriter();
		public final ChunkGenerator chunkGenerator;
		public final File regionFile;
		public final Region region;
		public final long timestamp;
		
		protected boolean completed;
		
		public RegionGenerationJob( ChunkGenerator chunkGenerator, File regionFile, Region r, long timestamp ) {
			this.chunkGenerator = chunkGenerator;
			this.regionFile = regionFile;
			this.region = r;
			this.timestamp = timestamp;
		}
		
		public synchronized void waitForCompletion() throws InterruptedException {
			while( !completed ) wait();
		}
		
		public void run() {
			System.err.println("Generating "+region);
			try {
				ByteBlob b = regionWriter.generateRegion(chunkGenerator, region.rx, region.rz, (int)timestamp);
				FileOutputStream fos = new FileOutputStream( regionFile );
				for( Iterator i=b.chunkIterator(); i.hasNext(); ) {
					ByteChunk c = (ByteChunk)i.next();
					fos.write( c.getBuffer(), c.getOffset(), c.getSize() );
				}
				fos.close();
			} catch( IOException e ) {
				System.err.println("Error while writing region file "+regionFile);
				e.printStackTrace();
			} finally {
				synchronized( this ) {
					completed = true;
					notifyAll();
				}
				System.err.println("Finished "+region);
			}
		}
	}
	
	public File regionDir;
	public ChunkGenerator chunkGenerator;
	public Script generatorScript;
	public long interval = 10*1000;
	
	public RegionDaemon( File regionDir, Script s ) {
		this.regionDir = regionDir;
		this.generatorScript = s;
		this.chunkGenerator = new ChunkGenerator( ((WorldGenerator)s.program).getChunkMunger() );
	}
	
	JobService js = new JobService(new LinkedBlockingQueue(), 1);
	HashMap regionGenerationTimes = new HashMap();
	HashSet existingRegions = new HashSet();
	
	protected static final Pattern RFPAT = Pattern.compile("r\\.(-?\\d+)\\.(-?\\d+)\\.mcr$");
	
	protected File regionFile( Region r ) {
		return new File(regionDir + "/r."+r.rx+"."+r.rz+".mcr");
	}
	
	/**
	 * @return set of regions that have been touched by player
	 */
	protected Set regionsTouched() {
		File[] filez = regionDir.listFiles();
		HashSet touched = new HashSet();
		for( int i=0; i<filez.length; ++i ) {
			File f = filez[i];
			Matcher m = RFPAT.matcher(f.getName());
			if( m.matches() ) {
				Region r = new Region(
					Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2))
				);
				Long generated = (Long)regionGenerationTimes.get(r);
				if( generated == null || f.lastModified() > generated.longValue() ) {
					touched.add(r);
				}
			}
		}
		return touched;
	}
	
	protected Set regionsToGenerate() {
		HashSet toGenerate = new HashSet();
		for( Iterator i=regionsTouched().iterator(); i.hasNext(); ) {
			Region r = (Region)i.next();
			for( int dz=-2; dz<=+2; ++dz ) {
				for( int dx=-2; dx<=+2; ++dx ) {
					Region tgr = new Region(r.rx+dx, r.rz+dz);
					if( !existingRegions.contains(tgr) ) {
						toGenerate.add(tgr);
					}
				}
			}
		}
		return toGenerate;
	}
	
	protected void loadExistingRegions() {
		File[] filez = regionDir.listFiles();
		for( int i=0; i<filez.length; ++i ) {
			File f = filez[i];
			Matcher m = RFPAT.matcher(f.getName());
			if( m.matches() ) {
				existingRegions.add( new Region(
					Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2))
				) );
			}
		}
	}
	
	public void run() {
		js.start();
		
		loadExistingRegions();
		try {
			while( true ) {
				Set rtg = regionsToGenerate();
				if( rtg.size() > 0 ) {
					System.err.println(rtg.size()+" regions to generate!");
					List jobs = new ArrayList(rtg.size());
					for( Iterator i=rtg.iterator(); i.hasNext(); ) {
						Region r = (Region)i.next();
						System.err.println("Enqueuing "+r);
						RegionGenerationJob j = new RegionGenerationJob(chunkGenerator, regionFile(r), r, System.currentTimeMillis()/1000); 
						jobs.add(j);
						js.jobQueue.add(j);
					}
					for( Iterator i=jobs.iterator(); i.hasNext(); ) {
						RegionGenerationJob j = (RegionGenerationJob)i.next();
						System.err.println("Waiting for "+j.regionFile+"...");
						j.waitForCompletion();
						existingRegions.add(j.region);
					}
				}
				Thread.sleep(interval);
			}
		} catch( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		} finally {
			js.halt();
		}
	}
	
	public static void main( String[] args ) {
		try {
			Script script = Util.readScript(new File("terrain.tnl"));
			TNLWorldGeneratorCompiler c = new TNLWorldGeneratorCompiler();
			script.program = ScriptUtil.compile(c, script);
			RegionDaemon rd = new RegionDaemon( new File("region"), script );
			rd.run();
		} catch( Exception e ) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
