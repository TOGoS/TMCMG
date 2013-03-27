package togos.noise.v1.func;

import togos.noise.Function;
import togos.noise.v1.DigestUtil;
import togos.noise.v1.cache.Cache;
import togos.noise.v1.cache.SoftCache;
import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.lang.FunctionUtil;
import togos.noise.v1.rewrite.ExpressionRewriter;

public class CacheDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	public static final SoftCache<Pair<String,DataDaDaDa>,DataDa> GLOBAL_CACHE = new SoftCache<Pair<String,DataDaDaDa>,DataDa>();
	
	/**
	 * A pair of objects, equal to another pair if the corresponding components are equal
	 */
	public final static class Pair<T0,T1> {
		public final T0 item0;
		public final T1 item1;
		public final int hashCode;
		
		public Pair( T0 item0, T1 item1 ) {
			this.item0 = item0;
			this.item1 = item1;
			this.hashCode = item0.hashCode() ^ item1.hashCode();
		}
		
		@Override
		public boolean equals( Object other ) {
			return other == this ||
				(other instanceof Pair && item0.equals(((Pair<?,?>)other).item0) && item1.equals(((Pair<?,?>)other).item1));
		}
		
		@Override
		public int hashCode() {
			return hashCode;
		}
	}
	
	protected final Cache<Pair<String,DataDaDaDa>,DataDa> cache;
	public final FunctionDaDaDa_Da wrapped;
	protected final String wrappedExpressionUrn;
	
	public CacheDaDaDa_Da( Cache<Pair<String,DataDaDaDa>,DataDa> cache, FunctionDaDaDa_Da next ) {
		this.cache = cache;
		this.wrapped = next;
		this.wrappedExpressionUrn = DigestUtil.getSha1Urn( FunctionUtil.toTnl(next) );
	}
	
	public DataDa apply( final DataDaDaDa in ) {
	    try {
	        return cache.get(new Pair<String,DataDaDaDa>( wrappedExpressionUrn, in ), new Function<Pair<String,DataDaDaDa>,DataDa>() {
	        	public DataDa apply( Pair<String,DataDaDaDa> cacheKey ) {
	        		return wrapped.apply( cacheKey.item1 );
	        	}
	        });
        } catch( Exception e ) {
        	throw new RuntimeException(e);
        }
	}
	
	public boolean isConstant() {
		return FunctionUtil.isConstant(wrapped);
	}
	
	public Object rewriteSubExpressions( ExpressionRewriter v ) {
		return new CacheDaDaDa_Da( cache, (TNLFunctionDaDaDa_Da)v.rewrite(wrapped) );
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{ wrapped };
	}
	
	public String toString() {
		return "cache("+wrapped.toString()+")";
	}
	
	public String toTnl() {
	    return FunctionUtil.toTnl(wrapped);
	}
}
