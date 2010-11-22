package togos.minecraft.mapgen;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

public class Dumper
{
	public static void main(String[] args) {
		try {
			String filename = args[0];
			FileInputStream is = new FileInputStream(new File(filename));
			Tag t;
			try {
				NBTInputStream nis = new NBTInputStream(is);
				t = nis.readTag();
				nis.close();
			} finally {
				is.close();
			}
			System.out.println(t.toString());
		} catch( IOException e ) {
			throw new RuntimeException(e);
		}
	}
}
