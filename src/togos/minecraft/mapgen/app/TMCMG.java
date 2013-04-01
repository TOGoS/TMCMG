package togos.minecraft.mapgen.app;

import java.util.Arrays;

import togos.minecraft.mapgen.util.Util;
import togos.noise.v3.REPL;

public class TMCMG
{
	protected static final String USAGE =
		"Usage: TMCMG -<sub-command> <sub-command-specific args...>\n" +
		"\n" +
		"Sub-commands:\n" +
		"  -?, -h, -help, etc   ; show this help text\n" +
		"  -world-designer-gui  ; run the world designer GUI\n" +
		"  -tnl                 ; run stand-alone TNL scripts\n" +
		"\n" +
		"For help with a sub-command, run: TMCMG -<sub-command> -?\n" +
		"Called with no arguments, starts the world designer GUI.";
	
	public static void main( String[] args ) throws Exception {
		if( args.length == 0 ) {
			WorldDesigner.main( args );
			return;
		}
		for( int i=0; i<args.length; ++i ) {
			if( Util.isHelpArgument(args[i]) ) {
				System.out.println(USAGE);
				return;
			} else if( "-world-designer-gui".equals(args[i]) ) {
				WorldDesigner.main( Arrays.copyOf(args, i) );
			} else if( "-tnl-repl".equals(args[i]) ) {
				REPL.main( Arrays.copyOf(args, i) );
			} else {
				System.err.println("Unrecognised argument: "+args[i]);
				System.err.println();
				System.err.println(USAGE);
				System.exit(1);
			}
		}
	}
}
