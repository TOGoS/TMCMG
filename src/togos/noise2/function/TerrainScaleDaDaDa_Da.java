package togos.noise2.function;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.rewrite.ExpressionRewriter;


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
		double[] scaledX = new double[in.getLength()];
		double[] scaledY = new double[in.getLength()];
		double[] scaledZ = new double[in.getLength()];
		double[] out = new double[in.getLength()];
		for( int i=in.getLength()-1; i>=0; --i ) {
			scaledX[i] = in.x[i]/hScale;
			scaledY[i] = in.y[i]/hScale;
			scaledZ[i] = in.z[i]/hScale;
		}
		double[] subOut = next.apply(new DataDaDaDa(scaledX,scaledY,scaledZ)).x;
		for( int i=in.getLength()-1; i>=0; --i ) out[i] = subOut[i] * vScale;
		return new DataDa(subOut);
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
