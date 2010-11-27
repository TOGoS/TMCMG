package togos.minecraft.mapgen.script;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptCompiler
{
	HashMap macroTypes = new HashMap();
	HashSet compilingMacroTypes = new HashSet();
	HashMap macroDefs = new HashMap();
	
	public void addMacroDef( String name, ScriptNode node ) {
		if( macroTypes.containsKey(name) ) {
			throw new CompileError("Attempt to redefine macro '"+name+"'", node);
		}
		macroDefs.put(name, node);
	}
	
	protected MacroType getMacroType( String name, SourceLocation sloc ) {
		if( !macroTypes.containsKey(name) ) {
			ScriptNode def = (ScriptNode)macroDefs.get(name);
			if( def == null ) {
				throw new CompileError("Reference to undefined macro "+name, sloc);
			}
			if( compilingMacroTypes.contains(name) ) {
				throw new CompileError("'" + name + "' macro definition is recursive", sloc);
			}
			compilingMacroTypes.add(name);
			try {
				macroTypes.put(name, compile(def));
			} finally {
				compilingMacroTypes.remove(name);
			}
		}
		return (MacroType)macroTypes.get(name);
	}
	
	Pattern hexIntPat = Pattern.compile("([+-])?0x([0-9a-fA-F]+)");
	Pattern intPat = Pattern.compile("[+-]?\\d+");
	Pattern floatPat = Pattern.compile("[+-]?\\d+(\\.\\d+)?([eE][+-]?\\d+)?");
	
	public Object compile( ScriptNode node ) {
		Matcher m;
		if( (m = hexIntPat.matcher(node.macroName)).matches() ) {
			return Integer.valueOf((m.group(1) == null ? "" : m.group(1))+m.group(2),16);
		} else if( (m = intPat.matcher(node.macroName)).matches() ) {
			return Integer.valueOf(node.macroName);
		} else if( (m = floatPat.matcher(node.macroName)).matches() ) {
			return Double.valueOf(node.macroName);
		} else {
			MacroType mt = getMacroType(node.macroName, node);
			return mt.instantiate(this, node);
		}
	}
	
	public Object compile( String source ) {
		ScriptParser parser = new ScriptParser(new StringReader(source));
		try {
			ScriptNode sn = parser.readNode(ScriptParser.COMMA_PRECEDENCE);
			return compile(sn);
		} catch( IOException e ) {
			throw new CompileError(e, new Token("","(inline)",1,1));
		}
	}
}
