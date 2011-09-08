package togos.minecraft.mapgen.bukkit;

import java.io.File;
import java.io.IOException;

import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.noise2.lang.CompileError;
import togos.noise2.lang.ParseError;

public class TMCMGBukkitPlugin extends JavaPlugin
{
	public void onDisable() {
	}
	
	public void onEnable() {
		System.out.println(getDescription().getName() + " version " + getDescription().getVersion() + " enabled!");
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.WORLD_INIT, new WorldListener() {
			public void onWorldInit(WorldInitEvent event) {
				TMCMGBukkitPlugin.this.onWorldInit(event.getWorld());
			}
		}, Event.Priority.High, this);
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
	
	public void onWorldInit( World world ) {
		ChunkGenerator cg = world.getGenerator();
		if( !(cg instanceof TMCMGBukkitChunkGenerator) ) return; // not ours!
		if( !(world instanceof CraftWorld) ) {
			System.err.println("Can't override biomes in "+world.getName()+" because it is not a CraftWorld");
		}
		/*
		 * This is an attempt at overriding climate functions;
		 * it should be conditional on the script providing overrides,
		 * and right now it doesn't work anyway, so commented out.
		net.minecraft.server.World mcWorld = ((CraftWorld)world).getHandle();
		System.out.println("Setting worldProvider.b to TMCMGWorldChunkManager");
		mcWorld.worldProvider.b = new TMCMGWorldChunkManager();
		*/
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
        
        return new TMCMGBukkitChunkGenerator( worldName, wg );
	}
}
