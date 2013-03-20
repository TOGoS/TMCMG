package togos.noise2.vm.dftree.func;

import togos.noise2.DigestUtil;
import togos.noise2.cache.Cache;
import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.lang.FunctionUtil;

public class CacheDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	final static class CacheKey<T0,T1> {
		public final T0 item0;
		public final T1 item1;
		public final int hashCode;
		
		public CacheKey( T0 item0, T1 item1 ) {
			this.item0 = item0;
			this.item1 = item1;
			this.hashCode = item0.hashCode() ^ item1.hashCode();
		}
		
		public boolean equals( CacheKey<T0,T1> othre ) {
			if( othre == this ) return true;
			return item0.equals(othre.item0) && item1.equals(othre.item1);
		}
		
		public int hashCode() {
			return hashCode;
		}
	}
	
	protected final Cache<CacheKey<String,DataDaDaDa>,DataDa> cache;
	public final FunctionDaDaDa_Da wrapped;
	protected final String wrappedExpressionUrn;
	
	public CacheDaDaDa_Da( Cache<CacheKey<String,DataDaDaDa>,DataDa> cache, FunctionDaDaDa_Da next ) {
		this.cache = cache;
		this.wrapped = next;
		this.wrappedExpressionUrn = DigestUtil.getSha1Urn( FunctionUtil.toTnl(next) );
	}
	
	public DataDa apply( final DataDaDaDa in ) {
	    return cache.get(new CacheKey<String,DataDaDaDa>( wrappedExpressionUrn, in ), new Function<CacheKey<String,DataDaDaDa>,DataDa>() {
	    	public DataDa apply( CacheKey<String,DataDaDaDa> cacheKey ) {
	    		return wrapped.apply( cacheKey.item1 );
	    	}
	    });
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
