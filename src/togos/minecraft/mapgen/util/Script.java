package togos.minecraft.mapgen.util;

/**
 * For tracking script source, filename, and compiled value
 */
public class Script
{
	public final byte[] source;
	public final String sourceFilename;
	public Object program;
	
	public Script( byte[] source, String sourceFilename ) {
		this.source = source;
		this.sourceFilename = sourceFilename;
	}
}
