package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;


public class TransformInDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	TNLFunctionDaDaDa_Da next;
	TNLFunctionDaDaDa_Da xfX, xfY, xfZ;
	public TransformInDaDaDa_Da( TNLFunctionDaDaDa_Da xfX, TNLFunctionDaDaDa_Da xfY, TNLFunctionDaDaDa_Da xfZ, TNLFunctionDaDaDa_Da next ) {
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
			(TNLFunctionDaDaDa_Da)rw.rewrite(xfX),
			(TNLFunctionDaDaDa_Da)rw.rewrite(xfY),
			(TNLFunctionDaDaDa_Da)rw.rewrite(xfZ),
			(TNLFunctionDaDaDa_Da)rw.rewrite(next)
		);
	}
	
	public String toTnl() {
		return "transform-in("+xfX.toTnl()+", "+xfY.toTnl()+", "+xfZ.toTnl()+", "+next.toTnl()+")";
	}
}
