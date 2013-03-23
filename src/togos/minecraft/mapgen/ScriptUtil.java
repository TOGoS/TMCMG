package togos.minecraft.mapgen;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import togos.minecraft.mapgen.util.Script;
import togos.noise.v1.lang.ASTNode;
import togos.noise.v1.lang.CompileError;
import togos.noise.v1.lang.ParseError;
import togos.noise.v1.lang.ParseUtil;
import togos.noise.v1.lang.TNLCompiler;
import togos.noise.v1.lang.TNLParser;
import togos.noise.v1.lang.TNLTokenizer;

public class ScriptUtil
{
	public static Object compileOrExit( TNLCompiler c, ASTNode n ) {
		try {
			return c.compile(n);
		} catch( CompileError e ) {
			System.err.println(ParseUtil.formatScriptError(e));
			System.exit(1);
			return null;
		}
	}
	
	public static Object compile( TNLCompiler c, Reader r, String sourceFilename, int sourceLineNumber ) throws IOException, ParseError, CompileError {
		return c.compile( new TNLParser(new TNLTokenizer(r, sourceFilename, sourceLineNumber, 1)).readNode(0) );
	}
	
	public static Object compile( TNLCompiler c, String source, String sourceFilename, int sourceLineNumber )
		throws ParseError, CompileError
	{
		try {
			return compile( c, new StringReader(source), sourceFilename, sourceLineNumber );
		} catch( IOException e ) {
			throw new RuntimeException(e); // shouldn't happen
		}
	}
	
	public static Object compile( TNLCompiler c, File f ) throws IOException, ParseError, CompileError {
		Reader r = new FileReader(f);
		try {
			return compile( c, r, f.getName(), 1 );
		} finally {
			r.close();
		}
	}

	public static Object compile( TNLCompiler c, Script s ) throws IOException, ParseError, CompileError {
		Reader r = new InputStreamReader(new ByteArrayInputStream(s.source));
		try {
			return compile( c, r, s.sourceFilename, 1 );
		} finally {
			r.close();
		}
	}
}
