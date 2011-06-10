package togos.noise2.lang.macro;

import java.util.HashMap;
import java.util.Map;

import togos.minecraft.mapgen.ScriptUtil;
import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.TNLCompiler;

class CurryMacroType implements MacroType
{
	ASTNode oldNode;
	Map oldContext;
	
	public CurryMacroType( ASTNode oldNode, Map oldContext ) {
		this.oldNode = oldNode;
		this.oldContext = oldContext;
	}
	
	public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError {
		if( oldNode.arguments.size() > 0 && sn.arguments.size() > 0 ) {
			throw new CompileError("Due to compiler design limitations, can't add additional arguments to non-empty argument list to "+
				oldNode.macroName+" (original arguments given at "+ScriptUtil.formaLocation(oldNode)+")", sn);
		}
		if( sn.arguments.size() > 0 ) {
			MacroType mt = (MacroType)oldContext.get(oldNode.macroName);
			if( mt == null ) {
				throw new CompileError("Undefined curried macro '"+oldNode.macroName+"'", oldNode);
			}
			return mt.instantiate(c, sn);
		} else {
			return new TNLCompiler(oldContext).compile(oldNode);			
		}
	}
}

class UserMacroType implements MacroType {
	String name;
	String[] argNames;
	ASTNode value;
	Map valueContext;
	
	public UserMacroType( String name, String[] argNames, ASTNode value, Map valueContext ) {
		this.name = name;
		this.argNames = argNames;
		this.value = value;
		this.valueContext = valueContext;
	}
	
	public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError {
		if( sn.arguments.size() != argNames.length ) {
			throw new CompileError("Macro '"+name+"' requires "+argNames.length+
				" arguments, but was given "+sn.arguments.size(), sn);
		}
		Map innerContext = new HashMap(valueContext);
		for( int pi=0; pi<argNames.length; ++pi ) {
			String paramName = argNames[pi];
			if( valueContext.get(paramName) != null ) {
				throw new CompileError("Argument to '"+name+"' would override '"+paramName+"'", sn);
			}
			MacroType argValue = new CurryMacroType((ASTNode)sn.arguments.get(pi), c.macroTypes);
			innerContext.put(paramName, argValue);
		}
		return new TNLCompiler(innerContext).compile(value);
	}
}
