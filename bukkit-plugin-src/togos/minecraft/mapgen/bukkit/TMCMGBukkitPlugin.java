package togos.minecraft.mapgen.bukkit;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.ParseError;
import togos.noise2.vm.dftree.data.DataDaDa;
import togos.noise2.vm.dftree.data.DataDaIa;
import togos.noise2.vm.dftree.func.FunctionDaDa_DaIa;

public class TMCMGBukkitPlugin extends JavaPlugin
{
	public void onDisable() {
	}
	
	public void onEnable() {
		System.out.println(getDescription().getName() + " version " + getDescription().getVersion() + " enabled!");
	}
	
	protected boolean debug = false;
	
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args ) {
		if( sender instanceof Player ) {
			return false;
		}
		if( args.length == 0 ) {
			sender.sendMessage("TMCMG debug mode is "+(debug?"on":"off"));
		} else if( args.length == 1 ) {
			if( "on".equalsIgnoreCase(args[0]) ) {
				debug = true;
				return true;
			} else if( "off".equalsIgnoreCase(args[0]) ) {
				debug = false;
				return true;
			}
		}
		return false;
	}
	
	protected File findScript( String worldName, String id ) {
		File f;
		if( id != null && (f = new File( id )).exists() ) return f;
		if( (f = new File( worldName+".tnl" )).exists() ) return f;
		if( (f = new File( "plugins/TMCMG/"+worldName+".tnl" )).exists() ) return f;
		return null;
	}
	
	public ChunkGenerator getDefaultWorldGenerator(final String worldName, String id) {
		File tnlFile = findScript(worldName, id);
		if( !tnlFile.exists() ) {
			System.out.println("Couldn't find TNL file '"+id+"' or based on world name '"+worldName+"'");
			return null;
		}
		
		WorldGenerator wg;
        try {
        	System.out.println("Compiling "+tnlFile+"...");
	        wg = (WorldGenerator)ScriptUtil.compile( new TNLWorldGeneratorCompiler(), tnlFile );
	        System.out.println("Compiled "+tnlFile);
        } catch( ParseError e ) {
	        e.printStackTrace();
	        return null;
        } catch( CompileError e ) {
	        e.printStackTrace();
	        return null;
        } catch( IOException e ) {
	        e.printStackTrace();
	        return null;
        }
        
        final FunctionDaDa_DaIa groundFunction = wg.getGroundFunction();
		final ChunkMunger cm = wg.getChunkMunger();
		
		return new ChunkGenerator() {
			public boolean canSpawn( World world, int x, int z ) {
				DataDaDa coords = new DataDaDa( 1, new double[] { x }, new double[] { z } );
				DataDaIa gnd = groundFunction.apply(coords);
				boolean cs = gnd.d[0] > 32 && gnd.d[0] < 96 && gnd.i[0] != Blocks.WATER && gnd.i[0] != Blocks.LAVA;
				System.out.println("Can spawn at "+worldName+":"+x+","+z+"?    "+cs);
				return cs;
			}
			
			public byte[] generate( World world, Random r, int cx, int cz ) {
				if( debug ) {
					System.out.println("Generating chunk "+worldName+"/"+cx+","+cz+"...");
				}
				ChunkData cd = ChunkData.forChunkCoords( cx, cz );
				long beginTime = System.currentTimeMillis();
				cm.mungeChunk( cd );
				long endTime = System.currentTimeMillis();
				if( debug ) {
					System.out.println("Generated chunk "+worldName+"/"+cx+","+cz+" in "+(endTime-beginTime)+"ms");
				}
				return cd.blockData;
            }
		};
	}
}
