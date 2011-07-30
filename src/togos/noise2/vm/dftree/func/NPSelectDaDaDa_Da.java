package togos.noise2.vm.dftree.func;

import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.lang.FunctionUtil;

public class NPSelectDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	FunctionDaDaDa_Da n, p;
	FunctionDaDaDa_Da selector;
	
	public NPSelectDaDaDa_Da( FunctionDaDaDa_Da n, FunctionDaDaDa_Da p, FunctionDaDaDa_Da selector ) {
		this.n = n;
		this.p = p;
		this.selector = selector;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		int count = in.getLength();
		
		DataDa sValues = selector.apply(in);
		
		// TODO: if n and p are complicated, could save a lot of time
		// by only calling them for the points where they apply.
		DataDa nValues = n.apply(in);
		DataDa pValues = p.apply(in);
		
		double[] resultValues = new double[count]; 
		for( int i=0; i<count; ++i ) {
			resultValues[i] = sValues.x[i] > 0 ? pValues.x[i] : nValues.x[i];
		}
		return new DataDa(resultValues);
	}
	
	public Object[] directSubExpressions() {
	    return new Object[] { n, p, selector };
	}
	
	public Object rewriteSubExpressions( ExpressionRewriter erw ) {
	    return new NPSelectDaDaDa_Da(
	    	(FunctionDaDaDa_Da)erw.rewrite(n),
	    	(FunctionDaDaDa_Da)erw.rewrite(p),
	    	(FunctionDaDaDa_Da)erw.rewrite(selector)
	    );
	}
	
	public String toTnl() {
	    return "np-select("+FunctionUtil.toTnl(n)+", "+FunctionUtil.toTnl(p)+", "+FunctionUtil.toTnl(selector)+")";
	}
	
	public String toString() {
	    return "np-select("+n+", "+p+", "+selector+")";
	}
	
	public boolean isConstant() {
		return
			FunctionUtil.isConstant(selector) &&
			FunctionUtil.isConstant(n) &&
			FunctionUtil.isConstant(p);
	}
}
