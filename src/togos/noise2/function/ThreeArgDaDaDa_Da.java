package togos.noise2.function;

import java.lang.reflect.InvocationTargetException;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.rewrite.ExpressionRewriter;

public abstract class ThreeArgDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	public FunctionDaDaDa_Da inX, inY, inZ;
	
	public ThreeArgDaDaDa_Da( FunctionDaDaDa_Da inX, FunctionDaDaDa_Da inY, FunctionDaDaDa_Da inZ ) {
		this.inX = inX;
		this.inY = inY;
		this.inZ = inZ;
	}
	
	protected abstract String getMacroName();
	public abstract DataDa apply( DataDaDaDa in );
	
	public String toString() {
		return getMacroName()+"(" + inX + ", " + inY + ", " + inZ + ")";
	}
	
	public String toTnl() { 
		return getMacroName()+"(" +
			FunctionUtil.toTnl(inX) + ", " +
			FunctionUtil.toTnl(inY) + ", " +
			FunctionUtil.toTnl(inZ) +
		")"; 
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{ inX, inY, inZ };
    }
	
	public Object rewriteSubExpressions( ExpressionRewriter rw ) {
		try {
	        return this.getClass().getConstructor(new Class[]{
	        	FunctionDaDaDa_Da.class,
	        	FunctionDaDaDa_Da.class,
	        	FunctionDaDaDa_Da.class,
	        }).newInstance(new Object[]{
	        	rw.rewrite(inX), rw.rewrite(inY), rw.rewrite(inZ)
	        });
        } catch( IllegalArgumentException e ) {
        	throw new RuntimeException(e);
        } catch( SecurityException e ) {
        	throw new RuntimeException(e);
        } catch( InstantiationException e ) {
        	throw new RuntimeException(e);
        } catch( IllegalAccessException e ) {
        	throw new RuntimeException(e);
        } catch( InvocationTargetException e ) {
        	throw new RuntimeException(e);
        } catch( NoSuchMethodException e ) {
        	throw new RuntimeException(e);

        }
    }
	
	public boolean isConstant() { 
		return
			FunctionUtil.isConstant(inX) &&
			FunctionUtil.isConstant(inY) &&
			FunctionUtil.isConstant(inZ);
	}
}
