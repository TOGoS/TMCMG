package togos.minecraft.mapgen;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.SourceLocation;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.lang.TNLParser;
import togos.noise2.lang.TNLTokenizer;

public class ScriptUtil
{
	public static String formatCompileError( CompileError e ) {
		SourceLocation sloc = e.sourceLocation;
		return "Compile error: "+e.getMessage() + "\n" +
			"At "+sloc.getSourceFilename()+":"+sloc.getSourceLineNumber()+","+sloc.getSourceColumnNumber();
	}
	
	public static Object compileOrExit( TNLCompiler c, ASTNode n ) {
		try {
			return c.compile(n);
		} catch( CompileError e ) {
			System.err.println(formatCompileError(e));
			System.exit(1);
			return null;
		}
	}
	
	public static Object compile( TNLCompiler c, Reader r, String sourceFilename, int sourceLineNumber ) throws IOException {
		return c.compile( new TNLParser(new TNLTokenizer(r, sourceFilename, sourceLineNumber, 1)).readNode(0) );		
	}
	
	public static Object compile( TNLCompiler c, String source, String sourceFilename, int sourceLineNumber ) {
		try {
			return compile( c, new StringReader(source), sourceFilename, sourceLineNumber );
		} catch( IOException e ) {
			throw new RuntimeException(e); // shouldn't happen
		}
	}
	
	public static Object compile( TNLCompiler c, File f ) throws IOException {
		Reader r = new FileReader(f);
		try {
			return compile( c, r, f.getName(), 1 );
		} finally {
			r.close();
		}
	}
}
