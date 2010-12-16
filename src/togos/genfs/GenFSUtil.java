package togos.genfs;

import java.io.File;


public class GenFSUtil
{
	public static int DEFAULT_DIRECTORY_PERM_BITS = 0755;
	public static int DEFAULT_FILE_PERM_BITS = 0644;
	public static int DIRECTORY_MODE = 0040000;
	public static int FILE_MODE      = 0100000;
	
	public static int getMode( File f ) {
		if( f.isDirectory() ) {
			return DIRECTORY_MODE | DEFAULT_DIRECTORY_PERM_BITS;
		} else {
			return FILE_MODE | DEFAULT_FILE_PERM_BITS;
		}
	}
	
	public static Stat getStat( File f ) {
		return new Stat( f.length(), getMode(f) );
	}
	
	public static DirectoryEntry getDirEntry( File f ) {
		return new DirectoryEntry( f.getName(), f.length(), getMode(f) );
	}
}
