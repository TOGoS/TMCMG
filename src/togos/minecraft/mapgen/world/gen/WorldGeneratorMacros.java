package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import togos.minecraft.mapgen.world.Blocks;
import togos.noise2.cache.SoftCache;
import togos.noise2.function.FunctionDaDa_Da;
import togos.noise2.function.FunctionDaDa_Ia;
import togos.noise2.lang.ASTNode;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.FunctionUtil;
import togos.noise2.lang.TNLCompiler;
import togos.noise2.lang.macro.BaseMacroType;
import togos.noise2.lang.macro.ConstantMacroType;
import togos.noise2.lang.macro.MacroType;
import togos.noise2.rewrite.CacheRewriter;
import togos.noise2.rewrite.ConstantFolder;

public class WorldGeneratorMacros
{
	public static class ComponentDef
	{
		public String name;
		public Object value;

		public ComponentDef( String name, Object value ) {
			this.name = name;
			this.value = value;
		}
	}

	static HashMap wgMacros = new HashMap();
	static {
		wgMacros.put("layer", new MacroType() {
			public Object instantiate( TNLCompiler c, ASTNode sn )
					throws CompileError {
				if( sn.arguments.size() != 3 ) {
					throw new CompileError(
							sn.macroName
									+ " requires 3 arguments for type, floor, ceiling; given "
									+ sn.arguments.size(), sn);
				}
				return new LayerTerrainGenerator.Layer(
					FunctionUtil.toDaDa_Ia(c.compile((ASTNode)sn.arguments.get(0)), sn),
					FunctionUtil.toDaDa_Da(c.compile((ASTNode)sn.arguments.get(1)), sn),
					FunctionUtil.toDaDa_Da(c.compile((ASTNode)sn.arguments.get(2)), sn)
				);
			}
		});
		wgMacros.put("component", new BaseMacroType() {
			protected int getRequiredArgCount() {
				return 2;
			}

			protected Object instantiate( ASTNode node, ASTNode[] argNodes,
					Object[] compiledArgs ) {
				return new ComponentDef(compiledArgs[0].toString(),	compiledArgs[1]);
			}
		});
		wgMacros.put("grassifier", new ConstantMacroType(new Grassifier()));
		wgMacros.put("winterizer", new BaseMacroType() {
			protected int getRequiredArgCount() {
				return 1;
			}

			protected Object instantiate( ASTNode node, ASTNode[] argNodes,
					Object[] compiledArgs ) throws CompileError {
				return new Winterizer(FunctionUtil.toDaDa_Da(compiledArgs[0], argNodes[0]));
			}
		});
		wgMacros.put("lighter", new ConstantMacroType(new Lighter()));
		wgMacros.put("flag-populated", new ConstantMacroType(
				new TerrainPopulatedSetter()));
		wgMacros.put("tree-types.round", new ConstantMacroType(
				new RoundTreeGenerator()));
		wgMacros.put("tree-types.pine", new ConstantMacroType(
				new PineTreeGenerator()));
		wgMacros.put("tree-populator", new BaseMacroType() {
			protected int getRequiredArgCount() {
				return -1;
			}

			protected Object instantiate( ASTNode node, ASTNode[] argNodes,
					Object[] compiledArgs ) throws CompileError {
				int placementSeed;
				if( compiledArgs.length == 2 ) {
					placementSeed = 0;
				} else if( compiledArgs.length == 3 ) {
					placementSeed = FunctionUtil.toInt(compiledArgs[2],
							argNodes[2]);
				} else {
					throw new CompileError(node.macroName
							+ " requires 2 or 3 arguments", node);
				}
				if( !(compiledArgs[0] instanceof StampGenerator) ) {
					throw new CompileError("First argument to "
							+ node.macroName
							+ " should be a StampGenerator, but given "
							+ compiledArgs[0].getClass(), node);
				}
				StampGenerator stampGenerator = (StampGenerator) compiledArgs[0];
				FunctionDaDa_Da density = FunctionUtil.toDaDa_Da(
						compiledArgs[1], argNodes[1]);
				GroundStampPopulator gsp = new GroundStampPopulator(
						stampGenerator, 20, density, 4, null, new int[] {
								Blocks.DIRT, Blocks.GRASS });
				gsp.placementSeed = placementSeed;
				return gsp;
			}
		});
		wgMacros.put("layered-terrain", new MacroType() {
			public Object instantiate( TNLCompiler c, ASTNode sn ) throws CompileError {
				ConstantFolder cf = ConstantFolder.instance;
				CacheRewriter crw = new CacheRewriter(SoftCache.getInstance());

				ArrayList chunkMungers = new ArrayList();
				LayerTerrainGenerator lm = new LayerTerrainGenerator();
				HashMap components = new HashMap();
				for( Iterator i = sn.arguments.iterator(); i.hasNext(); ) {
					ASTNode argNode = (ASTNode) i.next();
					Object node = c.compile(argNode);
					if( node instanceof LayerTerrainGenerator.Layer ) {
						lm.layers.add(node);
						continue;
					}
					if( node instanceof ComponentDef ) {
						components.put(((ComponentDef) node).name,
								((ComponentDef) node).value);
						continue;
					}
					if( node instanceof StampPopulator ) {
						if( node instanceof GroundStampPopulator ) {
							GroundStampPopulator gsp = (GroundStampPopulator) node;
							gsp.groundFunction = lm.getGroundFunction();
						}
						
						node = new StampPopulatorChunkMunger(
								(StampPopulator) node);
					}
					if( node instanceof ChunkMunger ) {
						chunkMungers.add(node);
						continue;
					}
					throw new CompileError("Don't know how to incorporate "
							+ node.getClass() + " into world generator",
							argNode);
				}
				
				for( Iterator li=lm.layers.iterator(); li.hasNext(); ) {
					LayerTerrainGenerator.Layer layer = (LayerTerrainGenerator.Layer)li.next();
					
					layer.floorHeightFunction   = (FunctionDaDa_Da)cf.rewrite( layer.floorHeightFunction );
					layer.ceilingHeightFunction = (FunctionDaDa_Da)cf.rewrite( layer.ceilingHeightFunction );
					layer.typeFunction          = (FunctionDaDa_Ia)cf.rewrite( layer.typeFunction );
					
					crw.initCounts( layer.floorHeightFunction );
					crw.initCounts( layer.ceilingHeightFunction );
					crw.initCounts( layer.typeFunction );
				}
				
				//crw.dumpCounts(System.out);
				
				for( Iterator li=lm.layers.iterator(); li.hasNext(); ) {
					LayerTerrainGenerator.Layer layer = (LayerTerrainGenerator.Layer)li.next();
					//System.err.println("   "+layer.floorHeightFunction.toString());
					//System.err.println("   "+layer.ceilingHeightFunction.toString());
					//System.err.println("   "+layer.typeFunction.toString());

					layer.floorHeightFunction   = (FunctionDaDa_Da)crw.rewrite( layer.floorHeightFunction );
					layer.ceilingHeightFunction = (FunctionDaDa_Da)crw.rewrite( layer.ceilingHeightFunction );
					layer.typeFunction          = (FunctionDaDa_Ia)crw.rewrite( layer.typeFunction );
					
					//System.err.println("-> "+layer.floorHeightFunction.toString());
					//System.err.println("-> "+layer.ceilingHeightFunction.toString());
					//System.err.println("-> "+layer.typeFunction.toString());
				}
				
				//System.exit(0);
				
				chunkMungers.add(0, lm.getChunkMunger());
				return new SimpleWorldGenerator(
						new ChunkMungeList(chunkMungers), lm
								.getGroundFunction(), components);
			}
		});
	}
}
