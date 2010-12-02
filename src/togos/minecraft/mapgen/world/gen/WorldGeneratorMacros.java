package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import togos.minecraft.mapgen.world.Blocks;
import togos.noise2.function.FunctionDaDa_Da;
import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.lang.macro.BaseMacroType;
import togos.noise2.lang.macro.ConstantMacroType;
import togos.noise2.lang.macro.MacroType;

public class WorldGeneratorMacros
{
	static HashMap wgMacros = new HashMap();
	static {
		wgMacros.put("layer", new MacroType() {
			public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError {
				if( sn.arguments.size() != 3 ) {
					throw new CompileError( sn.macroName + " requires 3 arguments for type, floor, ceiling; given "+sn.arguments.size(), sn );
				}
				return new LayerTerrainGenerator.Layer(
					FunctionUtil.toDaDa_Ia(c.compile((ASTNode)sn.arguments.get(0)), sn),
					FunctionUtil.toDaDa_Da(c.compile((ASTNode)sn.arguments.get(1)), sn),
					FunctionUtil.toDaDa_Da(c.compile((ASTNode)sn.arguments.get(2)), sn)
				);
			}
		});
		
		wgMacros.put("grassifier", new ConstantMacroType(new Grassifier()));
		wgMacros.put("lighter", new ConstantMacroType(new Lighter()));
		wgMacros.put("tree-types.round", new ConstantMacroType(new RoundTreeGenerator()));
		wgMacros.put("tree-types.pine", new ConstantMacroType(new PineTreeGenerator()));
		wgMacros.put("tree-populator", new BaseMacroType() {
			protected int getRequiredArgCount() {  return 2;  }
			
			protected Object instantiate( ASTNode node, ASTNode[] argNodes,
			        Object[] compiledArgs ) throws CompileError {
				if( !(compiledArgs[0] instanceof StampGenerator) ) {
					throw new CompileError("First argument to "+node.macroName+
						" should be a StampGenerator, but given "+compiledArgs[0].getClass(), node);
				}
				StampGenerator stampGenerator = (StampGenerator)compiledArgs[0];
				FunctionDaDa_Da density = FunctionUtil.toDaDa_Da(compiledArgs[1], argNodes[1]);
				return new GroundStampPopulator( stampGenerator, 20, density, 4, null, new int[]{
					Blocks.DIRT, Blocks.GRASS
				} );
			}
		});
		wgMacros.put("layered-terrain", new MacroType() {
			public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError {
				ArrayList chunkMungers = new ArrayList();
				LayerTerrainGenerator lm = new LayerTerrainGenerator();
				for( Iterator i=sn.arguments.iterator(); i.hasNext(); ) {
					ASTNode argNode = (ASTNode)i.next();
					Object node = c.compile(argNode);
					if( node instanceof LayerTerrainGenerator.Layer ) {
						lm.layers.add( node );
						continue;
					}
					if( node instanceof StampPopulator ) {
						if( node instanceof GroundStampPopulator ) {
							GroundStampPopulator gsp = (GroundStampPopulator)node;
							gsp.groundFunction = lm.getGroundFunction();
						}
						
						node = new StampPopulatorChunkMunger((StampPopulator)node);
					}
					if( node instanceof ChunkMunger ) {
						chunkMungers.add(node);
						continue;
					}
					throw new CompileError( "Don't know how to incorporate "+node.getClass()+" into world generator", argNode );
				}
				
				chunkMungers.add(0,lm.getChunkMunger());
				return new SimpleWorldGenerator( new ChunkMungeList(chunkMungers), lm.getGroundFunction() );
			}
		});
	}
}
