package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class Constant_Da extends TNLFunctionDaDaDa_Da implements FunctionDaDa_Da
{
	public static final Constant_Da ZERO = new Constant_Da(0);
	
	public static Constant_Da forValue( double v ) {
		if( v == 0 ) {
			return ZERO;
		} else {
			return new Constant_Da(v);
		}
	}
	
	public double value; // this sale is for a limited time only!
	
	public Constant_Da( double value ) {
		this.value = value;
	}
	
	public DataDa apply( int count ) {
		double[] out = new double[count];
		for( int j=0; j<count; ++j ) {
			out[j] = value;
		}
		return new DataDa(out);
	}
	
	public DataDa apply( DataDaDa in ) {
		return apply( in.getLength() );
	}
	
	public DataDa apply( DataDaDaDa in ) {
		return apply( in.getLength() );
	}
	
	public boolean equals( Object oth ) {
		if( !(oth instanceof Constant_Da) ) return false;
		return value == ((Constant_Da)oth).value;
	}
	
	public String toString() {
		return "constant-double("+value+")";
	}
	
	public String toTnl() {
		return Double.toString(value);
	}
	
	public boolean isConstant() {
		return true;
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
	    return this;
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public int getTriviality() {
	    return 100;
	}
}
