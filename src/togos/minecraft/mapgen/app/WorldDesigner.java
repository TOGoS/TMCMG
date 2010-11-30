package togos.minecraft.mapgen.app;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BoxLayout;

import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.ui.LayerSideCanvas;
import togos.minecraft.mapgen.ui.MasterWorldExplorerView;
import togos.minecraft.mapgen.ui.NoiseCanvas;
import togos.minecraft.mapgen.ui.WorldExploreKeyListener;
import togos.minecraft.mapgen.util.FileUpdateListener;
import togos.minecraft.mapgen.util.FileWatcher;
import togos.minecraft.mapgen.util.GeneratorUpdateListener;
import togos.minecraft.mapgen.util.ServiceManager;
import togos.minecraft.mapgen.world.gen.SimpleWorldGenerator;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.noise2.lang.ScriptError;

public class WorldDesigner
{
	public static class ControlPanel extends Panel {
        private static final long serialVersionUID = 1L;

		public ControlPanel() {
        }
	}
	
	public static void main( String[] args ) {
		String scriptFilename = null;
		boolean autoReload = false;
		for( int i=0; i<args.length; ++i ) {
			if( "-auto-reload".equals(args[i]) ) {
				autoReload = true;
			} else if( !args[i].startsWith("-") ) {
				scriptFilename = args[i];
			} else {
				System.err.println("Usage: NoiseCanvas <path/to/script.tnl>");
				System.exit(1);
			}
		}
		
		final ServiceManager sm = new ServiceManager();
		final Frame f = new Frame("World Preview");
		final LayerSideCanvas lsc = new LayerSideCanvas();
		lsc.showZoom = false;
		final NoiseCanvas nc = new NoiseCanvas();
		final MasterWorldExplorerView mwev = new MasterWorldExplorerView();
		mwev.addSubView(nc);
		mwev.addSubView(lsc);
		final WorldExploreKeyListener wekl = new WorldExploreKeyListener(mwev);
		
		final GeneratorUpdateListener gul = new GeneratorUpdateListener() {
			public void generatorUpdated( WorldGenerator wg ) {
				lsc.setWorldGenerator( wg );
				nc.setWorldGenerator( wg );
			}
		};
		
		final FileUpdateListener ful = new FileUpdateListener() {
			public void fileUpdated( File scriptFile ) {
				try {
					WorldGenerator worldGenerator = (WorldGenerator)ScriptUtil.compile( new TNLWorldGeneratorCompiler(), scriptFile );
					gul.generatorUpdated( worldGenerator );
				} catch( ScriptError e ) {
					System.err.println(ScriptUtil.formatScriptError(e));
				} catch( FileNotFoundException e ) {
					System.err.println(e.getMessage());
					System.exit(1);
					return;
				} catch( IOException e ) {
					throw new RuntimeException(e);
				}
			}
		};
		
		if( scriptFilename != null ) {
			File scriptFile = new File(scriptFilename);
			ful.fileUpdated( scriptFile );
			if( autoReload ) {
				FileWatcher fw = new FileWatcher( scriptFile );
				fw.addUpdateListener(ful);
				sm.add(fw);
			}
		} else {
			gul.generatorUpdated( SimpleWorldGenerator.DEFAULT );
		}
		
		f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
		//ControlPanel cp = new ControlPanel();
		//f.add(cp);
		lsc.setPreferredSize(new Dimension(640,128));
		lsc.setMaximumSize(new Dimension(Integer.MAX_VALUE,128));
		f.add(lsc);
		nc.setPreferredSize(new Dimension(640,384));
		f.add(nc);
		f.pack();
		f.addWindowListener(new WindowListener() {
			public void windowOpened( WindowEvent arg0 ) {}
			public void windowIconified( WindowEvent arg0 ) {}
			public void windowDeiconified( WindowEvent arg0 ) {}
			public void windowDeactivated( WindowEvent arg0 ) {}
			public void windowClosing( WindowEvent arg0 ) {
				lsc.stopRenderer();
				nc.stopRenderer();
				f.dispose();
				sm.halt();
			}
			public void windowClosed( WindowEvent arg0 ) {}
			public void windowActivated( WindowEvent arg0 ) {}
		});
		sm.start();
		lsc.addKeyListener(wekl);
		nc.addKeyListener(wekl);
		f.setVisible(true);
		mwev.setWorldPos(0,0,1);
		lsc.requestFocus();
	}
}
