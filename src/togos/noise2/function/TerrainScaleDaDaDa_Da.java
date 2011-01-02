package togos.noise2.function;

import togos.noise2.rewrite.ExpressionRewriter;


public class TerrainScaleDaDaDa_Da implements SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da next;
	double hScale, vScale;
	public TerrainScaleDaDaDa_Da( double hScale, double vScale, SmartFunctionDaDaDa_Da next ) {
		this.hScale = hScale;
		this.vScale = vScale;
		this.next = next;
	}
	
	public void apply( int count, double[] inX, double[] inY, double[] inZ, double[] out ) {
		double[] scaledX = new double[count];
		double[] scaledY = new double[count];
		double[] scaledZ = new double[count];
		for( int i=0; i<count; ++i ) {
			scaledX[i] = inX[i]/hScale;
			scaledY[i] = inY[i]/hScale;
			scaledZ[i] = inZ[i]/hScale;
		}
		next.apply(count, scaledX, scaledY, scaledZ, out);
		for( int i=0; i<count; ++i ) out[i] *= vScale;
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
