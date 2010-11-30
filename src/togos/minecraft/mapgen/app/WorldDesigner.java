package togos.minecraft.mapgen.app;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
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
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.noise2.lang.ScriptError;

public class WorldDesigner
{
	public static class WorldDesignerKernel implements FileUpdateListener {
		ServiceManager sm = new ServiceManager();
		FileWatcher fw;
		File scriptFile;
		FileUpdateListener ful;
		GeneratorUpdateListener gul;
		boolean autoReloadEnabled = false;
		
		public void setFileUpdateListener( FileUpdateListener ful ) {
			this.ful = ful;
		}
		
		public void fileUpdated( File f ) {
			if( ful != null ) {
				ful.fileUpdated(f);
			}
		}
		
		protected void initFileWatcher() {
			if( scriptFile != null ) {
				fw = new FileWatcher(scriptFile);
				fw.addUpdateListener(this);
				sm.add(fw);
			}
		}
		
		public void setScriptFile( File f ) {
			if( fw != null ) {
				fw.halt();
				fw = null;
			}
			this.scriptFile = f;
			if( autoReloadEnabled ) initFileWatcher();
			fileUpdated(f);
		}
		
		public void setAutoReloadEnabled( boolean e ) {
			if( e == autoReloadEnabled ) return;
			
			autoReloadEnabled = e;
			if( e ) {
				initFileWatcher();
			} else {
				if( fw != null ) {
					sm.remove(fw);
					fw = null;
				}
			}
		}
		
		public void start() {  sm.start();  }
		public void halt() {  sm.halt();  }
	}
	
	public static class WorldDesignerMenuBar extends MenuBar {
        private static final long serialVersionUID = 1L;
        
