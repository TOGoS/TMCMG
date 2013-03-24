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

import togos.minecraft.mapgen.MaterialColumnFunction;
import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.util.FileUpdateListener;
import togos.minecraft.mapgen.util.FileWatcher;
import togos.minecraft.mapgen.util.GeneratorUpdateListener;
import togos.minecraft.mapgen.util.Script;
import togos.minecraft.mapgen.util.ServiceManager;
import togos.minecraft.mapgen.util.Util;
import togos.minecraft.mapgen.world.Material;
import togos.minecraft.mapgen.world.gen.SimpleWorldGenerator;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.minecraft.mapgen.world.structure.ChunkData;
import togos.noise.v1.lang.ParseUtil;
import togos.lang.ScriptError;
import togos.service.Service;

public class ColumnSideCanvas extends WorldExplorerViewCanvas
{
    private static final long serialVersionUID = 1L;
	
    public static final int SKY_COLOR = 0xFF00AAFF;
	public final int worldFloor = 0, worldCeiling = ChunkData.NORMAL_CHUNK_HEIGHT;
    
	class ColumnSideRenderer implements Runnable, Service {
		MaterialColumnFunction cFunc;
		int width, height;
		double worldX, worldZ, worldXPerPixel;
		
		public volatile BufferedImage buffer;
		protected volatile boolean stop = false;		
		
		public ColumnSideRenderer( MaterialColumnFunction cFunc, int width, int height,
			double worldX, double worldZ, double worldXPerPixel
		) {
			this.cFunc = cFunc;
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
			
			final int sectWidth = 32; 
			
			double[] wx = new double[sectWidth];
			double[] wy = new double[height];
			double[] wz = new double[sectWidth];
			Material[] mat = new Material[sectWidth*height];
			int[] imgCol = new int[sectWidth*height];
			
			synchronized( buffer ) {
				g.setColor( color(SKY_COLOR) );
				g.fillRect(0,0,width,height);
			}
			
			for( int i=0; i<height; ++i ) {
				wy[i] = 0 + i;
			}
			
			for( int sx=0; sx<width && !stop; sx+=sectWidth ) {
				int sw = width - sx;
				if( sw > sectWidth ) sw = sectWidth;
				for( int i=0; i<sw; ++i ) {
					wx[i] = worldX + (sx+i)*worldXPerPixel;
					wz[i] = worldZ;
				}
				cFunc.apply( sw, wx, wz, height, wy, mat );
				
				for( int x=0; x<sw; ++x ) {
					for( int y=0; y<height; ++y ) {
						Material material = mat[x*height+y];
						int color = material.color == 0 ? SKY_COLOR : material.color;
						imgCol[x+(height-y-1)*sectWidth] = color;
					}
				}
				
				synchronized( buffer ) {
					buffer.setRGB(sx, 0, sw, height, imgCol, 0, sectWidth);
				}
				repaint();
			}
		}
		
		public void halt() {
			this.stop = true;
		}
		
		public void start() {
			new Thread(this).start();
		}
	}
	
	protected MaterialColumnFunction cFunc;
	ColumnSideRenderer cnr;
	
	public ColumnSideCanvas() {
		super();
    }
	
	protected void stateUpdated() {
		double mpp = 1/zoom;
		double leftX = wx-mpp*getWidth()/2;
		stopRenderer();
		
		if( wg == null ) {
			cFunc = null;
		} else {
			cFunc = wg.getColumnFunction();
		}
		
		if( cFunc != null ) {
			startRenderer(new ColumnSideRenderer(cFunc,getWidth(),getHeight(),leftX,wy,mpp));
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
		ColumnSideRenderer nr = cnr;
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
	
	public void startRenderer( ColumnSideRenderer nr ) {
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
		final ColumnSideCanvas nc = new ColumnSideCanvas();
		
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
