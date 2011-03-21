package togos.noise2.function;

import togos.minecraft.mapgen.server.UriUtil;
import togos.noise2.DigestUtil;
import togos.noise2.cache.Cache;
import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.Expression;
import togos.noise2.rewrite.ExpressionRewriter;

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
				"+operator@"+UriUtil.uriEncode(funcUrn)+
				"+operand@"+UriUtil.uriEncode(dataUrn);
		}
	}
	
	protected Cache cache;
	protected TNLFunctionDaDaDa_Da next;
	protected String nextUrn;
	
	public CacheDaDaDa_Da( Cache cache, TNLFunctionDaDaDa_Da next ) {
		this.cache = cache;
		this.next = next;
		this.nextUrn = DigestUtil.getSha1Urn( next.toTnl() );
	}
	
	public DataDa apply( final DataDaDaDa in ) {
	    return (DataDa)cache.get(new CacheKey( nextUrn, in.getUrn() ), new FunctionO_O() {
	    	public Object apply( Object cacheKey ) {
	    		return next.apply(in);
	    	}
	    });
	}
	
	public boolean isConstant() {
		return next.isConstant();
	}
	
	public Object rewriteSubExpressions( ExpressionRewriter v ) {
		return new CacheDaDaDa_Da( cache, (TNLFunctionDaDaDa_Da)v.rewrite(next) );
	}
	
	public Expression[] directSubExpressions() {
		return new Expression[]{ next };
	}
	
	public String toTnl() {
	    return "cache("+next.toTnl()+")";
	}
}
