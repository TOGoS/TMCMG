package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;


public class TransformInDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da next;
	SmartFunctionDaDaDa_Da xfX, xfY, xfZ;
	public TransformInDaDaDa_Da( SmartFunctionDaDaDa_Da xfX, SmartFunctionDaDaDa_Da xfY, SmartFunctionDaDaDa_Da xfZ, SmartFunctionDaDaDa_Da next ) {
		this.xfX = xfX;
		this.xfY = xfY;
		this.xfZ = xfZ;
		this.next = next;
	}
	
	public void apply( InputDaDaDa in, double[] out ) {
		double[] transformedX = new double[in.count];
		double[] transformedY = new double[in.count];
		double[] transformedZ = new double[in.count];
		xfX.apply(in.count, in.x, in.y, in.z, transformedX);
		xfY.apply(in.count, in.x, in.y, in.z, transformedY);
		xfZ.apply(in.count, in.x, in.y, in.z, transformedZ);
		next.apply(in.count, transformedX, transformedY, transformedZ, out);
	}
	
	public boolean isConstant() {
		return xfX.isConstant() && xfY.isConstant() && xfZ.isConstant() && next.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw)  {
		return new TransformInDaDaDa_Da(
			(SmartFunctionDaDaDa_Da)rw.rewrite(xfX),
			(SmartFunctionDaDaDa_Da)rw.rewrite(xfY),
			(SmartFunctionDaDaDa_Da)rw.rewrite(xfZ),
			(SmartFunctionDaDaDa_Da)rw.rewrite(next)
		);
	}
	
	public String toString() {
		return "transform-in("+xfX+", "+xfY+", "+xfZ+", "+next+")";
	}
}
