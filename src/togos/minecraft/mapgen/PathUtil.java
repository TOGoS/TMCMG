package togos.minecraft.mapgen;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PathUtil
{
	protected static int TREEPATH_COMPONENTS = 24;
	protected static int TREEPATH_TAIL_COMPONENTS = 2;
	
	protected static String qtChunkDir( int x, int y, int components, int tailcomponents ) {
		int cx = 0;
		int cy = 0;
		String p = "";
		for( int i=components-1; i>=tailcomponents; --i ) {
			int div = 1<<(i-1); // No difference once i<0, so no i<<-1 worry
			if( x < cx ) {
				cx -= div;
				p += "n";
			} else {
				cx += div;
				p += "s";
			}
			if( y < cy ) {
				cy -= div;
				p += "e";
			} else {
				cy += div;
				p += "w";
			}
			if( i > tailcomponents && (i&1) == 0 ) p += "/";
		}
		return p;
	}
	
	public static String qtChunkDir( int x, int y ) {
		return qtChunkDir( x, y, TREEPATH_COMPONENTS, TREEPATH_TAIL_COMPONENTS );
	}
	
	protected static int tmod( int num, int modby ) {
		if( num < 0 ) {
			num = -num;
			num %= modby;
			num = modby - num;
			return num;
		} else {
			return num % modby;
		}
	}
	
	public static String mcChunkDir( int x, int z ) {
		return
			Integer.toString( tmod(x,64), 36 ) + "/" +
			Integer.toString( tmod(z,64), 36 );
	}
	
	public static String chunkBaseName( int x, int z ) {
		return "c." + Integer.toString(x,36) + "." + Integer.toString(z,36) + ".dat";
	}
	
	static Pattern CHUNKPAT = Pattern.compile("^(.*/)?c\\.(\\-?[0-9a-z]+)\\.(\\-?[0-9a-z]+)\\.dat");
	
	public static int[] chunkCoords( String resName ) {
		Matcher cpm = CHUNKPAT.matcher(resName);
		if( !cpm.matches() ) return null;
		
		return new int[] {
			Integer.parseInt(cpm.group(2), 36),
			Integer.parseInt(cpm.group(3), 36),
		};
	}
}
