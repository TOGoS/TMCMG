package togos.minecraft.mapgen.ui;

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import javax.swing.BoxLayout;

import org.jnbt.CompoundTag;
import org.jnbt.DoubleTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;

import togos.minecraft.mapgen.util.ChunkWritingService;
import togos.minecraft.mapgen.util.FileUpdateListener;
import togos.minecraft.mapgen.util.FileWatcher;
import togos.minecraft.mapgen.util.ServiceManager;
import togos.minecraft.mapgen.world.gen.ChunkMunger;
import togos.minecraft.mapgen.world.gen.WorldGenerator;

public class ChunkExportWindow extends Frame
{
    public class ProgressBar extends Canvas {
        private static final long serialVersionUID = 1L;
        
		float progress = 0.0f;
    	String message = "";
    	
    	public Color progressColor = new Color(0,128,0);
    	public Color errorColor = new Color(128,0,0);
    	public Color bgColor = new Color(96,96,96); 
    	
    	public ProgressBar() {
    		super();
    	}
    	
    	public void setProgress( float p, String message ) {
    		this.progress = p;
    		this.message = message;
    		repaint();
    	}
    	
    	public void setMessage( String message ) {
    		this.message = message;
    		repaint();
    	}
    	
    	public void update( Graphics g ) {
    		paint(g);
    	}
    	
    	public void paint( Graphics g ) {
    	    super.paint(g);
    	    Rectangle2D messageBounds = g.getFontMetrics().getStringBounds(message, g);
    	    float rat = Math.abs(progress);
    	    int cutoff = (int)(getWidth()*rat);
    	    g.setColor(progress < 0 ? errorColor : progressColor);
    	    g.fillRect(0, 0, cutoff, getHeight());
    	    g.setColor(bgColor);
    	    g.fillRect(cutoff, 0, getWidth()-cutoff, getHeight());
    	    int mx = getWidth()/2 - (int)messageBounds.getCenterX();
    	    int my = getHeight()/2 - (int)messageBounds.getCenterY();
    	    g.setColor(Color.WHITE);
    	    g.drawString(message, mx, my);
    	    g.setColor(Color.BLACK);
    	    g.drawRect(0, 0, getWidth()-1, getHeight()-1);
    	}
    }
    
    private static final long serialVersionUID = 1L;
    
    public WorldGenerator worldGenerator;
    public ChunkWritingService cws;
    
    Label outputDirField = new Label();
    TextField xField, zField, widthField, depthField;
    ProgressBar progressBar;
    Label levelDatStatusBar = new Label();
    ServiceManager sm;
    FileWatcher levelDatWatcher;
    
    protected void interpretLevelDat( File f ) {
    	if( !f.exists() ) {
    		levelDatStatusBar.setText("No level.dat found.");
    		return;
    	}
    	
    	try {
			FileInputStream is = new FileInputStream(f);
			try {
				NBTInputStream nis = new NBTInputStream(is);
				CompoundTag t = (CompoundTag)nis.readTag();
				CompoundTag d = (CompoundTag)t.getValue().get("Data");
				CompoundTag p = (CompoundTag)d.getValue().get("Player");
				ListTag pos = (ListTag)p.getValue().get("Pos");
				List posValues = pos.getValue();
				long x = ((DoubleTag)posValues.get(0)).getValue().longValue();
				long y = ((DoubleTag)posValues.get(1)).getValue().longValue();
				long z = ((DoubleTag)posValues.get(2)).getValue().longValue();
				
				int cx = (int)Math.floor(x/16d);
				int cz = (int)Math.floor(z/16d);
				
				levelDatStatusBar.setText("Player's at ("+x+","+y+","+z+"), in chunk ("+cx+","+cz+")." );
				nis.close();
			} finally {
				is.close();
			}
    	} catch( Exception e ) {
    		levelDatStatusBar.setText("Failed to find player: "+e.getMessage());
    	}
	}
    
    protected void initLevelDatWatcher() {
    	String chunkDir = outputDirField.getText();
    	File levelDat = new File(chunkDir + "/level.dat");
    	synchronized( this ) {
	    	if( levelDatWatcher != null ) {
	    		sm.remove(levelDatWatcher);
	    	}
	    	levelDatWatcher = new FileWatcher(levelDat, 1000);
	    	levelDatWatcher.addUpdateListener(new FileUpdateListener() {
				public void fileUpdated( File f ) {
					interpretLevelDat( f );
				}
			});
	    	sm.add(levelDatWatcher);
    	}
    	levelDatWatcher.forceUpdate();
    }
    
    public void setChunkExportBounds( int x, int z, int depth, int width ) {
    	xField.setText(""+x);
    	zField.setText(""+z);
    	widthField.setText(""+width);
    	depthField.setText(""+depth);
    }
    
    public void setChunkDir( String dir ) {
    	outputDirField.setText( new File(dir).getAbsolutePath() );
    	initLevelDatWatcher();
    }
    
    public void setWorldGenerator( WorldGenerator wg ) {
    	this.worldGenerator = wg;
    }
    
