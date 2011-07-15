package togos.rdf;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;

import org.bitpedia.util.Base32;

import togos.noise2.DigestUtil;
import togos.noise2.lang.BaseSourceLocation;

public class RDFExpressionUtil
{
	public static String getIdentifier( Object o ) {
		if( o instanceof RDFDescription ) {
			return ((RDFDescription)o).getIdentifier();
		} else {
			return o.toString();
		}
	}
	
	public static String generateIdentifier( RDFDescription e ) {
		String k = toString(e, false);
		byte[] sha1;
		try {
			sha1 = DigestUtil.createSha1Digestor().digest(k.getBytes("UTF-8"));
		} catch( UnsupportedEncodingException e1 ) {
			throw new RuntimeException(e1);
		}
		return Base32.encode(sha1);
	}
	
	public static void write( RDFDescription e, boolean includeSourceLoc, StringBuilder sb ) {
		sb.append("`");
		sb.append(e.getTypeName());
		sb.append("`");
		if( e.getAttributeEntries().size() > 0 ) {
			boolean wroteAny = false;
			sb.append("{ ");
			for( Iterator i=e.getAttributeEntries().iterator(); i.hasNext(); ) {
				if( wroteAny ) {
					sb.append(", ");
				}
				Map.Entry kv = (Map.Entry)i.next();
				sb.append("`");
				sb.append( kv.getKey().toString() );
				sb.append("`");
				sb.append("@");
				write( (RDFExpression)kv.getValue(), includeSourceLoc, sb );
				wroteAny = true;
			}
			sb.append(" }");
		}
	}
	
	public static void write( RDFExpression e, boolean includeSourceLoc, StringBuilder sb ) {
		if( e instanceof RDFDescription ) {
			write( (RDFDescription)e, includeSourceLoc, sb );
		} else if( e instanceof RDFLiteral ) {
			sb.append('"');
			sb.append( ((RDFLiteral)e).getValue() );
			sb.append('"');
		} else if( e instanceof RDFURIRef ) {
			sb.append('`');
			sb.append( ((RDFURIRef)e).getUri() );
			sb.append('`');
		} else {
			sb.append( e );
		}
		if( includeSourceLoc ) {
			sb.append( ' ' );
			sb.append( BaseSourceLocation.toString(e.getSourceLocation()) );
		}
	}
	
	public static String toString( RDFExpression e, boolean includeSourceLoc ) {
		StringBuilder sb = new StringBuilder();
		write( e, includeSourceLoc, sb );
		return sb.toString();
	}
	
	public static int hashCode( RDFDescription e ) {
		return e.getIdentifier().hashCode() + 1;
	}
	
	public static boolean equals( RDFDescription d1, RDFDescription d2 ) {
		return d1.getIdentifier().equals( d2.getIdentifier() );
	}
}
