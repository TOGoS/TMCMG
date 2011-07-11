package togos.noise2.lang;

import java.util.HashSet;

import togos.noise2.rdf.BaseRDFObjectExpression;
import togos.noise2.rdf.BaseRDFLiteralExpression;
import togos.rdf.RDFExpression;

public class TNLExpressionCompiler
{
	public HashSet primitiveSymbols = new HashSet();
	
	protected TNLExpression resolve( String symbol, TNLExpression scope ) {
		while( scope != null && (!(scope instanceof TNLBlockExpression) || !((TNLBlockExpression)scope).definitions.containsKey(symbol)) ) {
			scope = scope.parent;
		}
		return (TNLExpression)(scope == null ? null : ((TNLBlockExpression)scope).definitions.get(symbol));
	}
	
	public RDFExpression compile( TNLSymbolExpression exp ) throws CompileError {
		String symbol = exp.symbol;
		if( primitiveSymbols.contains(symbol) ) {
			return new BaseRDFObjectExpression(symbol, exp);
		}
		TNLExpression e = resolve(symbol,exp);
		if( e == null ) {
			throw new CompileError("Couldn't resolve symbol `"+symbol+"`", exp);
		}
		return compile( e );
	}
	
	public RDFExpression compile( TNLApplyExpression exp ) throws CompileError {
		return null;
	}

	public RDFExpression compile( TNLLiteralExpression exp ) throws CompileError {
		return new BaseRDFLiteralExpression( exp.value, exp );
	}
	
	public RDFExpression compile( TNLExpression exp ) throws CompileError {
		if( exp instanceof TNLSymbolExpression ) {
			return compile( (TNLSymbolExpression)exp );
		} else if( exp instanceof TNLLiteralExpression ) {
			return compile( (TNLLiteralExpression)exp );
		} else if( exp instanceof TNLApplyExpression ) {
			return compile( (TNLApplyExpression)exp );
		} else {
			throw new CompileError( "Don't know how to RDF-ify "+exp.getClass(), exp );
		}
	}
}
