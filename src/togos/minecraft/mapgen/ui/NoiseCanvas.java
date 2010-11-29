package togos.minecraft.mapgen.ui;

import java.awt.Canvas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import togos.minecraft.mapgen.ScriptUtil;
import togos.minecraft.mapgen.world.gen.GroundColorFunction;
import togos.minecraft.mapgen.world.gen.TNLWorldGeneratorCompiler;
import togos.minecraft.mapgen.world.gen.SimpleWorldGenerator;
import togos.minecraft.mapgen.world.gen.WorldGenerator;
import togos.noise2.function.AddOutDaDaDa_Da;
import togos.noise2.function.FunctionDaDaDa_Da;
import togos.noise2.function.FunctionDaDa_Ia;
import togos.noise2.function.PerlinDaDaDa_Da;
import togos.noise2.function.ScaleInDaDaDa_Da;
import togos.noise2.function.ScaleOutDaDaDa_Da;

public class NoiseCanvas extends Canvas
{
    private static final long serialVersionUID = 1L;
    
	static class RandomColorFunction implements FunctionDaDa_Ia {
		PerlinDaDaDa_Da perlin = new PerlinDaDaDa_Da();
		AddOutDaDaDa_Da summed = new AddOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
			new ScaleOutDaDaDa_Da( 0.03 , new ScaleInDaDaDa_Da(0.001,0.001,0.001, perlin) ),
			new ScaleOutDaDaDa_Da( 0.01 , new ScaleInDaDaDa_Da(0.01,0.01,0.01, perlin) ),
			new ScaleOutDaDaDa_Da( 0.01 , new ScaleInDaDaDa_Da(0.1,0.1,0.1, perlin) ),
			new ScaleOutDaDaDa_Da( 0.001, new ScaleInDaDaDa_Da(1,1,1, perlin) ),
		});
		
		public void apply( int count, double[] inX, double[] inY, int[] out ) {
			double[] dirtLevel = new double[count];
			summed.apply(count, inX, inY, new double[count], dirtLevel);
		    for( int i=0; i<count; ++i ) {
		    	out[i] = dirtLevel[i] < 0 ? 0xFF000000 : 0xFFFFFFFF;
		    }
		}
	}
	
	class NoiseRenderer implements Runnable {
		FunctionDaDa_Ia colorFunction;
		int width, height;
		double worldX, worldY, worldXPerPixel, worldYPerPixel;
		
		public volatile BufferedImage buffer;
		protected volatile boolean stop = false;		
		
		public NoiseRenderer( FunctionDaDa_Ia colorFunction, int width, int height,
			double worldX, double worldY, double worldXPerPixel, double worldYPerPixel
		) {
			this.colorFunction = colorFunction;
			this.width = width;
			this.height = height;
			this.worldX = worldX;
			this.worldY = worldY;
			this.worldXPerPixel = worldXPerPixel;
			this.worldYPerPixel = worldYPerPixel;
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
		
		class CoordPopulator {
			int currentX, currentY, currentScale = 32;
			boolean sub = false;
			protected int nextCoords( int[] px, int[] py, int[] scale ) {
				int i;
				for( i=0; i<px.length; currentX += currentScale ) {
					if( currentX >= width ) {
						currentX = 0;
						currentY += currentScale;
					}
					if( currentY >= height ) {
						currentScale /= 2;
						currentX = 0;
						currentY = 0;
						sub = true;
					}
					if( currentScale == 0 ) {
						++i;
						break;
					}
					if( sub && (currentX / currentScale) % 2 == 0 && (currentY / currentScale) % 2 == 0 ) {
						continue;
					}
					px[i] = currentX;
					py[i] = currentY;
					scale[i] = currentScale;
					++i;
				}
				return i;
			}
		}
		
		CoordPopulator cpop = new CoordPopulator();
		
		public void run() {
			buffer = createBuffer();
			Graphics g = buffer.getGraphics();
			
			int[] px = new int[256];
			int[] py = new int[256];
			int[] scale = new int[256];
			
			double[] wx = new double[256];
			double[] wy = new double[256];
			int[] color = new int[256];
			
			while( !stop ) {
				int coordCount = cpop.nextCoords(px, py, scale);
				if( coordCount == 0 ) return;
				for( int i=0; i<coordCount; ++i ) {
					wx[i] = worldX + px[i]*worldXPerPixel;
					wy[i] = worldY + py[i]*worldYPerPixel;
				}
				colorFunction.apply(coordCount,wx,wy,color);
				synchronized( buffer ) {
					for( int i=0; i<coordCount; ++i ) {
						g.setColor( color(color[i]) );
						g.fillRect( px[i], py[i], scale[i], scale[i] );
					}
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
	
	double wx, wy, zoom;
	
	FunctionDaDa_Ia colorFunc;
	NoiseRenderer cnr;
	
	public NoiseCanvas() {
		addComponentListener(new ComponentListener() {
			public void componentShown( ComponentEvent arg0 ) {
				updateRenderer();
			}
			public void componentResized( ComponentEvent arg0 ) {
				updateRenderer();
			}
			public void componentMoved( ComponentEvent arg0 ) {
			}
			public void componentHidden( ComponentEvent arg0 ) {
			}
		});
		addKeyListener(new KeyListener() {
			public void keyTyped( KeyEvent evt ) {
			}
			public void keyReleased( KeyEvent evt ) {
			}
			public void keyPressed( KeyEvent evt ) {
				switch( evt.getKeyCode() ) {
				case(KeyEvent.VK_MINUS): case(KeyEvent.VK_UNDERSCORE):
					setPos(wx,wy,zoom/2);
					break;
				case(KeyEvent.VK_PLUS): case(KeyEvent.VK_EQUALS):
					setPos(wx,wy,zoom*2);
					break;
				case(KeyEvent.VK_UP):
					setPos(wx,wy-getHeight()/(4*zoom),zoom);
					break;
				case(KeyEvent.VK_DOWN):
					setPos(wx,wy+getHeight()/(4*zoom),zoom);
					break;
				case(KeyEvent.VK_LEFT):
					setPos(wx-getWidth()/(4*zoom),wy,zoom);
					break;
				case(KeyEvent.VK_RIGHT):
					setPos(wx+getWidth()/(4*zoom),wy,zoom);
					break;
				}
			}
		});
    }
	
	protected void setTitle(String t) {
	}
	
	protected void updateRenderer() {
		double mpp = 1/zoom;
		setTitle(mpp+" mpp");
		double leftX = wx-mpp*getWidth()/2;
		double topY = wy-mpp*getHeight()/2;
		stopRenderer();
		if( colorFunc != null ) {
			startRenderer(new NoiseRenderer(colorFunc,getWidth(),getHeight(),leftX,topY,mpp,mpp));
		}
	}
	
	public void setPos( double wx, double wy, double zoom ) {
		if( zoom == 0 ) {
			throw new RuntimeException("Zoom cannot be zero!");
		}
		if( Double.isInfinite(zoom) ) {
			throw new RuntimeException("Zoom cannot be infinite!");
		}
		if( Double.isNaN(zoom) ) {
			throw new RuntimeException("Zoom must be a number!");
		}
		this.wx = wx;
		this.wy = wy;
		this.zoom = zoom;
		this.updateRenderer();
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
		NoiseRenderer nr = cnr;
		if( nr != null && (buf = nr.buffer) != null ) {
			synchronized( buf ) {
				g.drawImage(buf, 0, 0, null);
			}
		} else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	public void stopRenderer() {
		if( cnr != null ) {
			cnr.halt();
			cnr = null;
		}
	}
	
	public void startRenderer( NoiseRenderer nr ) {
		this.stopRenderer();
		this.cnr = nr;
		this.cnr.start();
	}
	
	public static void main( String[] args ) {
		String scriptFile = null;
		for( int i=0; i<args.length; ++i ) {
			if( !args[i].startsWith("-") ) {
				scriptFile = args[i];
			} else {
				System.err.println("Usage: NoiseCanvas <path/to/script.tnl>");
				System.exit(1);
			}
		}
		
		final Frame f = new Frame("Noise Canvas");
		// TODO: probably want to set up some listener instead of overriding
		final NoiseCanvas nc = new NoiseCanvas() {
			protected void setTitle(String t) {  f.setTitle("Noise Canvas ("+t+")");  }
		};
		
		WorldGenerator worldGenerator;
		if( scriptFile != null ) {
			try {
				worldGenerator = (WorldGenerator)ScriptUtil.compile( new TNLWorldGeneratorCompiler(), new File(scriptFile) );
			} catch( FileNotFoundException e ) {
				System.err.println(e.getMessage());
				System.exit(1);
				return;
			} catch( IOException e ) {
				throw new RuntimeException(e);
			}
		} else {
			worldGenerator = SimpleWorldGenerator.DEFAULT;
		}
		
		nc.colorFunc = new GroundColorFunction( worldGenerator.getGroundFunction() );
		
		nc.setPreferredSize(new Dimension(512,384));
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
			}
			public void windowClosed( WindowEvent arg0 ) {}
			public void windowActivated( WindowEvent arg0 ) {}
		});
		f.setVisible(true);
		nc.setPos(0,0,1);
		nc.requestFocus();
	}
}
