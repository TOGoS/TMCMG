package togos.minecraft.mapgen;

import java.util.HashMap;

import org.jnbt.Tag;

/**
 * A HashMap that's slightly easier to add Tags to.
 */
public class TagMap extends HashMap
{
	private static final long serialVersionUID = 1L;
	
	public void add( Tag t ) {
		put( t.getName(), t );
	}
}
