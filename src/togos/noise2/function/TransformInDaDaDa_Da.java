package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.rewrite.ExpressionRewriter;


public class TransformInDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	FunctionDaDaDa_Da next;
	FunctionDaDaDa_Da xfX, xfY, xfZ;
	public TransformInDaDaDa_Da( FunctionDaDaDa_Da xfX, FunctionDaDaDa_Da xfY, FunctionDaDaDa_Da xfZ, FunctionDaDaDa_Da next ) {
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
		return FunctionUtil.isConstant(xfX) && FunctionUtil.isConstant(xfY) && FunctionUtil.isConstant(xfZ) && FunctionUtil.isConstant(next);
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw)  {
		return new TransformInDaDaDa_Da(
			(TNLFunctionDaDaDa_Da)rw.rewrite(xfX),
			(TNLFunctionDaDaDa_Da)rw.rewrite(xfY),
			(TNLFunctionDaDaDa_Da)rw.rewrite(xfZ),
			(TNLFunctionDaDaDa_Da)rw.rewrite(next)
		);
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{ xfX, xfY, xfZ };
	}
	
	public String toString() {
		return "xf("+xfX+", "+xfY+", "+xfZ+", "+next+")";
	}
	public String toTnl() {
		return "xf("+FunctionUtil.toTnl(xfX)+", "+FunctionUtil.toTnl(xfY)+", "+FunctionUtil.toTnl(xfZ)+", "+FunctionUtil.toTnl(next)+")";
	}
}
