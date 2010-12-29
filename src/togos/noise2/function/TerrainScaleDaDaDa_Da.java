package togos.noise2.function;

import togos.noise2.lang.FunctionUtil;


public class TerrainScaleDaDaDa_Da implements SmartFunctionDaDaDa_Da
{
	SmartFunctionDaDaDa_Da next;
	double hScale, vScale;
	public TerrainScaleDaDaDa_Da( SmartFunctionDaDaDa_Da next, double hScale, double vScale ) {
		this.next = next;
		this.hScale = hScale;
		this.vScale = vScale;
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
	
	public SmartFunctionDaDaDa_Da simplify() {
		return FunctionUtil.collapseIfConstant(this);
	}
}
