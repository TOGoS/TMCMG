package togos.minecraft.mapgen;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;

import togos.lang.BaseSourceLocation;
import togos.lang.RuntimeError;
import togos.lang.SourceLocation;
import togos.minecraft.mapgen.world.gen.MinecraftWorldGenerator;
import togos.noise.v3.functions.MathFunctions;
import togos.noise.v3.parse.Parser;
import togos.noise.v3.parse.ProgramTreeBuilder;
import togos.noise.v3.parse.ast.ASTNode;
import togos.noise.v3.program.runtime.Context;
import togos.noise.v3.program.structure.Expression;

public class ScriptUtil
{
	static final Context STD_CONTEXT = new Context();
	static {
		STD_CONTEXT.putAll(MathFunctions.CONTEXT);
		STD_CONTEXT.putAll(GeneratorDefinitionFunctions.CONTEXT);
	}
	
	public static MinecraftWorldGenerator loadWorldGenerator( Reader scriptReader, SourceLocation sLoc ) throws Exception {
		ProgramTreeBuilder ptb = new ProgramTreeBuilder();
		
		ASTNode programAst = Parser.parse( scriptReader, sLoc );
		Expression<?> program = ptb.parseExpression(programAst);
		Object v = program.bind( STD_CONTEXT ).getValue();
		if( v instanceof MinecraftWorldGenerator ) {
			return (MinecraftWorldGenerator)v;
		} else {
			throw new RuntimeError( "Program did not return a world generator, but "+v.getClass(), program.sLoc);
		}
	}
	
	public static MinecraftWorldGenerator loadWorldGenerator( File scriptFile ) throws Exception {
		FileReader r = new FileReader(scriptFile);
		try {
			return loadWorldGenerator( r, new BaseSourceLocation(scriptFile.getPath(), 1, 1));
		} finally {
			r.close();
		}
	}
}
