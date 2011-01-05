package togos.noise2.cache;

import togos.noise2.cache.HardCache;
import togos.noise2.function.FunctionO_O;
import junit.framework.TestCase;

public class CacheTest extends TestCase
{
	protected void testCache( Cache c ) {
		assertNull(c.get("sox"));
		assertNull(c.get("rox"));
		
		c.put("sox", "box");
		
		assertEquals("box", c.get("sox"));
		
		c.put("rox", "shox");
		
		assertEquals("box", c.get("sox"));
		assertEquals("shox", c.get("rox"));
		
		assertEquals("ghoti", c.get("fish", new FunctionO_O() {
			public Object apply( Object input ) {
				assertEquals("fish", input);
				return "ghoti";
			}
		}));

		assertEquals("box", c.get("sox"));
		assertEquals("shox", c.get("rox"));
		assertEquals("ghoti", c.get("fish"));
		assertNull(c.get("ghoti"));
	}
	
	public void testHardCache() {
		testCache(new HardCache());
	}
	
	public void testSoftCache() {
		// While it's *possible* for this to fail due to things
		// getting garbage collected immediately, that ought to
		// be an extremely rare occurence...
		testCache(new SoftCache());
	}
}
