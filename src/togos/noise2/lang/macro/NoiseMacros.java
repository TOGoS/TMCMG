package togos.noise2.lang.macro;

import java.util.HashMap;

import togos.noise2.function.AddOutDaDaDa_Da;
import togos.noise2.function.DivideOutDaDaDa_Da;
import togos.noise2.function.MultiplyOutDaDaDa_Da;
import togos.noise2.function.PerlinDaDaDa_Da;
import togos.noise2.function.SubtractOutDaDaDa_Da;

public class NoiseMacros
{
	public static HashMap standardBuiltinMacros = new HashMap();
	public static void add( String name, MacroType mt ) {
		standardBuiltinMacros.put(name,mt);
	}
	static MacroType dddaamt(Class functionClass) {
		return new DaDaDa_DaArrayArgMacroType(functionClass);
	}
	static {
		add("+", dddaamt(AddOutDaDaDa_Da.class));
		add("*", dddaamt(MultiplyOutDaDaDa_Da.class));
		add("-", dddaamt(SubtractOutDaDaDa_Da.class));
		add("/", dddaamt(DivideOutDaDaDa_Da.class));
		add("perlin", new ConstantMacroType(PerlinDaDaDa_Da.instance));
	}
}
