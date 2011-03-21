package togos.noise2.rewrite;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import togos.noise2.cache.Cache;
import togos.noise2.function.CacheDaDaDa_Da;
import togos.noise2.function.TNLFunctionDaDaDa_Da;
import togos.noise2.lang.Expression;

/**
 * Wraps common expressions in cache() thingers
 */
public class CacheRewriter implements ExpressionRewriter
{
	Cache cache;
	HashMap uteCounts = new HashMap();
	
	public CacheRewriter( Cache cache ) {
		this.cache = cache;
	}
	
	protected void incr( String tnl ) {
		Integer c = (Integer)uteCounts.get(tnl);
		if( c == null ) {
			c = new Integer(1);
		} else {
			c = new Integer(c.intValue()+1);
		}
		uteCounts.put(tnl, c);
	}
	
	public void initCounts( Expression expr ) {
		incr( expr.toTnl() );
		Expression[] se = expr.directSubExpressions();
		for( int i=0; i<se.length; ++i ) {
			initCounts(se[i]);
		}
	}
	
	public void dumpCounts( PrintStream out ) {
		for( Iterator i = uteCounts.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry me = (Map.Entry)i.next();
			System.err.println( me.getValue() + " times - " + me.getKey().toString() );
		}
	}
	
	public Object rewrite( Object f ) {
		if( f instanceof TNLFunctionDaDaDa_Da ) {
			TNLFunctionDaDaDa_Da e = (TNLFunctionDaDaDa_Da)f;
			
			if( e.isConstant() ) {
				// Don't bother caching constants!
				return e;
			}
			
			String tnl = e.toTnl(); // Do this BEFORE rewriting sub-exprs...
			e = (TNLFunctionDaDaDa_Da)e.rewriteSubExpressions(this);
			Integer count = (Integer)this.uteCounts.get(tnl);
			if( count != null && count.intValue() >= 2 ) {
				return new CacheDaDaDa_Da(cache, e);
			} else {
				return e;
			}
		} else {
			return f;
		}
	}
}
