package togos.noise2.vm.javac;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import togos.mf.base.Util;

public class JavaCompiler
{
	public String javac;
	public String destClassRoot;
	public String[] classRoot;
	public String sourceRoot;
	public String cpSeparator = ";";
	
	public String findJavac() {
		String home = System.getProperty("java.home");
		if( new File( home+"/bin/javac" ).exists() ) {
			return home+"/bin/javac";
		}
		if( new File( home+"/../bin/javac" ).exists() ) {
			return home+"/../bin/javac";
		}
		if( new File( home+"/bin/javac.exe" ).exists() ) {
			return home+"/bin/javac.exe";
		}
		if( new File( home+"/../bin/javac.exe" ).exists() ) {
			return home+"/../bin/javac.exe";
		}
		return null;
	}
	
	public void compile( String classname, String source ) throws IOException {
		String relJavaPath = classname.replace('.', '/') + ".java";
		File sourceFile = new File( sourceRoot + "/" + relJavaPath );
		
		File sourceDir = sourceFile.getParentFile();
		if( !sourceDir.exists() ) sourceDir.mkdirs();

		FileWriter w = new FileWriter(sourceFile);
		w.write(source);
		w.close();
		
		File destClassRootFile = new File(destClassRoot);
		if( !destClassRootFile.exists() ) destClassRootFile.mkdirs();
		
		String cp = "";
		for( int i=0; i<classRoot.length; ++i ) {
			if( cp != "" ) cp += ";";
			cp += classRoot[i];
		}
		String[] args = new String[]{ javac, "-cp", cp, "-sourcepath", sourceRoot, "-d", destClassRoot, sourceRoot + "/" + relJavaPath };
		Process javacProc = Runtime.getRuntime().exec(args);
		try {
			InputStream err = javacProc.getErrorStream();
			byte[] errBuf = new byte[2048];
			int bo = 0;
			int r;
			while( (r = err.read(errBuf, bo, errBuf.length - bo)) > 0 ) {
				bo += r;
			}
			int status = javacProc.waitFor();
			if( status != 0 ) {
				throw new RuntimeException("Failed to compile "+classname+":\n"+Util.string(errBuf, 0, bo));
			}
		} catch( InterruptedException e ) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupted while waiting for javac to finish");
		}
		
	}
	
	public boolean classExists( String classname ) {
		String relClassPath = classname.replace('.', '/') + ".class";
		
		for( int i=0; i<classRoot.length; ++i ) {
			if( new File( classRoot[i] + "/" + relClassPath ).exists() ) return true;
		}
		
		return false;
	}
}