        WorldDesignerKernel wdk;
		public WorldDesignerMenuBar( WorldDesignerKernel _wdk ) {
			this.wdk = _wdk;

			MenuItem loadMenuItem = new MenuItem("Load Script...",new MenuShortcut(KeyEvent.VK_O));
			loadMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed( ActionEvent arg0 ) {
					FileDialog fd = new FileDialog((Frame)null, "Load TNL Script", FileDialog.LOAD);
					fd.setVisible(true);
					if( fd.getFile() != null ) {
						wdk.setScriptFile( new File(fd.getDirectory()+"/"+fd.getFile()) );
					}
					fd.dispose();
				}
			});
			MenuItem reloadMenuItem = new MenuItem("Reload",new MenuShortcut(KeyEvent.VK_R));
			reloadMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed( ActionEvent arg0 ) {
					if( wdk.scriptFile != null ) {
						wdk.fileUpdated(wdk.scriptFile);
					}
				}
			});
			MenuItem exportMenuItem = new MenuItem("Export Chunks...",new MenuShortcut(KeyEvent.VK_X));
			exportMenuItem.setEnabled(false);
			exportMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed( ActionEvent arg0 ) {
					System.err.println("EXPORT!");
				}
			});
			CheckboxMenuItem autoReloadMenuItem = new CheckboxMenuItem("Auto Reload");
			autoReloadMenuItem.setShortcut(new MenuShortcut(KeyEvent.VK_A));
			autoReloadMenuItem.setState(wdk.autoReloadEnabled);
			autoReloadMenuItem.addItemListener(new ItemListener() {
				public void itemStateChanged( ItemEvent evt ) {
					wdk.setAutoReloadEnabled( evt.getStateChange() == ItemEvent.SELECTED );
				}
			});
			
			Menu fileMenu = new Menu("File");
			//fileMenu.setShortcut(new MenuShortcut(KeyEvent.VK_F)); // seems to have no effect
			fileMenu.add(loadMenuItem);
			fileMenu.add(reloadMenuItem);
			fileMenu.add(exportMenuItem);
			fileMenu.add("-");
			fileMenu.add(autoReloadMenuItem);
			add( fileMenu );
        }
	}
	
	public static void main( String[] args ) {
		String scriptFilename = null;
		boolean autoReload = false;
		boolean fullscreen = false;
		for( int i=0; i<args.length; ++i ) {
			if( "-auto-reload".equals(args[i]) ) {
				autoReload = true;
			} else if( "-fullscreen".equals(args[i]) ) {
				fullscreen = true;
			} else if( !args[i].startsWith("-") ) {
				scriptFilename = args[i];
			} else {
				System.err.println("Usage: NoiseCanvas <path/to/script.tnl>");
				System.exit(1);
			}
		}
		
		final WorldDesignerKernel wdk = new WorldDesignerKernel();
		
		final LayerSideCanvas lsc = new LayerSideCanvas();
		lsc.showZoom = false;
		final NoiseCanvas nc = new NoiseCanvas();
		final MasterWorldExplorerView mwev = new MasterWorldExplorerView();
		final Frame f = new Frame("World Preview");
		final WorldExploreKeyListener wekl = new WorldExploreKeyListener(mwev);
		
		final GeneratorUpdateListener gul = new GeneratorUpdateListener() {
			public void generatorUpdated( WorldGenerator wg ) {
				lsc.setWorldGenerator( wg );
				nc.setWorldGenerator( wg );
			}
		};
		
		wdk.setAutoReloadEnabled(autoReload);
		
		wdk.setFileUpdateListener(new FileUpdateListener() {
			public void fileUpdated( File scriptFile ) {
				try {
					WorldGenerator worldGenerator = (WorldGenerator)ScriptUtil.compile( new TNLWorldGeneratorCompiler(), scriptFile );
					gul.generatorUpdated( worldGenerator );
				} catch( ScriptError e ) {
					System.err.println(ScriptUtil.formatScriptError(e));
				} catch( FileNotFoundException e ) {
					System.err.println(e.getMessage());
					return;
				} catch( IOException e ) {
					throw new RuntimeException(e);
				}
			}
		});
		
		if( scriptFilename != null ) {
			File scriptFile = new File(scriptFilename);
			wdk.setScriptFile(scriptFile);
		}
		
		final WorldDesignerMenuBar menuBar = new WorldDesignerMenuBar(wdk);
		mwev.addSubView(nc);
		mwev.addSubView(lsc);
		lsc.setPreferredSize(new Dimension(640,128));
		lsc.setMaximumSize(new Dimension(Integer.MAX_VALUE,128));
		nc.setPreferredSize(new Dimension(640,384));

		Label errorLabel = new Label();
		errorLabel.setBackground(Color.BLACK);
		errorLabel.setForeground(Color.PINK);
		errorLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE,errorLabel.getPreferredSize().height));

		f.setMenuBar(menuBar);
		f.setLayout(new BoxLayout(f, BoxLayout.Y_AXIS));
		f.add(lsc);
		f.add(nc);
		f.add(errorLabel);
		f.validate();
		f.addWindowListener(new WindowListener() {
			public void windowOpened( WindowEvent arg0 ) {}
			public void windowIconified( WindowEvent arg0 ) {}
			public void windowDeiconified( WindowEvent arg0 ) {}
			public void windowDeactivated( WindowEvent arg0 ) {}
			public void windowClosing( WindowEvent arg0 ) {
				lsc.stopRenderer();
				nc.stopRenderer();
				f.dispose();
				wdk.halt();
			}
			public void windowClosed( WindowEvent arg0 ) {}
			public void windowActivated( WindowEvent arg0 ) {}
		});
		wdk.start();
		lsc.addKeyListener(wekl);
		nc.addKeyListener(wekl);
		if( fullscreen ) {
			f.setUndecorated(true);
			f.setExtendedState(Frame.MAXIMIZED_BOTH);
		} else {
			f.pack();
		}
		f.setVisible(true);
		mwev.setWorldPos(0,0,1);
		lsc.requestFocus();
	}
}
