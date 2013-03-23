package togos.noise2.vm.vops;

import java.io.IOException;

import togos.noise2.vm.NameGenerator;

public class VKOpWriter
{
	int varNum = 1;
	Appendable w;
	final NameGenerator g = new NameGenerator();
	
	protected void println(String line) {
		try {
			w.append(line+"\n");
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
	
	public String declareVar(String type, String nameSuggestion) throws IOException {
		String name = g.nextVar(nameSuggestion);
		w.append("var:"+type+" "+name+"\n");
		return name;
	}
	
	public VKOpWriter( Appendable w ) {
		this.w = w;
	}
	
	public String writeConstant( String dest, double value ) {
		return Double.toString(value);
	}
	
	public void writeOp( String dest, String opName, String[] operands ) throws IOException {
		w.append( dest + " = " );
		if( operands.length == 1 ) {
			w.append( opName + " " + operands[0] );
		} else if( operands.length == 2 ) {
			w.append( operands[0] + " " + opName + " " + operands[1] );
		} else {
			throw new RuntimeException("Can't write operation with "+operands.length+" operands");
		}
		w.append("\n");
	}
}
