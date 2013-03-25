package togos.noise.v1.lang;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import togos.lang.CompileError;
import togos.lang.ParseError;
import togos.lang.ScriptError;
import togos.lang.SourceLocation;
import togos.noise.Compiler;
import togos.noise.v1.data.DataDa;
import togos.noise.v1.data.DataDaDaDa;
import togos.noise.v1.func.FunctionDaDaDa_Da;
import togos.noise.v1.func.LFunctionDaDaDa_Da;
import togos.noise.v1.lang.macro.MacroType;

public class TNLCompiler implements Compiler
{
	public Map<String,MacroType> macroTypes = new HashMap<String,MacroType>();
	
	public TNLCompiler() {
		initBuiltins();
	}
	
	protected void initBuiltins() {
		// Here so you can override it
	}
	
	public TNLCompiler( Map<String,MacroType> macroTypes ) {
		this.macroTypes = macroTypes;
	}
	
	public MacroType getMacroType( String name ) {
		return (MacroType)macroTypes.get(name);
	}
	
	Pattern hexIntPat = Pattern.compile("([+-])?0x([0-9a-fA-F]+)");
	Pattern intPat = Pattern.compile("[+-]?\\d+");
	Pattern floatPat = Pattern.compile("[+-]?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
	
	protected String sanitizeNumberString(String numStr) {
		if( numStr.startsWith("+") ) {
			numStr = numStr.substring(1);
		}
		return numStr;
	}
	
	public Object compile( ASTNode node ) throws CompileError {
		Matcher m;
		if( (m = hexIntPat.matcher(node.macroName)).matches() ) {
			return Integer.valueOf((m.group(1) == null ? "" : m.group(1))+m.group(2),16);
		} else if( (m = intPat.matcher(node.macroName)).matches() ) {
			return Integer.valueOf(sanitizeNumberString(node.macroName));
		} else if( (m = floatPat.matcher(node.macroName)).matches() ) {
			return Double.valueOf(sanitizeNumberString(node.macroName));
		} else if( node.macroName.startsWith("\"") ) {
			// Tokenizer's already unescaped things...
			return node.macroName.substring(1);
		} else {
			MacroType mt = (MacroType)macroTypes.get(node.macroName);
			if( mt == null ) {
				throw new CompileError("Undefined macro '"+node.macroName+"'", node);
			}
			return mt.instantiate(this, node);
		}
	}
	
	public Object compile( String source, String sourceName ) throws ParseError, CompileError {
		TNLParser parser = new TNLParser(new TNLTokenizer(new StringReader(source), sourceName, 1, 1));
		try {
			ASTNode sn = parser.readNode(0);
			return compile(sn);
		} catch( IOException e ) {
			throw new CompileError(e, new Token("","(inline)",1,1));
		}
	}

	public Object compile( String source ) throws ParseError, CompileError {
		return compile( source, "(unnamed)" );
	}

	public Object compile( String source, SourceLocation loc, String scriptId, Class<?> preferredType ) throws ScriptError {
		Object cv = compile( source, loc.getSourceFilename() );
		
		if( preferredType.isAssignableFrom(cv.getClass()) ) {
			return cv;
		}
		
		if( preferredType == LFunctionDaDaDa_Da.class ) {
			if( cv instanceof FunctionDaDaDa_Da ) {
				final FunctionDaDaDa_Da dddd = (FunctionDaDaDa_Da)cv;
				return new LFunctionDaDaDa_Da() {
					public void apply( int vectorSize, double[] x, double[] y, double[] z, double[] dest ) {
						DataDa d = dddd.apply( new DataDaDaDa(vectorSize,x,y,z) );
						for( int i=0; i<vectorSize; ++i ) {
							dest[i] = d.x[i];
						}
                    }
				};
			}
		}
		
		return cv;
    }
}
