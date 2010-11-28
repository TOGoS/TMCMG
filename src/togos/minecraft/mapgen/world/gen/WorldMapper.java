package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.world.Blocks;
import togos.noise2.function.AdaptInDaDa_DaDaDa_Da;
import togos.noise2.function.AddOutDaDaDa_Da;
import togos.noise2.function.Constant_Da;
import togos.noise2.function.Constant_Ia;
import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.function.FunctionDaDa_Ia;
import togos.noise2.function.MultiplyOutDaDaDa_Da;
import togos.noise2.function.PerlinDaDaDa_Da;
import togos.noise2.function.ScaleInDaDaDa_Da;
import togos.noise2.function.ScaleOutDaDaDa_Da;
import togos.noise2.function.TerrainScaleDaDaDa_Da;

public class WorldMapper
{
	public FunctionDaDa_Ia colorFunction;
	public ChunkMunger chunkMunger;
	
	public static WorldMapper DEFAULT;
	static {
		PerlinDaDaDa_Da perlin = new PerlinDaDaDa_Da();
		AddOutDaDaDa_Da sandLevel = new AddOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
			new Constant_Da(56),
			new TerrainScaleDaDaDa_Da( perlin, 16384, 16 ),
			new TerrainScaleDaDaDa_Da( perlin,  8192, 16 ),
			new TerrainScaleDaDaDa_Da( perlin,  4096, 16 ),
			new TerrainScaleDaDaDa_Da( perlin,   512, 12 ),
		});
		AddOutDaDaDa_Da dirtLevel = new AddOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
			sandLevel,
			new TerrainScaleDaDaDa_Da( perlin,  2048, 32  ),
			new TerrainScaleDaDaDa_Da( perlin,  1024, 12 ),
			new TerrainScaleDaDaDa_Da( perlin,   256,  8 ),
			new TerrainScaleDaDaDa_Da( perlin,    64,  8 ),
			new TerrainScaleDaDaDa_Da( perlin,    32,  8 ),
			new TerrainScaleDaDaDa_Da( perlin,    16,  8 ),
			new TerrainScaleDaDaDa_Da( perlin,     4,  2 ),
		});
		AddOutDaDaDa_Da stoneLevel = new AddOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
			dirtLevel,
			new Constant_Da(-4),
			new TerrainScaleDaDaDa_Da( perlin,  128,  8 ),
			new MultiplyOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
				new TerrainScaleDaDaDa_Da( perlin, 1024,  2 ),
				new TerrainScaleDaDaDa_Da( perlin,    8,  8 ),
			}),
		});
		
		LayerMapper lm = new LayerMapper();
		lm.layers.add( new LayerMapper.Layer(
			new Constant_Ia(Blocks.WATER),
			new Constant_Da(32),
			new Constant_Da(64)
		));
		lm.layers.add( new LayerMapper.Layer(
			new Constant_Ia(Blocks.SAND),
			new Constant_Da(-10),
			new AdaptInDaDa_DaDaDa_Da(sandLevel)
		));
		lm.layers.add( new LayerMapper.Layer(
			new Constant_Ia(Blocks.DIRT),
			new Constant_Da(0),
			new AdaptInDaDa_DaDaDa_Da(dirtLevel)
		));
		lm.layers.add( new LayerMapper.Layer(
			new Constant_Ia(Blocks.STONE),
			new Constant_Da(1),
			new AdaptInDaDa_DaDaDa_Da(stoneLevel)
		));
		lm.layers.add( new LayerMapper.Layer(
			new Constant_Ia(Blocks.BEDROCK),
			new Constant_Da(0),
			new Constant_Da(1)
		));
		
		ChunkMungeList cmList = new ChunkMungeList(); 
		cmList.addMunger( lm.getLayerChunkMunger() );
		cmList.addMunger( new Grassifier() );
		GroundStampPopulator tsp = new GroundStampPopulator(
			new TreeGenerator(), 20,
			new AdaptInDaDa_DaDaDa_Da(new ScaleOutDaDaDa_Da(1d/16, new ScaleInDaDaDa_Da(1d/64, 1d/64, 1d/64, new PerlinDaDaDa_Da()))),
			lm.getGroundFunction(), new int[]{Blocks.DIRT, Blocks.GRASS}
		);
		cmList.addMunger( new StampPopulatorChunkMunger(tsp) );
		
		DEFAULT = new WorldMapper();
		DEFAULT.chunkMunger = cmList; 
		DEFAULT.colorFunction = lm.getLayerColorFunction();
	}
	
	public WorldMapper( ChunkMunger chunkMunger, FunctionDaDa_Ia colorFunction ) {
		this.chunkMunger = chunkMunger;
		this.colorFunction = colorFunction;
	}
	public WorldMapper() {
		this( new ChunkMungeList(), new Constant_Ia(0) );
	}
	
	public FunctionDaDa_Ia getLayerColorFunction() {
		return colorFunction;
	}
	
	public ChunkMunger getChunkMunger() {
		return chunkMunger;
	}
}
