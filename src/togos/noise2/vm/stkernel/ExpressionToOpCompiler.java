package togos.noise2.vm.stkernel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import togos.noise2.rdf.ExprUtil;
import togos.noise2.rdf.Expression;
import togos.noise2.rdf.TNLNamespace;
import togos.noise2.vm.OpWriter;

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
	
	protected boolean shouldInlineExpr( String exprId ) {
		Integer u = (Integer)exprUsage.get(exprId);
		if( u == null ) {
			throw new RuntimeException("Expression was not counted or something");
		}
		return u.intValue() == 1;
	}
	
	protected String[] getArgStrings( Expression expr, String[] argNames ) {
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
				args.add( compile( it.next() ) );
			}
		}
		String[] s = new String[args.size()];
		for( int i=0; i<s.length; ++i ) {
			s[i] = (String)args.get(i);
		}
		return s;
	}
	
	public String compile( Object expr ) {
		String vn = (String)exprVars.get( ExprUtil.getIdentifier(expr) );
		if( vn != null ) return vn;
		
		if( expr instanceof Expression ) {
			return compile( (Expression)expr );
		} else if( expr instanceof Double ) {
			return w.writeConstant( null, ((Double)expr).doubleValue() );
		} else {
			throw new RuntimeException("Don't know how to compile "+expr);
		}
	}
	
	protected String compile( Expression expr ) {
		String varName;
		if( shouldInlineExpr( ExprUtil.getIdentifier(expr) ) ) {
			varName = null;
		} else {
			// TODO: Could re-use old variables, here.
			varName = w.declareVar("double", "var");
		}
		
		String typeName = expr.getTypeName();
		if( TNLNamespace.ADD.equals(typeName) ) {
			return w.writeOp( varName, typeName, getArgStrings(expr,ADD_ARGS) );
		} else {
			throw new RuntimeException("Unsupported expression type "+typeName);
		}
	}
}
