package togos.noise.v1.func;

import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.rewrite.ExpressionRewriter;


public class TerrainScaleDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	TNLFunctionDaDaDa_Da next;
	double hScale, vScale;
	public TerrainScaleDaDaDa_Da( double hScale, double vScale, TNLFunctionDaDaDa_Da next ) {
		this.hScale = hScale;
		this.vScale = vScale;
		this.next = next;
	}
	
	public DataDa apply( DataDaDaDa in ) {
		final int vectorSize=in.getLength();
		double[] scaledX = new double[vectorSize];
		double[] scaledY = new double[vectorSize];
		double[] scaledZ = new double[vectorSize];
		double[] out = new double[vectorSize];
		for( int i=vectorSize-1; i>=0; --i ) {
			scaledX[i] = in.x[i]/hScale;
			scaledY[i] = in.y[i]/hScale;
			scaledZ[i] = in.z[i]/hScale;
		}
		double[] subOut = next.apply(new DataDaDaDa(vectorSize,scaledX,scaledY,scaledZ)).x;
		for( int i=in.getLength()-1; i>=0; --i ) out[i] = subOut[i] * vScale;
		return new DataDa(vectorSize,subOut);
	}
	
	public boolean isConstant() {
		return next.isConstant();
	}
	
	public Object rewriteSubExpressions(ExpressionRewriter rw) {
		return new TerrainScaleDaDaDa_Da(hScale, vScale, (TNLFunctionDaDaDa_Da)rw.rewrite(next));
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{};
	}
	
	public String toTnl() {
		return "terrain-scale("+hScale+", "+vScale+", "+next.toTnl()+")";
	}
}
