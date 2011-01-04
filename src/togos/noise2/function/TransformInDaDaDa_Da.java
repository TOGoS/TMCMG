package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
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
	
	public DataDa apply( DataDaDaDa in ) {
		return next.apply(new DataDaDaDa(
			xfX.apply(in).v,
			xfY.apply(in).v,
			xfZ.apply(in).v
		));
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