    public ChunkExportWindow( ServiceManager _sm, ChunkWritingService _cws ) {
    	super("Export Chunks");
    	
    	this.sm = _sm;
    	this.cws = _cws;
    	setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
    	
    	FocusListener selectAllFocusListener = new FocusListener() {
    		public void focusGained( FocusEvent evt ) {
    			TextField tf = (TextField)evt.getComponent();
    			tf.selectAll();
    		}
    		public void focusLost( FocusEvent evt ) {}
    	};
    	
    	Button browseDirButton = new Button("Output Dir");
    	outputDirField.setPreferredSize(new Dimension(200,0));
    	outputDirField.setAlignment(Label.RIGHT);
    	browseDirButton.addActionListener(new ActionListener() {
    		public void actionPerformed( ActionEvent arg0 ) {
    			FileDialog picker = new FileDialog((Frame)null, "    Find level.dat");
    			picker.setVisible(true);
    			if( picker.getFile() != null ) {
    				String dir = picker.getDirectory();
    				if( dir.endsWith("/") || dir.endsWith("\\") ) {
    					dir = dir.substring(0,dir.length()-1);
    				}
    				setChunkDir( dir );
    			}
    			picker.dispose();
    		}
    	});
    	
    	Panel outputdirPanel = new Panel();
    	outputdirPanel.setLayout(new BoxLayout(outputdirPanel,BoxLayout.X_AXIS));
    	outputdirPanel.add(browseDirButton);
    	outputdirPanel.add(outputDirField);
    	
    	xField = new TextField("-16",4);
    	xField.addFocusListener(selectAllFocusListener);
    	zField = new TextField("-16",4);
    	zField.addFocusListener(selectAllFocusListener);
    	widthField = new TextField("32",4);
    	widthField.addFocusListener(selectAllFocusListener);
    	depthField = new TextField("32",4);
    	depthField.addFocusListener(selectAllFocusListener);
    	Button generateButton = new Button("Generate");
    	generateButton.addActionListener(new ActionListener() {
			public void actionPerformed( ActionEvent arg0 ) {
				if( !cws.isRunning() ) {
					try {
						int bx = Integer.parseInt(xField.getText());
						int bz = Integer.parseInt(zField.getText());
						int bw = Integer.parseInt(widthField.getText());
						int bd = Integer.parseInt(depthField.getText());

						WorldGenerator wg = worldGenerator;
						if( wg == null ) {
							progressBar.setProgress(-1, "Error: no world generator");
							return;
						}
						ChunkMunger chunkMunger = wg.getChunkMunger();
						
						cws.setBounds(bx, bz, bw, bd);
						cws.setChunkDir(outputDirField.getText());
						cws.setChunkMunger(chunkMunger);
						cws.start();
					} catch( NumberFormatException e ) {
						progressBar.setProgress(-1, "Error: "+e.getMessage());
					}
				}
			}
		});
    	Button abortButton = new Button("Abort");
    	abortButton.addActionListener(new ActionListener() {
    		public void actionPerformed( ActionEvent arg0 ) {
    			cws.halt();
    		}
    	});
    	
    	Panel inputPanel = new Panel();
    	inputPanel.setLayout(new BoxLayout(inputPanel,BoxLayout.X_AXIS));
    	inputPanel.add(xField);
    	inputPanel.add(zField);
    	inputPanel.add(widthField);
    	inputPanel.add(depthField);
    	inputPanel.add(generateButton);
    	inputPanel.add(abortButton);
    	
    	progressBar = new ProgressBar();
    	progressBar.setPreferredSize(new Dimension(256,24));
    	progressBar.setMinimumSize(new Dimension(0,24));
    	cws.addProgressListener(new ChunkWritingService.ChunkWritingProgressListener() {
			public void chunkProgressUpdated( int chunksWritten, int totalChunks ) {
				progressBar.setProgress((float)chunksWritten/totalChunks, chunksWritten + " / " + totalChunks);
			}
		});
    	
    	add(outputdirPanel);
    	add(inputPanel);
    	add(progressBar);
    	add(levelDatStatusBar);
    	
    	addWindowListener(new WindowAdapter() {
    		public void windowActivated( WindowEvent evt ) {
    			if( levelDatWatcher != null ) {
    				sm.add(levelDatWatcher);
    			}
    		}
    		public void windowDeactivated( WindowEvent evt ) {
    			if( levelDatWatcher != null ) {
    				sm.remove(levelDatWatcher);
    			}
    		}
			public void windowClosing( WindowEvent evt ) {
				setVisible(false);
			}
		});
    	
    	validate();
    	pack();
    }
    
    public static void main( String[] args ) {
    	final ChunkWritingService cws = new ChunkWritingService();
    	final ServiceManager sm = new ServiceManager();
    	sm.add(cws);
    	final ChunkExportWindow cew = new ChunkExportWindow(sm,cws);
    	cew.addWindowListener(new WindowAdapter() {
    		public void windowClosing( WindowEvent e ) {
    			cew.dispose();
    			sm.halt();
    		}
    	});
    	cew.pack();
    	cew.setVisible(true);
    }
}
