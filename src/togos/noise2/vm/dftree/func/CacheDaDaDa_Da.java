package togos.noise2.vm.dftree.func;

import togos.noise2.DigestUtil;
import togos.noise2.cache.Cache;
import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.uri.URIUtil;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.lang.FunctionUtil;

public class CacheDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	final static class CacheKey {
		public final String funcUrn;
		public final String dataUrn;
		public final int hashCode;
		
		public CacheKey( String funcUrn, String dataUrn ) {
			this.funcUrn = funcUrn;
			this.dataUrn = dataUrn;
			this.hashCode = funcUrn.hashCode() ^ dataUrn.hashCode();
		}
		
		public boolean equals( Object othre ) {
			if( othre instanceof CacheKey ) {
				CacheKey ok = (CacheKey)othre;
				return funcUrn.equals(ok.funcUrn) && dataUrn.equals(ok.dataUrn);
			}
			return false;
		}
		
		public int hashCode() {
			return hashCode;
		}
		
		public String toString() {
			return "active:apply" +
				"+operator@"+URIUtil.uriEncode(funcUrn)+
				"+operand@"+URIUtil.uriEncode(dataUrn);
		}
	}
	
	protected final Cache cache;
	public final FunctionDaDaDa_Da wrapped;
	protected final String wrappedExpressionUrn;
	
	public CacheDaDaDa_Da( Cache cache, FunctionDaDaDa_Da next ) {
		this.cache = cache;
		this.wrapped = next;
		this.wrappedExpressionUrn = DigestUtil.getSha1Urn( FunctionUtil.toTnl(next) );
	}
	
	public DataDa apply( final DataDaDaDa in ) {
	    return (DataDa)cache.get(new CacheKey( wrappedExpressionUrn, in.getDataId() ), new FunctionO_O() {
	    	public Object apply( Object cacheKey ) {
	    		return wrapped.apply(in);
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
