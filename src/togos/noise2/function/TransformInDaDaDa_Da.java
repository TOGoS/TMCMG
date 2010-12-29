package togos.noise2.function;

import togos.noise2.lang.FunctionUtil;


public class TransformInDaDaDa_Da implements SmartFunctionDaDaDa_Da, Cloneable
{
	SmartFunctionDaDaDa_Da next;
	SmartFunctionDaDaDa_Da xfX, xfY, xfZ;
	public TransformInDaDaDa_Da( SmartFunctionDaDaDa_Da xfX, SmartFunctionDaDaDa_Da xfY, SmartFunctionDaDaDa_Da xfZ, SmartFunctionDaDaDa_Da next ) {
		this.xfX = xfX;
		this.xfY = xfY;
		this.xfZ = xfZ;
		this.next = next;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		double[] transformedX = new double[count];
		double[] transformedY = new double[count];
		double[] transformedZ = new double[count];
		xfX.apply(count, inX, inY, inZ, transformedX);
		xfY.apply(count, inX, inY, inZ, transformedY);
		xfZ.apply(count, inX, inY, inZ, transformedZ);
		next.apply(count, transformedX, transformedY, transformedZ, out);
	}
	
	public boolean isConstant() {
		return xfX.isConstant() && xfY.isConstant() && xfZ.isConstant() && next.isConstant();
	}
	
	public SmartFunctionDaDaDa_Da simplify() {
		TransformInDaDaDa_Da simplified;
		try {
			simplified = (TransformInDaDaDa_Da) clone();
        } catch( CloneNotSupportedException e ) {
        	throw new RuntimeException(e);
        }
        simplified.xfX = xfX.simplify();
        simplified.xfY = xfY.simplify();
        simplified.xfZ = xfZ.simplify();
        simplified.next = next.simplify();
		return FunctionUtil.collapseIfConstant(simplified);
	}
}
