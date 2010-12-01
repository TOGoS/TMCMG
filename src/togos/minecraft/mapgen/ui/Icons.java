package togos.minecraft.mapgen.ui;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class Icons
{
	protected static HashMap images = new HashMap();
	public static Image getIcon(String name) {
		if( !images.containsKey(name) ) {
			Image image = null;
			InputStream iconStream = Icons.class.getResourceAsStream(name);
			if( iconStream == null ) {
				System.err.println("Couldn't find "+name+" image resource");
			} else {
				try {
					image = ImageIO.read(iconStream);
					iconStream.close();
				} catch( IOException e ) {
					System.err.println("Couldn't load "+name+" image: "+e.getMessage());
				}
			}
			images.put(name,image);
		}
		return (Image)images.get(name);
	}
}
