package togos.noise2.function;

import java.lang.reflect.InvocationTargetException;

import togos.noise2.data.DataDa;
import togos.noise2.data.DataDaDaDa;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.rewrite.ExpressionRewriter;

public abstract class OneArgDaDaDa_Da extends TNLFunctionDaDaDa_Da
{
	public FunctionDaDaDa_Da arg;
	
	public OneArgDaDaDa_Da( FunctionDaDaDa_Da arg) {
		this.arg = arg;
	}
	
	protected abstract String getMacroName();
	public abstract DataDa apply( DataDaDaDa in );
	
	public String toString() {
		return getMacroName()+"(" + arg + ")";
	}
	
	public String toTnl() { 
		return getMacroName()+"(" + FunctionUtil.toTnl(arg) + ")"; 
	}
	
	public Object[] directSubExpressions() {
		return new Object[]{ arg };
    }
	
	public Object rewriteSubExpressions( ExpressionRewriter rw ) {
		try {
	        return this.getClass().getConstructor(new Class[]{FunctionDaDaDa_Da.class}).newInstance(new Object[]{rw.rewrite(arg)});
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
		return FunctionUtil.isConstant(arg); 
	}
}
