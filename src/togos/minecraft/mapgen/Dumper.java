package togos.minecraft.mapgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

public class Dumper
{
	public static void main(String[] args) {
		String readFile = null;
		String writeFile = null;
		for( int i=0; i<args.length; ++i ) {
			if( "-write".equals(args[i]) ) {
				writeFile = args[++i];
			} else if( !args[i].startsWith("-") ) {
				readFile = args[i]; 
			}
		}
		
		if( readFile == null ) {
			System.err.println("No read file specified");
			System.exit(1);
		}
		
		try {
			FileInputStream is = new FileInputStream(new File(readFile));
			Tag t;
			try {
				NBTInputStream nis = NBTInputStream.gzipOpen(is);
				t = nis.readTag();
				nis.close();
			} finally {
				is.close();
			}
			System.out.println(t.toString());
			
			if( writeFile != null ) {
				FileOutputStream os = new FileOutputStream(new File(writeFile));
				try {
					NBTOutputStream nbtos = NBTOutputStream.gzipOpen(os);
					nbtos.writeTag(t);
					nbtos.close();
				} finally {
					os.close();
				}
			}
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
}
