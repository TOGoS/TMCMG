package togos.minecraft.mapgen.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import togos.jobkernel.Service;
import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.util.FileUpdateListener;
import togos.minecraft.mapgen.util.FileWatcher;
import togos.minecraft.mapgen.util.GeneratorUpdateListener;
import togos.minecraft.mapgen.util.Script;
import togos.minecraft.mapgen.util.ServiceManager;
import togos.minecraft.mapgen.util.Util;
import togos.minecraft.mapgen.world.Blocks;
import togos.minecraft.mapgen.world.LayerUtil;
import togos.minecraft.mapgen.world.Materials;
import togos.minecraft.mapgen.world.gen.HeightmapLayer;
import togos.minecraft.mapgen.world.gen.LayerTerrainGenerator;
import togos.minecraft.mapgen.world.gen.SimpleWorldGenerator;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.noise2.lang.ParseUtil;
import togos.noise2.lang.ScriptError;
import togos.noise2.vm.dftree.data.DataDaDa;
import togos.noise2.vm.dftree.data.DataDaDaDa;
import togos.noise2.vm.dftree.lang.FunctionUtil;

public class LayerSideCanvas extends WorldExplorerViewCanvas
{
    private static final long serialVersionUID = 1L;
	
	class LayerSideRenderer implements Runnable, Service {
		List layers;
		int width, height;
		double worldX, worldZ, worldXPerPixel;
		final int worldFloor = 0, worldCeiling = 128;
		
		public volatile BufferedImage buffer;
		protected volatile boolean stop = false;		
		
		public LayerSideRenderer( List layers, int width, int height,
			double worldX, double worldZ, double worldXPerPixel
		) {
			this.layers = layers;
			this.width = width;
			this.height = height;
			this.worldX = worldX;
			this.worldZ = worldZ;
			this.worldXPerPixel = worldXPerPixel;
		}
		
		protected BufferedImage createBuffer() {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			
			// Create an image that does not support transparency
			return gc.createCompatibleImage(width, height, Transparency.OPAQUE);
		}
		
		protected Color color(int argb) {
			return new Color(argb);
		}
		
		public void run() {
			if( width == 0 || height == 0 ) return;
			
			buffer = createBuffer();
			Graphics g = buffer.getGraphics();
			
			int[] px = new int[width];
			
			double[] wx = new double[width];
			double[] wz = new double[width];
			int[] color = new int[width];
			
			synchronized( buffer ) {
				g.setColor( color(0xFF00AAFF) );
				g.fillRect(0,0,width,height);
			}
			for( Iterator li=layers.iterator(); li.hasNext(); ) {
				HeightmapLayer layer = (HeightmapLayer)li.next();
				for( int i=0; i<width; ++i ) {
					px[i] = i;
					wx[i] = worldX + px[i]*worldXPerPixel;
					wz[i] = worldZ;
				}
				DataDaDa input = new DataDaDa(wx,wz);
				int[] floor = LayerUtil.roundHeights(layer.floorHeightFunction.apply(input).x);
				int[] ceil  = LayerUtil.roundHeights(layer.ceilingHeightFunction.apply(input).x);
				double[] maxY = LayerUtil.maxY(ceil);
				if( FunctionUtil.isConstant(layer.typeFunction) ) {
					// To speed things up, special case when type is constant:
					// (Would also work if we only knew it was constant wrt. Y)
					
					DataDaDaDa typeInput = new DataDaDaDa(wx,maxY,wz);
					int[] type = layer.typeFunction.apply(typeInput).v;
					
					for( int i=0; i<width; ++i ) {
						if( type[i] == Blocks.NONE ) {
							color[i] = 0x00000000;
						} else if( type[i] == Blocks.AIR ) {
							color[i] = 0xFF0088FF;
						} else {
							color[i] = Materials.getByBlockType(type[i]).color;
						}
					}
					synchronized( buffer ) {
						for( int i=0; i<width; ++i ) {
							if( ceil[i] > worldCeiling ) ceil[i] = worldCeiling;
							if( floor[i] < worldFloor ) floor[i] = worldFloor;
							if( ceil[i] <= floor[i] ) continue;
							if( color[i] == 0x00000000 ) continue;
							
							int c = ((worldCeiling - ceil[i]) * height / (worldCeiling-worldFloor));
							int f = ((worldCeiling - floor[i]) * height / (worldCeiling-worldFloor));
							
							g.setColor( color(color[i]) );
							g.fillRect( px[i], c, 1, f-c );
						}
					}
				} else {
					BufferedImage columnBuffer = new BufferedImage(1,height,BufferedImage.TYPE_INT_ARGB);
					
					for( int i=0; i<width && !stop; ++i ) {
						if( ceil[i] > worldCeiling ) ceil[i] = worldCeiling;
						if( floor[i] < worldFloor ) floor[i] = worldFloor;
						if( ceil[i] < floor[i] ) continue;
						
						int colHeight = (int)ceil[i] - (int)floor[i];
						double[] colX = new double[colHeight];
						double[] colY = new double[colHeight];
						double[] colZ = new double[colHeight];
						int[] colColors = new int[colHeight];
						for( int j=0; j<colHeight; ++j ) {
							colX[j] = wx[i];
							colY[j] = (int)ceil[i] - j;
							colZ[j] = wz[i];
						}
						DataDaDaDa finput = new DataDaDaDa(colX,colY,colZ);
						int[] colTypes = layer.typeFunction.apply(finput).v;
						for( int j=0; j<colHeight; ++j ) {
							if( colTypes[j] == Blocks.NONE ) {
								colColors[j] = 0x00000000;
							} else if( colTypes[j] == Blocks.AIR ) {
								colColors[j] = 0xFF0088FF;
							} else {
								colColors[j] = Materials.getByBlockType(colTypes[j]).color;
							}
						}
						
						columnBuffer.setRGB(0, 0, 1, colHeight, colColors, 0, 1);
						synchronized( buffer ) {
							g.drawImage(columnBuffer,
								i, height-(int)ceil[i], i+1, height-(int)floor[i],
								0, 0, 1, colHeight,
								null);
						}
						
						// Request a repaint after every few columns of these ~expensive~ layers
						if( i % 32 == 31 ) repaint();
					}
					// Request a repaint after every layer is drawn
					repaint();
				}
			}
			// Request a repaint when *done*
			repaint();
		}
		
