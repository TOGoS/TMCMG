package togos.noise2.lang;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import togos.noise2.lang.macro.MacroType;

public class TNLCompiler
{
	public Map macroTypes = new HashMap();
	
	public TNLCompiler() {
		initBuiltins();
	}
	
	protected void initBuiltins() {
		// Here so you can override it
	}
	
	public TNLCompiler( Map macroTypes ) {
		this.macroTypes = macroTypes;
	}
	
	public TNLCompiler withMacroTypes( Map newMacroTypes ) {
		Map macroTypes = new HashMap(this.macroTypes);
		macroTypes.putAll(newMacroTypes);
		return new TNLCompiler(macroTypes);
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
}
