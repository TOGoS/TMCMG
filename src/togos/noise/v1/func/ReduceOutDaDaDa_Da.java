package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.lang.FunctionUtil;
import togos.noise.v1.rewrite.ExpressionRewriter;


public abstract class ReduceOutDaDaDa_Da
	extends TNLFunctionDaDaDa_Da
	implements Cloneable
{
	FunctionDaDaDa_Da[] components;
	public ReduceOutDaDaDa_Da( FunctionDaDaDa_Da[] components ) {
		this.components = components;
	}
	
	protected abstract void reduce( int count, double[] subOut, double[] out );
	
	public DataDa apply( DataDaDaDa in ) {
		int vectorSize = in.getLength();
		double[] first = components[0].apply(in).x;
		double[] out = new double[in.getLength()];
		for( int j=0; j<vectorSize; ++j ) {
			out[j] = first[j];
		}
		for( int i=1; i<components.length; ++i ) {
			reduce( vectorSize, components[i].apply(in).x, out );
		}
		return new DataDa(vectorSize,out);
	}
	
	public boolean equals( Object oth ) {
		if( oth.getClass() != getClass() ) return false;
		
		if( ((ReduceOutDaDaDa_Da)oth).components.length != components.length ) return false;
			
		for( int i=0; i<components.length; ++i ) {
			if( !components[i].equals( ((ReduceOutDaDaDa_Da)oth).components[i] ) ) return false;
		}
		return true;
	}
	
	protected String getOperatorSymbol() { return null; }
	protected String getMacroName() { return null; }
	
	public String toTnl(boolean tnlifySubs) {
		String macroName = getMacroName();
		String opSymbol = getOperatorSymbol();
		String separator;
		String s;
		if( macroName != null ) {
			s = macroName + "(";
			separator = ", ";
		} else if( opSymbol != null ) {
			s = "(";
			separator = " "+opSymbol+" ";
		} else {
			throw new RuntimeException("No opSymbol or macroName; can't toTnl "+getClass());
		}
		boolean first = true;
		for( int i=0; i<components.length; ++i ) {
			if( !first ) s += separator;
			s += (tnlifySubs ? FunctionUtil.toTnl(components[i]) : components[i].toString());
			first = false;
		}
		s += ")";
		return s;
	}
	
	public String toTnl() {
		return toTnl(true);
	}
	
	public String toString() {
		return toTnl(false);
	}
	
	public boolean isConstant() {
		for( int i=0; i<components.length; ++i ) {
			if( !FunctionUtil.isConstant(components[i]) ) return false;
		}
		return true;
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		TNLFunctionDaDaDa_Da[] simplifiedComponents = new TNLFunctionDaDaDa_Da[components.length];
		for( int i=0; i<components.length; ++i ) {
			simplifiedComponents[i] = (TNLFunctionDaDaDa_Da)rw.rewrite(components[i]);
		}
		ReduceOutDaDaDa_Da simplified;
        try {
	        simplified = (ReduceOutDaDaDa_Da)this.clone();
        } catch( CloneNotSupportedException e ) {
        	throw new RuntimeException(e);
        }
		simplified.components = simplifiedComponents;
		return FunctionUtil.collapseIfConstant(simplified);
	}
	
	public Object[] directSubExpressions() {
		return components;
	}
}
