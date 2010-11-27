package togos.minecraft.mapgen.script;

public class ConstantMacroType implements MacroType
{
	protected Object value;
	public ConstantMacroType( Object value ) {
		this.value = value;
	}
	public Object instantiate(ScriptCompiler c, ScriptNode sn) {
		return value;
	}
}
