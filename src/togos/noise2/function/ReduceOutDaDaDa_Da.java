package togos.noise2.function;

import togos.noise2.lang.FunctionUtil;


public abstract class ReduceOutDaDaDa_Da implements SmartFunctionDaDaDa_Da, Cloneable
{
	SmartFunctionDaDaDa_Da[] components;
	public ReduceOutDaDaDa_Da( SmartFunctionDaDaDa_Da[] components ) {
		this.components = components;
	}
	
	protected abstract void reduce( int count, double[] subOut, double[] out );
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		components[0].apply(count, inX, inY, inZ, out);
		double[] subOut = new double[count];
		for( int i=1; i<components.length; ++i ) {
			components[i].apply(count, inX, inY, inZ, subOut);
			reduce( count, subOut, out );
		}
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
	
	public String toString() {
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
			return super.toString();
		}
		boolean first = true;
		for( int i=0; i<components.length; ++i ) {
			if( !first ) s += separator;
			s += components[i].toString();
			first = false;
		}
		s += ")";
		return s;
	}
	
	public boolean isConstant() {
		for( int i=0; i<components.length; ++i ) {
			if( !components[i].isConstant() ) return false;
		}
		return true;
	}
	
	public SmartFunctionDaDaDa_Da simplify() {
		SmartFunctionDaDaDa_Da[] simplifiedComponents = new SmartFunctionDaDaDa_Da[components.length];
		for( int i=0; i<components.length; ++i ) {
			simplifiedComponents[i] = components[i].simplify();
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
}
