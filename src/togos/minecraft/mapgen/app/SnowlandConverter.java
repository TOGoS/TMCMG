package togos.minecraft.mapgen.app;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.IntTag;
import org.jnbt.DoubleTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;

import togos.minecraft.mapgen.io.RegionFile;
import togos.minecraft.mapgen.io.RegionFileCache;

public class SnowlandConverter
{
	int chunkCount = 0;
	int minX = 0, minZ = 0, maxX = 0, maxZ = 0;
	
	void moveChunk( CompoundTag level, int cdx, int cdz ) {
		int tdx = 16*cdx, tdz = 16*cdz;
		
        IntTag xPosTag = (IntTag)level.getValue().get("xPos");
        IntTag zPosTag = (IntTag)level.getValue().get("zPos");
		
        level.getValue().put("xPos", new IntTag("xPos",xPosTag.getIntValue()+cdx));
        level.getValue().put("zPos", new IntTag("zPos",zPosTag.getIntValue()+cdz));
        
        List tileEntities = ((ListTag)level.getValue().get("TileEntities")).getValue();
        for( int i=tileEntities.size()-1; i>=0; --i ) {
        	CompoundTag tileEntity = (CompoundTag)tileEntities.get(i);
        	int tileX = ((IntTag)tileEntity.getValue().get("x")).getIntValue();
        	int tileZ = ((IntTag)tileEntity.getValue().get("z")).getIntValue();
        	
        	tileEntity.getValue().put("x", new IntTag("x", tileX+tdx) );
        	tileEntity.getValue().put("z", new IntTag("z", tileZ+tdz) );
        }

        List entities = ((ListTag)level.getValue().get("Entities")).getValue();
        for( int i=entities.size()-1; i>=0; --i ) {
        	CompoundTag entity = (CompoundTag)entities.get(i);
        	List pos = ((ListTag)entity.getValue().get("Pos")).getValue();
        	pos.set( 0, new DoubleTag(null, ((DoubleTag)pos.get(0)).getDoubleValue()+tdx ) );
        	pos.set( 2, new DoubleTag(null, ((DoubleTag)pos.get(2)).getDoubleValue()+tdx ) );
        }
	}
	
	static int tmod( int a, int b ) {
		return a > 0 ? a % b : (b + (a % b))%b;
	}
	
	static void modtest( int a, int b ) {
		System.err.println( a + " % " + b + " = " + tmod(a,b) );
	}
	
	void processChunk( File chunkFile ) {
		// System.err.println("Processing chunk "+chunkFile);
		try {
			NBTInputStream nbtInputStream = null;
			CompoundTag t;
			try {
		        nbtInputStream = NBTInputStream.gzipOpen(new FileInputStream(chunkFile));
		        t = (CompoundTag)nbtInputStream.readTag();
			} finally {
				if( nbtInputStream != null ) nbtInputStream.close();
			}
	        CompoundTag level = (CompoundTag)t.getValue().get("Level");
	        
	        moveChunk( level, 32, -64 );
	        
	        IntTag xPosTag = (IntTag)level.getValue().get("xPos");
	        IntTag zPosTag = (IntTag)level.getValue().get("zPos");
	        int cx = xPosTag.getIntValue();
	        int cz = zPosTag.getIntValue();
	        
	        if( chunkCount == 0 ) {
		        minX = cx;
		        maxX = cx+1;
		        minZ = cz;
		        maxZ = cz+1;
	        } else {
	        	if( cx < minX ) minX = cx;
	        	if( cx > maxX ) maxX = cx+1;
	        	if( cz < minZ ) minZ = cz;
	        	if( cz > maxZ ) maxZ = cz+1;
	        }
	        ++chunkCount;
	        
	        /*
	        if( ((ListTag)level.getComponents().get("TileEntities")).getChildren().size() > 0 &&
	            ((ListTag)level.getComponents().get("Entities")).getChildren().size() > 0 ) {
	        	System.err.println(level.toString());
	        	System.exit(0);
	        }
	        */
	        
	        // CompoundTag tileEntities
	        
	        RegionFile f = RegionFileCache.getRegionFile( new File("/home/stevens/backup/git/games/minecraft/saves/omntland2"), cx, cz);
	        NBTOutputStream nos = null;
	        try {
	        	nos = NBTOutputStream.rawOpen( f.getChunkDataOutputStream( tmod(cx,32), tmod(cz,32) ) );
	        	nos.writeTag( t );
	        } finally { 
	        	if( nos != null ) nos.close();
	        }
	        
        } catch( IOException e ) {
        	throw new RuntimeException(e);
        }
	}
	
	void walk( File dir ) {
		if( dir.isDirectory() ) {
			File[] files = dir.listFiles();
			for( int i=0; i<files.length; ++i ) {
				if( files[i].getName().startsWith(".") ) continue;
				walk( files[i] );
			}
		} else if( dir.getName().startsWith("c.") ) {
			processChunk(dir);
		}
	}
	
	File rootInputDir = new File("/home/stevens/backup/git/games/minecraft/saves/Snowland");
	
	public void run() {
		walk( rootInputDir );
		System.err.println("Bounds: "+minX+","+minZ+" to "+maxX+","+maxZ);
	}
	
	public static void main( String[] args ) {
		/*
		modtest( 6, 5 );
		modtest( 1, 5 );
		modtest( 0, 5 );
		modtest( 5, 5 );
		modtest( -1, 5 );
		modtest( -4, 5 );
		modtest( -5, 5 );
		modtest( -6, 5 );
		*/
		new SnowlandConverter().run();
	}
}
