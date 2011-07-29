package togos.noise2.vm.vops;

import java.io.IOException;

import togos.noise2.lang.CompileError;
import togos.noise2.vm.dftree.func.LFunctionDaDaDa_Da;

public class VectorKernel implements LFunctionDaDaDa_Da
{
	protected String script;
	protected String filename;
	protected int maxVectorSize = 1024;
	String xVar, yVar, zVar, resVar;
	
	public VectorKernel( String script, String filename, String xVar, String yVar, String zVar, String resVar ) {
		this.script = script;
		this.filename = filename;
		this.xVar = xVar;
		this.yVar = yVar;
		this.zVar = zVar;
		this.resVar = resVar;
	}
	
	protected ThreadLocal stvk = new ThreadLocal() {
		protected Object initialValue() {
			try {
				STVKScriptCompiler compiler = new STVKScriptCompiler();
				return compiler.compile(script, filename, maxVectorSize);
			} catch( IOException e ) {
				throw new RuntimeException(e);
			} catch( CompileError e ) {
				throw new RuntimeException(e);
			}
		}
	};
	
	protected STVectorKernel getStvk() {
		return (STVectorKernel)stvk.get();
	}
	
	protected final static void copy( int count, double[] src, double[] dest ) {
		for( int i=0; i<count; ++i ) dest[i] = src[i];
	}
	
	public void apply( int vectorSize, double[] x, double[] y, double[] z, double[] dest ) {
		if( vectorSize > maxVectorSize ) {
			throw new RuntimeException("Vector size "+vectorSize+" too large! (>"+maxVectorSize+")");
		}
		STVectorKernel stvk = getStvk();
	    if( xVar != null ) copy( vectorSize, x, (double[])stvk.vars.get(xVar) );
	    if( yVar != null ) copy( vectorSize, y, (double[])stvk.vars.get(yVar) );
	    if( zVar != null ) copy( vectorSize, z, (double[])stvk.vars.get(zVar) );
	    stvk.invoke(vectorSize);
	    if( resVar != null ) copy( vectorSize, (double[])stvk.vars.get(resVar), dest );
	}
}
