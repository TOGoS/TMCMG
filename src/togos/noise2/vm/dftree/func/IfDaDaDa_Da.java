package togos.noise2.vm.dftree.func;

import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.lang.FunctionUtil;

public class IfDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	FunctionDaDaDa_Da[] funx;
	
	public IfDaDaDa_Da( FunctionDaDaDa_Da[] funx ) {
		if( funx.length % 2 == 0 ) {
			throw new RuntimeException("if(...) expression requires an odd number of arguments, given "+funx.length); 
		}
		this.funx = funx;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		int len = in.getLength();
		boolean anyFalses;
		boolean[] capturedList = new boolean[len];
		DataDa res = new DataDa(new double[len]);
		int condi;
		for( condi=0; condi<funx.length-1; condi+=2 ) {
			DataDa condData = funx[condi].apply(in);
			DataDa resData  = funx[condi+1].apply(in);
			anyFalses = false;
			for( int i=len-1; i>=0; --i ) {
				if( !capturedList[i] && condData.x[i] > 0 ) {
					res.x[i] = resData.x[i];
					capturedList[i] = true;
				} else {
					anyFalses = true;
				}
			}
			if( !anyFalses ) return res;
		}
		DataDa elseResData = funx[condi].apply(in);
		for( int i=len-1; i>=0; --i ) {
			if( !capturedList[i] ) {
				res.x[i] = elseResData.x[i];
			}
		}
		return res;
	}
	
	public Object[] directSubExpressions() {
		return funx;
	}

	public Object rewriteSubExpressions( ExpressionRewriter rw ) {
		FunctionDaDaDa_Da[] rwFunx = new FunctionDaDaDa_Da[funx.length];
		for( int i=0; i<funx.length; ++i ) {
			rwFunx[i] = (FunctionDaDaDa_Da)rw.rewrite(funx[i]);
		}
		return new IfDaDaDa_Da(rwFunx);
	}

	public String toTnl() {
		String tnl = "if(";
		for( int i=0; i<funx.length; ++i ) {
			if( i != 0 ) tnl += ", ";
			tnl += FunctionUtil.toTnl(funx[i]);
		}
		tnl += ")";
		return tnl;
	}

	public String toString() {
		String str = "if(";
		for( int i=0; i<funx.length; ++i ) {
			if( i != 0 ) str += ", ";
			str += funx[i];
		}
		str += ")";
		return str;
	}
	
	public boolean isConstant() {
		for( int i=0; i<funx.length; ++i ) {
			if( !FunctionUtil.isConstant(funx[i]) ) return false;
		}
		return true;
	}
}
