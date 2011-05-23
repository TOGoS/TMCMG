package togos.noise2.lang.macro;

import java.util.HashMap;
import java.util.Map;

import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.TNLCompiler;

class UserMacroType implements MacroType {
	String name;
	String[] argNames;
	ASTNode value;
	
	public UserMacroType( String name, String[] argNames, ASTNode value ) {
		this.name = name;
		this.argNames = argNames;
		this.value = value;
	}
	
	public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError {
		if( sn.arguments.size() != argNames.length ) {
			throw new CompileError("Macro '"+name+"' requires "+argNames.length+
				" arguments, but was given "+sn.arguments.size(), sn);
		}
		Map arguments = new HashMap();
		for( int pi=0; pi<argNames.length; ++pi ) {
			String paramName = argNames[pi];
			if( c.getMacroType(paramName) != null ) {
				throw new CompileError("Argument to '"+name+"' would override '"+paramName+"'", sn);
			}
			MacroType argValue = new UserMacroType(paramName, new String[0], (ASTNode)sn.arguments.get(pi));
			arguments.put(paramName, argValue);
		}
		return c.withMacroTypes(arguments).compile(value);
	}
}
