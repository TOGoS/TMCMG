package togos.minecraft.mapgen.ui;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

public class HelpWindow extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	public HelpWindow() {
		super("About TOGoS's Minecraft Map Generator");
		
		JLabel helpLavel = new JLabel();
		
		String helpText = "Ach, couldn't load help text!";
		try {
			InputStream helpStream = getClass().getResourceAsStream("about.html");
			if( helpStream != null ) {
				int pq;
				char[] mahChars = new char[65536];
				Reader r = new InputStreamReader(helpStream, "UTF-8");
				
				helpText = "";
				while( (pq = r.read(mahChars)) > 0 ) {
					helpText += new String(mahChars,0,pq);
				}
				helpStream.close();
			}
		} catch( Exception e ) {
			helpText = "Ach, couldn't load help text: "+e.getMessage();
		}
		helpText = helpText.replace("\r\n","\n");
		helpText = helpText.replace("</p>\n\n<p>", "</p>\n<br />\n<p>");
		helpLavel.setText(helpText);
		helpLavel.setPreferredSize(new Dimension(640,512));
		
		helpLavel.setBorder(new EmptyBorder(4, 8, 4, 12));
		
		getContentPane().add(helpLavel);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				dispose();
			}
		});
		setIconImage(Icons.getIcon("mcs48.png"));
		pack();
	}
	
	public static void main( String[] args ) {
		HelpWindow hw = new HelpWindow();
		hw.setVisible(true);
	}
}
