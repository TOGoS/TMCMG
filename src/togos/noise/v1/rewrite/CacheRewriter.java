package togos.noise.v1.rewrite;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import togos.noise.v1.cache.Cache;
import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.func.CacheDaDaDa_Da;
import togos.noise.v1.func.TNLFunctionDaDaDa_Da;
import togos.noise.v1.func.CacheDaDaDa_Da.Pair;
import togos.noise.v1.lang.Expression;

/**
 * Wraps common expressions in cache() thingers
 */
public class CacheRewriter implements ExpressionRewriter
{
	Cache<Pair<String,DataDaDaDa>,DataDa> cache;
	HashMap<String,Integer> uteCounts = new HashMap<String,Integer>();
	
	public CacheRewriter( Cache<Pair<String,DataDaDaDa>,DataDa> cache ) {
		this.cache = cache;
	}
	
	protected void incr( String tnl ) {
		Integer c = uteCounts.get(tnl);
		if( c == null ) {
			c = new Integer(1);
		} else {
			c = new Integer(c.intValue()+1);
		}
		uteCounts.put(tnl, c);
	}
	
	public void initCounts( Expression expr ) {
		incr( expr.toTnl() );
		Object[] se = expr.directSubExpressions();
		for( int i=0; i<se.length; ++i ) {
			initCounts(se[i]);
		}
	}
	
	public void initCounts( Object expr ) {
		if( expr instanceof Expression ) {
			initCounts( (Expression)expr );
		}
	}
	
	public void dumpCounts( PrintStream out ) {
		for( Map.Entry<String,Integer> me : uteCounts.entrySet() ) {
			System.err.println( me.getValue() + " times - " + me.getKey().toString() );
		}
	}
	
	public Object rewrite( Object f ) {
		if( f instanceof TNLFunctionDaDaDa_Da ) {
			TNLFunctionDaDaDa_Da e = (TNLFunctionDaDaDa_Da)f;
			
			if( e.getTriviality() > 0 ) {
				// Don't bother caching easy things!
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
		} else if( f instanceof Expression ) {
			return ((Expression)f).rewriteSubExpressions(this);
		} else {
			return f;
		}
	}
}
