package togos.noise.v3.parser;

import togos.lang.BaseSourceLocation;

public class TokenizerSettings extends BaseSourceLocation
{
	public static final int DEFAULT_TAB_WIDTH = 8;
	
	public static TokenizerSettings forBuiltinFunctions( Class<?> definedIn ) {
		return new TokenizerSettings( definedIn.getName(), 0, 0, 4 );
	}
	
	public final int tabWidth;
	
	public TokenizerSettings( String filename, int line, int col, int tabWidth ) {
		super(filename, line, col);
		this.tabWidth = tabWidth;
	}
}
