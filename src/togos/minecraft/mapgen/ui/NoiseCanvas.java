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
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

import togos.minecraft.mapgen.noise.AddOutDaDaDa_Da;
import togos.minecraft.mapgen.noise.PerlinDaDaDa_Da;
import togos.minecraft.mapgen.noise.ScaleInDaDaDa_Da;
import togos.minecraft.mapgen.noise.ScaleOutDaDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDaDa_Da;
import togos.minecraft.mapgen.noise.api.FunctionDaDa_Ia;

public class NoiseCanvas extends Canvas
{
    private static final long serialVersionUID = 1L;
    

    
	static class RandomColorFunction implements FunctionDaDa_Ia {
		PerlinDaDaDa_Da perlin = new PerlinDaDaDa_Da();
		AddOutDaDaDa_Da summed = new AddOutDaDaDa_Da(new FunctionDaDaDa_Da[] {
				new ScaleOutDaDaDa_Da( new ScaleInDaDaDa_Da(perlin,0.001,0.001,0.001), 0.03 ),
				new ScaleOutDaDaDa_Da( new ScaleInDaDaDa_Da(perlin,0.01,0.01,0.01), 0.01 ),
				new ScaleOutDaDaDa_Da( new ScaleInDaDaDa_Da(perlin,0.1,0.1,0.1), 0.01 ),
				new ScaleOutDaDaDa_Da( new ScaleInDaDaDa_Da(perlin,1,1,1), 0.001 ),
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
	
	FunctionDaDa_Ia colorFunc = new RandomColorFunction();
	NoiseRenderer cnr;
	
	public void update(Graphics g) {
		paint(g);
	}
	
	public void setPos( double wx, double wy, double zoom ) {
		double mpp = 1/zoom;
		double leftX = wx-mpp*getWidth()/2;
		double topY = wy-mpp*getHeight()/2;
		startRenderer(new NoiseRenderer(colorFunc,getWidth(),getHeight(),leftX,topY,mpp,mpp));
	}
	
	/*
	 * If this is crashing, run with VM options:
	 * -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true
	 */
	public void paint(Graphics g) {
		BufferedImage buf; 
		if( cnr != null && (buf = cnr.buffer) != null ) {
			synchronized( buf ) {
				g.drawImage(buf, 0, 0, null);
			}
		} else {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
		}
	}
	
	public void stopRenderer() {
		if( cnr != null ) {  cnr.halt();  }
	}
	
	public void startRenderer( NoiseRenderer nr ) {
		this.stopRenderer();
		this.cnr = nr;
		this.cnr.start();
	}
	
	public static void main( String[] args ) {
		final Frame f = new Frame("Noise canvas");
		final NoiseCanvas nc = new NoiseCanvas();
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
	}
}
