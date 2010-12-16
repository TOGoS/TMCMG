package togos.minecraft.mapgen;

import junit.framework.TestCase;

public class PathUtilTest extends TestCase
{
	public void testTreePath() {
		assertEquals("swne/nene", PathUtil.qtChunkDir(0, 0, 4, 0));
		assertEquals("swne", PathUtil.qtChunkDir(0, 0, 4, 2));
		assertEquals("swne/nene/nene/nene/nene/nene/nene/nene/nene/nene/nene",
				PathUtil.qtChunkDir(0, 0));
		
		assertEquals("swne/nesw", PathUtil.qtChunkDir( 1,  1, 4, 0));
		assertEquals("swne/senw", PathUtil.qtChunkDir( 2,  1, 4, 0));
		assertEquals("nwse/senw", PathUtil.qtChunkDir(-2,  1, 4, 0));
		assertEquals("sene/nwsw", PathUtil.qtChunkDir( 1, -5, 4, 0));
		assertEquals("nese/swnw", PathUtil.qtChunkDir(-2, -5, 4, 0));
		assertEquals("nese",      PathUtil.qtChunkDir(-2, -5, 4, 2));
		assertEquals("nesw/swse", PathUtil.qtChunkDir(-2, -5, 6, 2));
	}
}
