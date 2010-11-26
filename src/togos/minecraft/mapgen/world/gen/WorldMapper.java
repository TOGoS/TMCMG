package togos.minecraft.mapgen.world.gen;

import togos.minecraft.mapgen.noise.AdaptDaDa_DaDaDa_Da;
import togos.minecraft.mapgen.noise.AddOutDaDaDa_Da;
import togos.minecraft.mapgen.noise.Constant_Da;
import togos.minecraft.mapgen.noise.Constant_Ia;
import togos.minecraft.mapgen.noise.MultiplyOutDaDaDa_Da;
import togos.minecraft.mapgen.noise.PerlinDaDaDa_Da;
import togos.minecraft.mapgen.noise.TerrainScaleDaDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_Ia;

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
			Material.WATER,
			new Constant_Da(32),
			new Constant_Da(64)
		));
		lm.layers.add( new LayerMapper.Layer(
			Material.SAND,
			new Constant_Da(-10),
			new AdaptDaDa_DaDaDa_Da(sandLevel)
		));
		lm.layers.add( new LayerMapper.Layer(
			Material.DIRT,
			new Constant_Da(0),
			new AdaptDaDa_DaDaDa_Da(dirtLevel)
		));
		lm.layers.add( new LayerMapper.Layer(
			Material.STONE,
			new Constant_Da(1),
			new AdaptDaDa_DaDaDa_Da(stoneLevel)
		));
		lm.layers.add( new LayerMapper.Layer(
			Material.BEDROCK,
			new Constant_Da(0),
			new Constant_Da(1)
		));
		
		ChunkMungeList cmList = new ChunkMungeList(); 
		cmList.addMunger( lm.getLayerChunkMunger() );
		cmList.addMunger( new Grassifier() );
		TreeStampPopulator tsp = new TreeStampPopulator();
		tsp.groundFunction = lm.getGroundFunction();
		tsp.densityFunction = Constant_Da.forValue(1d/64);
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
