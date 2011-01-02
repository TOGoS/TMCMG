package togos.noise2.function;

import togos.noise2.InputDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;


public class TerrainScaleDaDaDa_Da extends SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da next;
	double hScale, vScale;
	public TerrainScaleDaDaDa_Da( double hScale, double vScale, SmartFunctionDaDaDa_Da next ) {
		this.hScale = hScale;
		this.vScale = vScale;
		this.next = next;
	}
	
	public void apply( InputDaDaDa in, double[] out ) {
		double[] scaledX = new double[in.count];
		double[] scaledY = new double[in.count];
		double[] scaledZ = new double[in.count];
		for( int i=in.count-1; i>=0; --i ) {
			scaledX[i] = in.x[i]/hScale;
			scaledY[i] = in.y[i]/hScale;
			scaledZ[i] = in.z[i]/hScale;
		}
		next.apply(in.count, scaledX, scaledY, scaledZ, out);
		for( int i=in.count-1; i>=0; --i ) out[i] *= vScale;
	}
	
	public boolean isConstant() {
		return next.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new TerrainScaleDaDaDa_Da(hScale, vScale, (SmartFunctionDaDaDa_Da)rw.rewrite(next));
	}
	
	public String toString() {
		return "terrain-scale("+hScale+", "+vScale+", "+next+")";
	}
}
