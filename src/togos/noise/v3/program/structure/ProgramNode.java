package togos.noise.v3.program.structure;

import togos.lang.SourceLocation;

public abstract class ProgramNode
{
	public final SourceLocation sLoc;
	
	public ProgramNode( SourceLocation sLoc ) {
		this.sLoc = sLoc;
	}
}
