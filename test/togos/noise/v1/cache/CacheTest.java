package togos.noise.v1.cache;

import junit.framework.TestCase;
import togos.noise.Function;
import togos.noise.v1.cache.Cache;
import togos.noise.v1.cache.HardCache;
import togos.noise.v1.cache.SoftCache;
import togos.noise.v1.cache.WeakCache;

public class CacheTest extends TestCase
{
	protected void testCache( Cache<String,String> c ) throws Exception {
		assertNull(c.get("sox"));
		assertNull(c.get("rox"));
		
		c.put("sox", "box");
		
		assertEquals("box", c.get("sox"));
		
		c.put("rox", "shox");
		
		assertEquals("box", c.get("sox"));
		assertEquals("shox", c.get("rox"));
		
		assertEquals("ghoti", c.get("fish", new Function<String,String>() {
			public String apply( String input ) {
				assertEquals("fish", input);
				return "ghoti";
			}
		}));
		
		assertEquals("box", c.get("sox"));
		assertEquals("shox", c.get("rox"));
		assertEquals("ghoti", c.get("fish"));
		assertNull(c.get("ghoti"));
	}
	
	public void testHardCache() throws Exception {
		testCache(new HardCache<String,String>());
	}
	
	public void testSoftCache() throws Exception {
		testCache(new SoftCache<String,String>());
	}
	
	public void testWeakCache() {
		// The purpose of this test is actually to test that
		// SoftCache entries will eventually get collected,
		// since it's more likely to have actually happened
		// after System.gc() with WeakReferences.
		
		WeakCache<String,String> c = new WeakCache<String,String>();
		
		c.put("heck", new String("neck"));
		assertEquals("neck", c.get("heck"));
		
		System.gc();
		
		assertNull(c.get("heck"));
	}
}
