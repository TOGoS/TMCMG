package togos.noise2.vm.vops;

import java.io.IOException;

import togos.noise2.rdf.TNLNamespace;
import togos.noise2.vm.OpWriter;

public class VKOpWriter implements OpWriter
{
	int varNum = 1;
	Appendable w;
	
	protected void println(String line) {
		try {
			w.append(line+"\n");
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public String declareVar(String type, String nameSuggestion) {
		String name = "var"+(varNum++);
		println("var "+type+" "+name);
		return name;
	}
	
	public VKOpWriter( Appendable w ) {
		this.w = w;
	}
	
	public String declareInput( String type, String nameSuggestion ) {
		return declareVar(type, nameSuggestion);
	}
	
	public String writeConstant( String dest, double value ) {
		return Double.toString(value);
	}
	
	protected String _writeOp( String dest, String symbol, String[] operands ) {
		String result = operands[0];
		for( int i=1; i<operands.length; ++i ) {
			String arg0 = result;
			if( i==operands.length-1 && dest != null ) {
				result = dest;
			} else {
				result = declareVar("double", "temp");
			}
			println( result + " = " + arg0 + " " + symbol + " " + operands[i] );
		}
		return result;
	}
	
	protected String _writePrefixOp( String dest, String opName, String[] operands ) {
		String operandStr = "";
		if( dest == null ) {
			dest = declareVar("double","temp");
		}
		for( int i=0; i<operands.length; ++i ) {
			operandStr += " "+operands[i];
		}
		println( dest + " = <" + opName + ">" + operandStr );
		return dest;
	}
	
	public String writeOp( String dest, String op, String[] operands ) {
		if( TNLNamespace.ADD.equals(op) ) {
			return _writeOp( dest, "+", operands );
		} else if( TNLNamespace.SUBTRACT.equals(op) ) {
			return _writeOp( dest, "-", operands );
		} else if( TNLNamespace.MULTIPLY.equals(op) ) {
			return _writeOp( dest, "*", operands );
		} else if( TNLNamespace.DIVIDE.equals(op) ) {
			return _writeOp( dest, "/", operands );
		} else if( TNLNamespace.EXPONENTIATE.equals(op) ) {
			return _writeOp( dest, "**", operands );
		} else {
			return _writePrefixOp( dest, op, operands );
		}
	}
}
