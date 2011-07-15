package togos.noise2.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.noise2.lang.CompileError;
import togos.noise2.rdf.TNLNamespace;
import togos.rdf.RDFDescription;
import togos.rdf.RDFExpression;
import togos.rdf.RDFExpressionUtil;
import togos.rdf.RDFLiteral;

public class ExpressionToOpCompiler
{
	Map exprVars = new HashMap();
	Map exprUsage = new HashMap();
	OpWriter w;
	
	public ExpressionToOpCompiler(OpWriter w) {
		this.w = w;
	}
	
	protected void markExprUsage( String exprId ) {
		Integer u = (Integer)exprUsage.get(exprId);
		if( u == null ) u = Integer.valueOf(1);
		else u = Integer.valueOf(u.intValue()+1);
		exprUsage.put(exprId,u);
	}
	
	protected void buildExprUsage( RDFDescription expr ) {
		markExprUsage( expr.getIdentifier() );
		for( Iterator i=expr.getAttributeEntries().iterator(); i.hasNext(); ) {
			Map.Entry en = (Map.Entry)i.next();
			if( en.getValue() instanceof RDFDescription ) {
				buildExprUsage( (RDFDescription)en.getValue() );
			}
		}
	}
	
	protected boolean shouldInlineExpr( Object expr ) {
		String exprId = RDFExpressionUtil.getIdentifier(expr);
		Integer u = (Integer)exprUsage.get(exprId);
		if( u == null ) {
			throw new RuntimeException("Expression "+expr+" was not counted or something");
		}
		return u.intValue() == 1;
	}
	
	protected String[] getArgStrings( RDFDescription expr, String[] argNames ) throws CompileError {
		ArrayList args = new ArrayList();
		boolean allowZero=false, allowMulti;
		for( int i=0; i<argNames.length; ++i ) {
			if( "+".equals(argNames[i]) ) {
				allowMulti = true;
				++i;
			} else {
				allowMulti = false;
			}
			List values = expr.getAttributeValues(argNames[i]);
			if( !allowMulti && values.size() > 1 ) {
				throw new RuntimeException( "Too many "+argNames[i]+" arguments provided to "+expr.getTypeName() );
			}
			if( !allowZero && values.size() == 0 ) {
				throw new RuntimeException( "Zero "+argNames[i]+" arguments provided to "+expr.getTypeName() );
			}
			for( Iterator it=values.iterator(); it.hasNext(); ) {
				args.add( _compile( (RDFExpression)it.next() ) );
			}
		}
		String[] s = new String[args.size()];
		for( int i=0; i<s.length; ++i ) {
			s[i] = (String)args.get(i);
		}
		return s;
	}
	
	public void bind( Object expr, String var ) {
		exprVars.put( RDFExpressionUtil.getIdentifier(expr), var );	
	}
	
	protected String _compileApply( RDFDescription expr ) throws CompileError {
		String varName;
		if( shouldInlineExpr( expr ) ) {
			varName = null;
		} else {
			// TODO: Could re-use old variables, here.
			varName = w.declareVar("double", "var");
		}
		
		String typeName = expr.getTypeName();
		if( TNLNamespace.ADD.equals(typeName) ) {
			return w.writeOp( varName, typeName, getArgStrings(expr,TNLNamespace.ADD_ARGS) );
		} else if( TNLNamespace.SUBTRACT.equals(typeName) ) {
			return w.writeOp( varName, typeName, getArgStrings(expr,TNLNamespace.SUBTRACT_ARGS) );
		} else if( TNLNamespace.MULTIPLY.equals(typeName) ) {
			return w.writeOp( varName, typeName, getArgStrings(expr,TNLNamespace.MULTIPLY_ARGS) );
		} else if( TNLNamespace.DIVIDE.equals(typeName) ) {
			return w.writeOp( varName, typeName, getArgStrings(expr,TNLNamespace.DIVIDE_ARGS) );
		} else {
			throw new RuntimeException("Unsupported expression type "+typeName);
		}
	}
	
	protected String _compile( RDFExpression expr ) throws CompileError {
		String vn = (String)exprVars.get( RDFExpressionUtil.getIdentifier(expr) );
		if( vn != null ) return vn;
		
		String var;
		if( expr instanceof RDFDescription ) {
			var = _compileApply( (RDFDescription)expr );
		} else if( expr instanceof RDFLiteral ) {
			Object val = ((RDFLiteral)expr).getValue();
			if( val == null ) {
				throw new CompileError("Don't know how to compile literal NULL", expr.getSourceLocation());
			} else if( val instanceof Double ) {
				var = w.writeConstant( null, ((Double)val).doubleValue() );
			} else {
				throw new CompileError("Don't know how to compile literal "+val.getClass(), expr.getSourceLocation());
			}
		} else {
			throw new CompileError("Don't know how to compile "+expr.getClass(), expr.getSourceLocation());
		}
		bind( expr, var );
		return var;
	}
	
	public String compile( RDFDescription expr ) throws CompileError {
		buildExprUsage(expr);
		return _compile( expr );
	}
}
