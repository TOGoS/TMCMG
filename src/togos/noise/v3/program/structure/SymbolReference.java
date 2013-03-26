package togos.noise.v3.program.structure;

import togos.lang.SourceLocation;

public class SymbolReference extends ProgramNode
{
	final String symbol;
	
	public SymbolReference( String symbol, SourceLocation sLoc ) {
		super( sLoc );
		this.symbol = symbol;
	}
}
