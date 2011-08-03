package togos.noise2.lang;

import java.util.HashMap;
import java.util.Map;

import togos.noise2.rdf.TNLNamespace;
import togos.noise2.vm.rdf.TNLExpressionCompiler;

public class ArithmeticMacros
{
	public static final Map MACROS = new HashMap();
	
	static void addsm( String shortName, String longName, String[] args ) {
		MACROS.put( shortName, new TNLExpressionCompiler.SimpleArgListMacro( longName, args ) );
	}
	
	static {
		addsm(  "x", TNLNamespace.X_VAR, new String[0] );
		addsm(  "y", TNLNamespace.Y_VAR, new String[0] );
		addsm(  "z", TNLNamespace.Z_VAR, new String[0] );
		addsm(  "+", TNLNamespace.ADD,      TNLNamespace.ADD_ARGS      );
		addsm(  "-", TNLNamespace.SUBTRACT, TNLNamespace.SUBTRACT_ARGS );
		addsm(  "*", TNLNamespace.MULTIPLY, TNLNamespace.MULTIPLY_ARGS );
		addsm(  "/", TNLNamespace.DIVIDE,   TNLNamespace.DIVIDE_ARGS   );
		addsm( "**", TNLNamespace.EXPONENTIATE, TNLNamespace.EXPONENTIATE_ARGS );
		
		addsm( "sqrt", TNLNamespace.SQRT, TNLNamespace.MISC1D_ARGS );
		addsm( "sin",  TNLNamespace.SIN,  TNLNamespace.TRIG1D_ARGS );
		addsm( "cos",  TNLNamespace.COS,  TNLNamespace.TRIG1D_ARGS );
		addsm( "tan",  TNLNamespace.TAN,  TNLNamespace.TRIG1D_ARGS );
		addsm( "atan", TNLNamespace.ATAN, TNLNamespace.TRIG1D_ARGS );
	}
}