		public void halt() {
			this.stop = true;
		}
		
		public void start() {
			new Thread(this).start();
		}
	}
	
	protected List layers;
	LayerSideRenderer cnr;
	
	public LayerSideCanvas() {
		super();
    }
	
	protected void stateUpdated() {
		double mpp = 1/zoom;
		double leftX = wx-mpp*getWidth()/2;
		stopRenderer();
		
		if( wg == null ) {
			layers = null;
		} else if( wg.getGroundFunction() instanceof LayerTerrainGenerator.LayerGroundFunction ) {
			this.layers = ((LayerTerrainGenerator.LayerGroundFunction)wg.getGroundFunction()).layers;
		} else {
			System.err.println("Aagh this ground function is not of the layered variety");
			System.err.println("Can't show it in LayerSideCanvas.");
		}
		
		if( layers != null ) {
			startRenderer(new LayerSideRenderer(layers,getWidth(),getHeight(),leftX,wy,mpp));
		}
	}
	
	public void update(Graphics g) {
		paint(g);
	}
	
	/*
	 * If this is crashing, run with VM options:
	 * -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true
	 */
	public void paint(Graphics g) {
		BufferedImage buf;
		LayerSideRenderer nr = cnr;
		if( nr != null && (buf = nr.buffer) != null ) {
			// Not sure if locking is really needed here...
			synchronized( buf ) {
				g.drawImage(buf, 0, 0, null);
			}
		} else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		paintOverlays(g);
	}
	
	public void stopRenderer() {
		if( cnr != null ) {
			cnr.halt();
			cnr = null;
		}
	}
	
	public void startRenderer( LayerSideRenderer nr ) {
		this.stopRenderer();
		this.cnr = nr;
		this.cnr.start();
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
		final Frame f = new Frame("Noise Canvas");
		final LayerSideCanvas nc = new LayerSideCanvas();
		
		final GeneratorUpdateListener gul = new GeneratorUpdateListener() {
			public void generatorUpdated( Script s ) {
				nc.setWorldGenerator( (WorldGenerator)s.program );
			}
		};
		
		final FileUpdateListener ful = new FileUpdateListener() {
			public void fileUpdated( File scriptFile ) {
				try {
					Script script = Util.readScript(scriptFile);
					script.program = (WorldGenerator)ScriptUtil.compile( new TNLWorldGeneratorCompiler(), script );
					gul.generatorUpdated( script );
				} catch( ScriptError e ) {
					System.err.println(ParseUtil.formatScriptError(e));
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
				FileWatcher fw = new FileWatcher( scriptFile, 500 );
				fw.addUpdateListener(ful);
				sm.add(fw);
			}
		} else {
			Script emptyScript = new Script( new byte[0], "" );
			emptyScript.program = SimpleWorldGenerator.DEFAULT;
			gul.generatorUpdated( emptyScript );
		}
		
		nc.setPreferredSize(new Dimension(512,128));
		nc.addKeyListener(new WorldExploreKeyListener(nc));

		f.add(nc);
		f.pack();
		f.addWindowListener(new WindowListener() {
			public void windowOpened( WindowEvent arg0 ) {}
			public void windowIconified( WindowEvent arg0 ) {}
			public void windowDeiconified( WindowEvent arg0 ) {}
			public void windowDeactivated( WindowEvent arg0 ) {}
			public void windowClosing( WindowEvent arg0 ) {
				nc.stopRenderer();
				f.dispose();
				sm.halt();
			}
			public void windowClosed( WindowEvent arg0 ) {}
			public void windowActivated( WindowEvent arg0 ) {}
		});
		sm.start();
		f.setVisible(true);
		nc.setWorldPos(0,0,1);
		nc.requestFocus();
	}
}
