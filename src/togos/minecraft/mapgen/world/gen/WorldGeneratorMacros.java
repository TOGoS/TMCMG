package togos.minecraft.mapgen.world.gen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import togos.lang.SourceLocation;
import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.Material;
import togos.noise2.cache.SoftCache;
import togos.noise2.lang.CompileError;
import togos.noise2.rewrite.CacheRewriter;
import togos.noise2.rewrite.ConstantFolder;
import togos.noise2.vm.dftree.func.Constant_Ia;
import togos.noise2.vm.dftree.func.FunctionDaDaDa_Da;
import togos.noise2.vm.dftree.func.FunctionDaDaDa_Ia;
import togos.noise2.vm.dftree.func.FunctionDaDa_Da;
import togos.noise2.vm.dftree.lang.ASTNode;
import togos.noise2.vm.dftree.lang.FunctionUtil;
import togos.noise2.vm.dftree.lang.TNLCompiler;
import togos.noise2.vm.dftree.lang.macro.BaseMacroType;
import togos.noise2.vm.dftree.lang.macro.ConstantMacroType;
import togos.noise2.vm.dftree.lang.macro.MacroType;

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
				return new HeightmapLayer(
					FunctionUtil.toDaDaDa_Ia(c.compile((ASTNode)sn.arguments.get(0)), sn),
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
		/*
		wgMacros.put("tree-types.custom", new ConstantMacroType(
				new CustomTreeGenerator()));
				*/
		wgMacros.put("pillar-generator", new BaseMacroType() {
			protected int getRequiredArgCount() {
	            return -1;
            }
			protected Material material( Object from, SourceLocation sl ) throws CompileError {
				if( from instanceof FunctionDaDaDa_Ia ) {
					return MaterialDaDaDa_Ia.intToMaterial(
						FunctionUtil.getValue( (FunctionDaDaDa_Ia)from, 0, 0, 0 )
					);
				}
				if( from instanceof FunctionDaDaDa_Da ) {
					return MaterialDaDaDa_Ia.intToMaterial(
						FunctionUtil.getValue( (FunctionDaDaDa_Ia)from, 0, 0, 0 )
					);
				}
				if( from instanceof Integer ) return MaterialDaDaDa_Ia.intToMaterial( ((Integer)from).intValue() );
				if( from instanceof Material ) return (Material)from;
				throw new CompileError("Can't convert "+from.getClass()+" to Material", sl);
			}
			protected Object instantiate( ASTNode node, ASTNode[] argNodes, Object[] compiledArgs ) throws CompileError {
				PillarGenerator pg = new PillarGenerator();
				if( compiledArgs.length >= 1 ) {
					pg.mat = material(compiledArgs[0],argNodes[0]);
				}
				if( compiledArgs.length >= 4 ) {
					pg.protoWidth  = FunctionUtil.toDouble( compiledArgs[1], argNodes[1] );
					pg.protoHeight = FunctionUtil.toDouble( compiledArgs[2], argNodes[2] );
					pg.protoDepth  = FunctionUtil.toDouble( compiledArgs[3], argNodes[3] );
				}
				if( compiledArgs.length >= 6 ) {
					pg.minScale = FunctionUtil.toDouble( compiledArgs[3], argNodes[3] );
					pg.maxScale = FunctionUtil.toDouble( compiledArgs[4], argNodes[4] );
				}
				if( compiledArgs.length >= 7 ) {
					pg.buriedness = FunctionUtil.toInt( compiledArgs[6], argNodes[6] );
				}
	            return pg;
            }
		});
		wgMacros.put("material", new BaseMacroType() {
			protected int getRequiredArgCount() {
	            return -1;
            }
			protected Object instantiate( ASTNode node, ASTNode[] argNodes, Object[] compiledArgs ) throws CompileError {
				FunctionDaDaDa_Ia blockType;
				FunctionDaDaDa_Ia extraBits = Constant_Ia.ZERO;
				if( compiledArgs.length >= 1 ) {
					blockType = FunctionUtil.toDaDaDa_Ia(compiledArgs[0], argNodes[0]);
				} else {
					throw new CompileError("material requires at least one argument", node);
				}
				if( compiledArgs.length >= 2 ) {
					extraBits = FunctionUtil.toDaDaDa_Ia(compiledArgs[1], argNodes[1]);
				}
	            return new MaterialDaDaDa_Ia(blockType,extraBits);
            }
		});
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
					if( node instanceof HeightmapLayer ) {
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
					HeightmapLayer layer = (HeightmapLayer)li.next();
					
					layer.floorHeightFunction   = (FunctionDaDa_Da)cf.rewrite( layer.floorHeightFunction );
					layer.ceilingHeightFunction = (FunctionDaDa_Da)cf.rewrite( layer.ceilingHeightFunction );
					layer.typeFunction        = (FunctionDaDaDa_Ia)cf.rewrite( layer.typeFunction );
					
					crw.initCounts( layer.floorHeightFunction );
					crw.initCounts( layer.ceilingHeightFunction );
					crw.initCounts( layer.typeFunction );
				}
				
				//crw.dumpCounts(System.out);
				
				for( Iterator li=lm.layers.iterator(); li.hasNext(); ) {
					HeightmapLayer layer = (HeightmapLayer)li.next();
					//System.err.println("   "+layer.floorHeightFunction.toString());
					//System.err.println("   "+layer.ceilingHeightFunction.toString());
					//System.err.println("   "+layer.typeFunction.toString());

					layer.floorHeightFunction   = (FunctionDaDa_Da)crw.rewrite( layer.floorHeightFunction );
					layer.ceilingHeightFunction = (FunctionDaDa_Da)crw.rewrite( layer.ceilingHeightFunction );
					layer.typeFunction        = (FunctionDaDaDa_Ia)crw.rewrite( layer.typeFunction );
					
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
