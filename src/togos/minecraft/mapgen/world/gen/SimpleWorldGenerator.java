package togos.minecraft.mapgen.world.gen;

import java.util.Map;

import togos.minecraft.mapgen.MaterialColumnFunction;
import togos.minecraft.mapgen.world.Blocks;
import togos.noise.v1.func.AdaptInXZDaDaDa_DaDa_Da;
import togos.noise.v1.func.AddOutDaDaDa_Da;
import togos.noise.v1.func.Constant_Da;
import togos.noise.v1.func.Constant_Ia;
import togos.noise.v1.func.FunctionDaDa_DaIa;
import togos.noise.v1.func.MultiplyOutDaDaDa_Da;
import togos.noise.v1.func.PerlinDaDaDa_Da;
import togos.noise.v1.func.ScaleInDaDaDa_Da;
import togos.noise.v1.func.ScaleOutDaDaDa_Da;
import togos.noise.v1.func.TNLFunctionDaDaDa_Da;
import togos.noise.v1.func.TerrainScaleDaDaDa_Da;

public class SimpleWorldGenerator implements WorldGenerator
{
	public FunctionDaDa_DaIa groundFunction;
	public MaterialColumnFunction columnFunction;
	public ChunkMunger chunkMunger;
	public Map<String,Object> components;
	
	public static SimpleWorldGenerator DEFAULT;
	static {
		PerlinDaDaDa_Da perlin = new PerlinDaDaDa_Da();
		AddOutDaDaDa_Da sandLevel = new AddOutDaDaDa_Da(new TNLFunctionDaDaDa_Da[] {
			new Constant_Da(56),
			new TerrainScaleDaDaDa_Da( 16384, 16, perlin ),
			new TerrainScaleDaDaDa_Da(  8192, 16, perlin ),
			new TerrainScaleDaDaDa_Da(  4096, 16, perlin ),
			new TerrainScaleDaDaDa_Da(   512, 12, perlin ),
		});
		AddOutDaDaDa_Da dirtLevel = new AddOutDaDaDa_Da(new TNLFunctionDaDaDa_Da[] {
			sandLevel,
			new TerrainScaleDaDaDa_Da(  2048, 32, perlin  ),
			new TerrainScaleDaDaDa_Da(  1024, 12, perlin ),
			new TerrainScaleDaDaDa_Da(   256,  8, perlin ),
			new TerrainScaleDaDaDa_Da(    64,  8, perlin ),
			new TerrainScaleDaDaDa_Da(    32,  8, perlin ),
			new TerrainScaleDaDaDa_Da(    16,  8, perlin ),
			new TerrainScaleDaDaDa_Da(     4,  2, perlin ),
		});
		AddOutDaDaDa_Da stoneLevel = new AddOutDaDaDa_Da(new TNLFunctionDaDaDa_Da[] {
			dirtLevel,
			new Constant_Da(-4),
			new TerrainScaleDaDaDa_Da(  128,  8, perlin ),
			new MultiplyOutDaDaDa_Da(new TNLFunctionDaDaDa_Da[] {
				new TerrainScaleDaDaDa_Da( 1024,  2, perlin ),
				new TerrainScaleDaDaDa_Da(    8,  8, perlin ),
			}),
		});
		
		LayerTerrainGenerator lm = new LayerTerrainGenerator();
		lm.layers.add( new HeightmapLayer(
			new Constant_Ia(Blocks.WATER),
			new Constant_Da(32),
			new Constant_Da(64)
		));
		lm.layers.add( new HeightmapLayer(
			new Constant_Ia(Blocks.SAND),
			new Constant_Da(-10),
			new AdaptInXZDaDaDa_DaDa_Da(sandLevel)
		));
		lm.layers.add( new HeightmapLayer(
			new Constant_Ia(Blocks.DIRT),
			new Constant_Da(0),
			new AdaptInXZDaDaDa_DaDa_Da(dirtLevel)
		));
		lm.layers.add( new HeightmapLayer(
			new Constant_Ia(Blocks.STONE),
			new Constant_Da(1),
			new AdaptInXZDaDaDa_DaDa_Da(stoneLevel)
		));
		lm.layers.add( new HeightmapLayer(
			new Constant_Ia(Blocks.BEDROCK),
			new Constant_Da(0),
			new Constant_Da(1)
		));
		
		ChunkMungeList cmList = new ChunkMungeList(); 
		cmList.addMunger( lm.getChunkMunger() );
		cmList.addMunger( new Grassifier() );
		GroundStampPopulator tsp = new GroundStampPopulator(
			new RoundTreeGenerator(), 20,
			new AdaptInXZDaDaDa_DaDa_Da(new ScaleOutDaDaDa_Da(1d/16, new ScaleInDaDaDa_Da(1d/64, 1d/64, 1d/64, new PerlinDaDaDa_Da()))), 4,
			lm.getGroundFunction(), new int[]{Blocks.DIRT, Blocks.GRASS}
		);
		cmList.addMunger( new StampPopulatorChunkMunger(tsp) );
		
		DEFAULT = new SimpleWorldGenerator(cmList, lm.getGroundFunction(), lm.getColumnFunction(), lm.getComponents());
	}
	
	public SimpleWorldGenerator( ChunkMunger chunkMunger, FunctionDaDa_DaIa groundFunction, MaterialColumnFunction columnFunction, Map<String,Object> components ) {
		this.chunkMunger = chunkMunger;
		this.groundFunction = groundFunction;
		this.columnFunction = columnFunction;
		this.components = components;
	}
	
	public FunctionDaDa_DaIa getGroundFunction() {
		return groundFunction;
	}
	
	public MaterialColumnFunction getColumnFunction() {
		return columnFunction;
    }
	
	public ChunkMunger getChunkMunger() {
		return chunkMunger;
	}
	
	public Map<String,Object> getComponents() {
		return components;
	}
}
