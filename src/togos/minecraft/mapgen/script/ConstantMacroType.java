package togos.minecraft.mapgen.script;

public class ConstantMacroType implements MacroType
{
	protected Object value;
	public ConstantMacroType( Object value ) {
		this.value = value;
	}
	public Object instantiate(ScriptCompiler c, ScriptNode sn) {
		if( sn.arguments.size() > 0 ) {
			throw new CompileError(sn.macroName + " takes no arguments, "+sn.arguments.size()+" given", sn);
		}
		return value;
	}
}
