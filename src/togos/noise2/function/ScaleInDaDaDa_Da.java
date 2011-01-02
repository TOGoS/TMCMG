package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;

public class ScaleInDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da next;
	double scaleX, scaleY, scaleZ;
	public ScaleInDaDaDa_Da( double scaleX, double scaleY, double scaleZ, SmartFunctionDaDaDa_Da next ) {
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.scaleZ = scaleZ;
		this.next = next;
	}
	
	public void apply( InputDaDaDa in, double[] out ) {
		double[] scaledX = new double[in.count];
		double[] scaledY = new double[in.count];
		double[] scaledZ = new double[in.count];
		for( int i=in.count-1; i>=0; --i ) {
			scaledX[i] = in.x[i]*scaleX;
			scaledY[i] = in.y[i]*scaleY;
			scaledZ[i] = in.z[i]*scaleZ;
		}
		next.apply(in.count, scaledX, scaledY, scaledZ, out);
	}
	
	public boolean isConstant() {
		return next.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new ScaleInDaDaDa_Da(scaleX, scaleY, scaleZ, (SmartFunctionDaDaDa_Da)rw.rewrite(next));
	}
	
	public String toString() {
		return "scale-in("+scaleX+", "+scaleY+", "+scaleZ+", "+next+")";
	}
}
