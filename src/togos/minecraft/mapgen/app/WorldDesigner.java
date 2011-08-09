package togos.minecraft.mapgen.app;

import java.awt.CheckboxMenuItem;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;

import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.job.JobService;
import togos.minecraft.mapgen.job.RemoteJobService;
import togos.minecraft.mapgen.ui.ChunkExportWindow;
import togos.minecraft.mapgen.ui.ColumnSideCanvas;
import togos.minecraft.mapgen.ui.HelpWindow;
import togos.minecraft.mapgen.ui.Icons;
import togos.minecraft.mapgen.ui.MasterWorldExplorerView;
import togos.minecraft.mapgen.ui.NoiseCanvas;
import togos.minecraft.mapgen.ui.Stat;
import togos.minecraft.mapgen.ui.WorldExploreKeyListener;
import togos.minecraft.mapgen.util.ChunkWritingService;
import togos.minecraft.mapgen.util.FileUpdateListener;
import togos.minecraft.mapgen.util.FileWatcher;
import togos.minecraft.mapgen.util.GeneratorUpdateListener;
import togos.minecraft.mapgen.util.Script;
import togos.minecraft.mapgen.util.ServiceManager;
import togos.minecraft.mapgen.util.Util;
import togos.minecraft.mapgen.world.Material;
import togos.minecraft.mapgen.world.Materials;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.noise2.lang.ParseUtil;
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
		JobService jobServ = new JobService();
		RemoteJobService remoteJobServ = new RemoteJobService(jobServ.jobQueue);
		
		public WorldDesignerKernel() {
			sm.add(jobServ);
			sm.add(remoteJobServ);
		}
		
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
				fw = new FileWatcher(scriptFile,500);
				fw.addUpdateListener(this);
				sm.add(fw);
			}
		}
		
		public void setScriptFile( File f ) {
			if( fw != null ) {
				fw.halt();
				fw = null;
			}
			this.scriptFile = f.getAbsoluteFile();
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
	
	public class WorldDesignerMenuBar extends MenuBar {
		private static final long serialVersionUID = 1L;
		
		WorldDesignerKernel wdk;
		
		CheckboxMenuItem autoReloadMenuItem;
		CheckboxMenuItem normalShadingMenuItem;
		CheckboxMenuItem heightShadingMenuItem;
		
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
			//exportMenuItem.setEnabled(false);
			exportMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed( ActionEvent arg0 ) {
					chunkExportWindow.setVisible(true);
					chunkExportWindow.pack();
				}
			});
			autoReloadMenuItem = new CheckboxMenuItem("Auto Reload");
			autoReloadMenuItem.setShortcut(new MenuShortcut(KeyEvent.VK_A));
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
			
			
			normalShadingMenuItem = new CheckboxMenuItem("Enable Normal Shading");
			normalShadingMenuItem.setShortcut(new MenuShortcut(KeyEvent.VK_N));
			normalShadingMenuItem.addItemListener(new ItemListener() {
				public void itemStateChanged( ItemEvent evt ) {
					noiseCanvas.normalShadingEnabled = ( evt.getStateChange() == ItemEvent.SELECTED );
					noiseCanvas.stateUpdated();
				}
			});
			heightShadingMenuItem = new CheckboxMenuItem("Enable Height Shading");
			heightShadingMenuItem.setShortcut(new MenuShortcut(KeyEvent.VK_H));
			heightShadingMenuItem.addItemListener(new ItemListener() {
				public void itemStateChanged( ItemEvent evt ) {
					noiseCanvas.heightShadingEnabled = ( evt.getStateChange() == ItemEvent.SELECTED );
					noiseCanvas.stateUpdated();
				}
			});

			Menu viewMenu = new Menu("View");
			viewMenu.add(normalShadingMenuItem);
			viewMenu.add(heightShadingMenuItem);

			
			
			MenuItem aboutMenuItem = new MenuItem("About");
			aboutMenuItem.setShortcut(new MenuShortcut(KeyEvent.VK_F1));
			aboutMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed( ActionEvent e ) {
					new HelpWindow().setVisible(true);
				}
			});
			
			Menu helpMenu = new Menu("Help");
			helpMenu.add(aboutMenuItem);
			
			add( fileMenu );
			add( viewMenu );
			add( helpMenu );
		}
		
		public void initState() {
			normalShadingMenuItem.setState(noiseCanvas.normalShadingEnabled);
			heightShadingMenuItem.setState(noiseCanvas.heightShadingEnabled);
			autoReloadMenuItem.setState(wdk.autoReloadEnabled);
		}
	}
	
	WorldDesignerKernel wdk = new WorldDesignerKernel();
	ChunkWritingService cws = new ChunkWritingService(wdk.jobServ.jobQueue);
	ChunkExportWindow chunkExportWindow = new ChunkExportWindow(wdk.sm,cws);
	
	ColumnSideCanvas lsc = new ColumnSideCanvas();
	NoiseCanvas noiseCanvas = new NoiseCanvas();
	Label statusLabel = new Label();
	MasterWorldExplorerView mwev = new MasterWorldExplorerView() {
		public void setWorldPos(double wx, double wy, double zoom) {
			super.setWorldPos(wx, wy, zoom);
			updatePositionStatus();
		}
	};
	Frame previewWindow = new Frame("World Preview");
	
	public WorldDesigner() {
		lsc.showZoom = false;
		noiseCanvas.showZoom = false;
	}
	
	public static String USAGE =
		"Usage: WorldDesigner [options] <path/to/script.tnl>\n" +
		"Options:\n" +
		"  -chunk-dir <dir> ; default directory to store generated map chunks\n" +
		"  -auto-reload     ; poll script file and automatically reload when updated\n" +
		"  -fullscreen      ; display maximized with no border\n" +
		"  -normal-shading  ; enable angle-based terrain shading (slow)\n" +
		"  -height-shading  ; enable height-based terrain shading)\n"+
		"  -job-system      ; enable job system\n" +
		"  -remote-generator [n*]<url> ; specify a remote web server to help\n" +
		"                   ; calculate chunks (requires job system)\n" +
		"Other commands:\n" +
		"  -?, -h           ; print help and exit\n" +
		"  -dump-materials  ; print material list and exit";
	
	protected void setStatus( boolean isError, String text ) {
		statusLabel.setForeground(isError ? Color.PINK : Color.GREEN);
		statusLabel.setText(text.replace("\n", "   "));
	}
	
	protected void updatePositionStatus() {
		setStatus(false, "MPP: "+(1/mwev.getZoom())+",    "+
			mwev.getWorldX()+", "+mwev.getWorldY());
	}
	
	protected static String alignLeft( String s, int width, char pad ) {
		if( s.length() > width ) {
			s = s.substring(0,width);
		}
		while( s.length() < width ) {
			s += pad;
		}
		return s;
	}
	
	protected static String alignRight( String s, int width, char pad ) {
		if( s.length() > width ) {
			s = s.substring(s.length()-width);
		}
		while( s.length() < width ) {
			s = pad + s;
		}
		return s;
	}
		
	protected static String formatMaterialInfo( Material m ) {
		return
			alignLeft( Materials.normalizeName(m.name), 30, ' ' ) + " " +
			alignLeft( m.name, 40, ' ' ) + " " +
			"0x" + alignRight(Integer.toString(m.blockType, 16).toUpperCase(), 2, '0');
	}
	
	protected static Pattern WRPAT = Pattern.compile("^(\\d+)\\*(.*)$");
	
	public void run(String[] args) {
		String scriptFilename = null;
		boolean autoReload = false;
		boolean fullscreen = false;
		boolean normalShade = false;
		boolean heightShade = false;
		boolean jobSystem = false;
		ArrayList remoteGenerators = new ArrayList();
		String chunkDir = "output-chunks";
		for( int i=0; i<args.length; ++i ) {
			if( "-chunk-dir".equals(args[i]) ) {
				chunkDir = args[++i];
			} else if( "-auto-reload".equals(args[i]) ) {
				autoReload = true;
			} else if( "-fullscreen".equals(args[i]) ) {
				fullscreen = true;
			} else if( "-normal-shading".equals(args[i]) ) {
				normalShade = true;
			} else if( "-height-shading".equals(args[i]) ) {
				heightShade = true;
			} else if( "-perf-log".equals(args[i]) ) {
				Stat.performanceLoggingEnabled = true;
			} else if( "-job-system".equals(args[i]) ) {
				jobSystem = true;
			} else if( "-remote-generator".equals(args[i]) ) {
				remoteGenerators.add(args[++i]);
			} else if( "-dump-materials".equals(args[i]) ) {
				for( int j=0; j<Materials.byBlockType.length; ++j ) {
					Material m = Materials.byBlockType[j];
					if( m != null ) {
						System.out.println(formatMaterialInfo(m));
					}
				}
				System.exit(0);
			} else if( "-?".equals(args[i]) || "-h".equals(args[i]) || "--help".equals(args[i]) ) {
				System.out.println(USAGE);
				System.exit(0);
			} else if( !args[i].startsWith("-") ) {
				scriptFilename = args[i];
			} else {
				System.err.println(USAGE);
				System.exit(1);
			}
		}
		
		Image icon = Icons.getIcon("mcs48.png");
		
		chunkExportWindow.setChunkDir( chunkDir );
		chunkExportWindow.setIconImage(icon);
		
		final WorldExploreKeyListener wekl = new WorldExploreKeyListener(mwev);
		
		final GeneratorUpdateListener gul = new GeneratorUpdateListener() {
			public void generatorUpdated( Script s ) {
				lsc.setWorldGenerator( (WorldGenerator)s.program );
				noiseCanvas.setWorldGenerator( (WorldGenerator)s.program );
				chunkExportWindow.setScript( s );
			}
		};
		
		cws.useJobSystem = jobSystem;
		
		for( Iterator i=remoteGenerators.iterator(); i.hasNext(); ) {
			String wr = (String)i.next();
			Matcher m = WRPAT.matcher(wr);
			int count;
			if( m.matches() ) {
				count = Integer.parseInt(m.group(1));
				wr = m.group(2);
			} else {
				count = 2;
			}
			wdk.remoteJobServ.addWebResolver( wr, count );
		}
			
		
		wdk.setAutoReloadEnabled(autoReload);
		
		wdk.setFileUpdateListener(new FileUpdateListener() {
			public void fileUpdated( File scriptFile ) {
				try {
					Script script = Util.readScript(scriptFile);
					script.program = (WorldGenerator)ScriptUtil.compile( new TNLWorldGeneratorCompiler(), scriptFile );
					gul.generatorUpdated( script );
					updatePositionStatus();
				} catch( ScriptError e ) {
					String errText = ParseUtil.formatScriptError(e);
					setStatus(true,errText);
					System.err.println(errText);
				} catch( FileNotFoundException e ) {
					String errText = e.getMessage();
					setStatus(true,errText);
					System.err.println(errText);
				} catch( IOException e ) {
					throw new RuntimeException(e);
				}
			}
		});
		
		if( scriptFilename != null ) {
			File scriptFile = new File(scriptFilename);
			wdk.setScriptFile(scriptFile);
		}
		
		wdk.sm.add(cws);
		
		wdk.start();
		
		final WorldDesignerMenuBar menuBar = new WorldDesignerMenuBar(wdk);
		mwev.addSubView(noiseCanvas);
		mwev.addSubView(lsc);
		lsc.setPreferredSize(new Dimension(640,128));
		lsc.setMaximumSize(new Dimension(Integer.MAX_VALUE,128));
		noiseCanvas.setPreferredSize(new Dimension(640,384));
		noiseCanvas.normalShadingEnabled = normalShade;
		noiseCanvas.heightShadingEnabled = heightShade;

		statusLabel.setBackground(Color.BLACK);
		statusLabel.setForeground(Color.PINK);
		statusLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE,statusLabel.getPreferredSize().height));
		statusLabel.validate();

		previewWindow.setMenuBar(menuBar);
		previewWindow.setLayout(new BoxLayout(previewWindow, BoxLayout.Y_AXIS));
		previewWindow.add(lsc);
		previewWindow.add(noiseCanvas);
		previewWindow.add(statusLabel);
		previewWindow.validate();
		previewWindow.addWindowListener(new WindowAdapter() {
			public void windowClosing( WindowEvent arg0 ) {
				halt();
			}
		});
		lsc.addKeyListener(wekl);
		noiseCanvas.addKeyListener(wekl);

		if( fullscreen ) {
			previewWindow.setUndecorated(true);
			previewWindow.setExtendedState(Frame.MAXIMIZED_BOTH);
		} else {
			previewWindow.setIconImage(icon);
			previewWindow.pack();
		}
		
		menuBar.initState();
		previewWindow.setVisible(true);
		
		chunkExportWindow.initState();
		chunkExportWindow.pack();
		
		lsc.requestFocus();
	}
	
	protected void halt() {
		lsc.stopRenderer();
		noiseCanvas.stopRenderer();
		previewWindow.dispose();
		chunkExportWindow.dispose();
		wdk.halt();
	}
	
	public static void main( String[] args ) {
		WorldDesigner wd = new WorldDesigner();
		try {
			wd.run(args);
		} catch( RuntimeException e ) {
			wd.halt();
			throw e;
		} catch( Exception e ) {
			wd.halt();
			throw new RuntimeException(e);
		}
	}
}
