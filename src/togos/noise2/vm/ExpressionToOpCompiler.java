package togos.noise2.vm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.noise2.rdf.ExprUtil;
import togos.noise2.rdf.RDFApplyExpression;
import togos.noise2.rdf.TNLNamespace;

public class ExpressionToOpCompiler
{
	Map exprVars = new HashMap();
	Map exprUsage = new HashMap();
	OpWriter w;
	
	public ExpressionToOpCompiler(OpWriter w) {
		this.w = w;
	}
	
	protected final String[] ADD_ARGS = new String[]{ "+", TNLNamespace.TERM };
	protected final String[] SUBTRACT_ARGS = new String[]{ TNLNamespace.MINUEND, "+", TNLNamespace.SUBTRAHEND };
	protected final String[] MULTIPLY_ARGS = new String[]{ "+", TNLNamespace.FACTOR };
	protected final String[] DIVIDE_ARGS = new String[]{ TNLNamespace.DIVIDEND, "+", TNLNamespace.DIVISOR };
	
	protected void markExprUsage( String exprId ) {
		Integer u = (Integer)exprUsage.get(exprId);
		if( u == null ) u = Integer.valueOf(1);
		else u = Integer.valueOf(u.intValue()+1);
		exprUsage.put(exprId,u);
	}
	
	protected void buildExprUsage( RDFApplyExpression expr ) {
		markExprUsage( expr.getIdentifier() );
		for( Iterator i=expr.getAttributeEntries().iterator(); i.hasNext(); ) {
			Map.Entry en = (Map.Entry)i.next();
			if( en.getValue() instanceof RDFApplyExpression ) {
				buildExprUsage( (RDFApplyExpression)en.getValue() );
			}
		}
	}
	
	protected boolean shouldInlineExpr( Object expr ) {
		String exprId = ExprUtil.getIdentifier(expr);
		Integer u = (Integer)exprUsage.get(exprId);
		if( u == null ) {
			throw new RuntimeException("Expression "+expr+" was not counted or something");
		}
		return u.intValue() == 1;
	}
	
	protected String[] getArgStrings( RDFApplyExpression expr, String[] argNames ) {
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
				args.add( _compile( it.next() ) );
			}
		}
		String[] s = new String[args.size()];
		for( int i=0; i<s.length; ++i ) {
			s[i] = (String)args.get(i);
		}
		return s;
	}
	
	public void bind( Object expr, String var ) {
		exprVars.put( ExprUtil.getIdentifier(expr), var );	
	}
	
	protected String _compile( RDFApplyExpression expr ) {
		String varName;
		if( shouldInlineExpr( expr ) ) {
			varName = null;
		} else {
			// TODO: Could re-use old variables, here.
			varName = w.declareVar("double", "var");
		}
		
		String typeName = expr.getTypeName();
		if( TNLNamespace.ADD.equals(typeName) ) {
			return w.writeOp( varName, typeName, getArgStrings(expr,ADD_ARGS) );
		} else if( TNLNamespace.SUBTRACT.equals(typeName) ) {
			return w.writeOp( varName, typeName, getArgStrings(expr,SUBTRACT_ARGS) );
		} else if( TNLNamespace.MULTIPLY.equals(typeName) ) {
			return w.writeOp( varName, typeName, getArgStrings(expr,MULTIPLY_ARGS) );
		} else if( TNLNamespace.DIVIDE.equals(typeName) ) {
			return w.writeOp( varName, typeName, getArgStrings(expr,DIVIDE_ARGS) );
		} else {
			throw new RuntimeException("Unsupported expression type "+typeName);
		}
	}
	
	protected String _compile( Object expr ) {
		String vn = (String)exprVars.get( ExprUtil.getIdentifier(expr) );
		if( vn != null ) return vn;
		
		String var;
		if( expr instanceof RDFApplyExpression ) {
			var = _compile( (RDFApplyExpression)expr );
		} else if( expr instanceof Double ) {
			var = w.writeConstant( null, ((Double)expr).doubleValue() );
		} else {
			throw new RuntimeException("Don't know how to compile "+expr);
		}
		bind( expr, var );
		return var;
	}
	
	public String compile( RDFApplyExpression expr ) {
		buildExprUsage(expr);
		return _compile((Object)expr);
	}
}
