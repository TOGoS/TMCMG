package togos.minecraft.mapgen.script;

import togos.noise2.function.FunctionDaDaDa_Da;

public class DaDaDa_DaArrayArgMacroType extends ArrayArgMacroType
{
	public DaDaDa_DaArrayArgMacroType( Class functionClass ) {
		super( functionClass, FunctionDaDaDa_Da.class );
	}
	
	protected Object compileArgument(ScriptCompiler c, ScriptNode sn) {
		Object r = c.compile(sn);
		return Functions.toDaDaDa_Da(r, sn);
	}
}
