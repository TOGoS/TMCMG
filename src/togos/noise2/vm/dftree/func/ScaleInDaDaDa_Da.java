package togos.noise2.vm.dftree.func;

import togos.noise2.rewrite.ExpressionRewriter;
import togos.noise2.vm.dftree.data.DataDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.lang.FunctionUtil;

public class ScaleInDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	FunctionDaDaDa_Da next;
	double scaleX, scaleY, scaleZ;
	public ScaleInDaDaDa_Da( double scaleX, double scaleY, double scaleZ, FunctionDaDaDa_Da next ) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.next = next;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		final int vectorSize = in.getLength();
		double[] scaledX = new double[vectorSize];
		double[] scaledY = new double[vectorSize];
		double[] scaledZ = new double[vectorSize];
		for( int i=vectorSize-1; i>=0; --i ) {
			scaledX[i] = in.x[i]*scaleX;
			scaledY[i] = in.y[i]*scaleY;
			scaledZ[i] = in.z[i]*scaleZ;
		}
		return next.apply(new DataDaDaDa(vectorSize, scaledX, scaledY, scaledZ));
	}
	
	public boolean isConstant() {
		return FunctionUtil.isConstant(next);
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new ScaleInDaDaDa_Da(scaleX, scaleY, scaleZ, (TNLFunctionDaDaDa_Da)rw.rewrite(next));
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toTnl() {
		return "scale-in("+scaleX+", "+scaleY+", "+scaleZ+", "+FunctionUtil.toTnl(next)+")";
	}
}
