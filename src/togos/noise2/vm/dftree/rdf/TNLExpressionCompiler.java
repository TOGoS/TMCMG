package togos.noise2.vm.dftree.rdf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import togos.lang.SourceLocation;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.TNLApplyExpression;
import togos.noise2.lang.TNLBlockExpression;
import togos.noise2.lang.TNLExpression;
import togos.noise2.lang.TNLLiteralExpression;
import togos.noise2.lang.TNLSymbolExpression;
import togos.rdf.BaseRDFDescription;
import togos.rdf.BaseRDFLiteral;
import togos.rdf.RDFExpression;
import togos.rdf.SimpleEntry;

public class TNLExpressionCompiler
{
	public interface Macro {
		public RDFExpression compile( List pArgs, List nArgs, SourceLocation sloc, TNLExpressionCompiler comp ) throws CompileError;
	}
	
	public static class SimpleArgListMacro implements Macro {
		String funcName;
		String[] argNames;
		List defaultArgs;
		int minArgs = 0;
		int maxArgs = 0;
		
		public SimpleArgListMacro( String funcName, String[] argNames, List defaultArgs ) {
			this.funcName = funcName;
			this.argNames = argNames;
			this.defaultArgs = defaultArgs;
			for( int i=0; i<argNames.length; ++i ) {
				if( "+".equals(argNames[i]) ) {
					maxArgs = Integer.MAX_VALUE;
				} else {
					if( maxArgs < Integer.MAX_VALUE ) ++maxArgs;
					++minArgs;
				}
			}
		}
		
		public SimpleArgListMacro( String funcName, String[] argNames ) {
			this( funcName, argNames, Collections.EMPTY_LIST );
		}
		
		public RDFExpression compile( List _pArgs, List nArgs, SourceLocation sloc, TNLExpressionCompiler comp ) throws CompileError {
			List pArgs; 
			if( defaultArgs.size() > _pArgs.size() ) {
				pArgs = new ArrayList(defaultArgs);
				for( int i=0; i<_pArgs.size(); ++i ) {
					pArgs.set( i, _pArgs.get(i) );
				}
			} else {
				pArgs = _pArgs;
			}
			if( pArgs.size() < minArgs ) throw new CompileError( funcName + " requires at least " + minArgs + " arguments; only "+pArgs.size()+" given (including defaults)", sloc );
			if( pArgs.size() > maxArgs ) throw new CompileError( funcName + " takes at most " + maxArgs + " arguments; "+pArgs.size()+" given (including defaults)", sloc );
			
			ArrayList attrs = new ArrayList();
			for( int i=0, j=0; i<argNames.length && j<pArgs.size(); ++i, ++j ) {
				if( "+".equals(argNames[i]) || "*".equals(argNames[i]) ) {
					++i;
					while( j<pArgs.size() ) {
						attrs.add( new SimpleEntry(argNames[i], comp.compile((TNLExpression)pArgs.get(j))) );
						++j;
					}
				} else {
					attrs.add( new SimpleEntry(argNames[i], comp.compile((TNLExpression)pArgs.get(j))) );
				}
			}
			
			return new BaseRDFDescription( funcName, attrs, sloc );
		}
	}
	
	protected Object resolve( String symbol, TNLExpression scope ) {
		while( scope != null && (!(scope instanceof TNLBlockExpression) || !((TNLBlockExpression)scope).definitions.containsKey(symbol)) ) {
			scope = scope.parent;
		}
		return scope == null ? null : ((TNLBlockExpression)scope).definitions.get(symbol);
	}
	
	protected Macro resolveMacro( TNLExpression exp ) throws CompileError {
		while( exp instanceof TNLSymbolExpression ) {
			Object o = resolve( ((TNLSymbolExpression)exp).symbol, exp );
			if( o == null ) {
				throw new CompileError("Couldn't resolve "+exp, exp);
			}
			if( o instanceof Macro ) return (Macro)o;
			else if( o instanceof TNLSymbolExpression ) exp = (TNLExpression)o;
			else throw new CompileError("Couldn't resolve to macro: "+exp, exp); 
		}
		throw new CompileError("Couldn't resolve to macro: "+exp, exp);
	}
	
	protected RDFExpression compileApply( TNLExpression funcExpression, List pArgs, List nArgs, SourceLocation sloc ) throws CompileError {
		return resolveMacro(funcExpression).compile( pArgs, nArgs, sloc, this );
	}
	
	public RDFExpression compile( TNLSymbolExpression exp ) throws CompileError {
		return compileApply( exp, Collections.EMPTY_LIST, Collections.EMPTY_LIST, exp );
	}
	
	public RDFExpression compile( TNLApplyExpression exp ) throws CompileError {
		// This would be a good place to expand splatted arguments,
		// if I ever want to support that.
		return compileApply( exp.functionExpression, exp.argumentExpressions, exp.namedArgumentExpressionEntries, exp );
	}

	public RDFExpression compile( TNLLiteralExpression exp ) throws CompileError {
		return new BaseRDFLiteral( exp.value, exp );
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
