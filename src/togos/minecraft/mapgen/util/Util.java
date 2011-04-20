package togos.minecraft.mapgen.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;

import togos.jobkernel.uri.BaseRef;
import togos.jobkernel.uri.URIUtil;
import togos.mf.value.URIRef;

public class Util
{
	public static final Charset UTF8 = Charset.forName("UTF-8");
	
	public static final String string( Object o ) {
		if( o instanceof String ) {
			return (String)o;
		} else if( o instanceof byte[] ) {
			return new String( (byte[])o, UTF8 );
		} else if( o == null ) {
			return null;
		} else {
			throw new RuntimeException("Don't know how to turn "+o.getClass()+" into a string");
		}
	}
	
	public static final byte[] readFile( File f ) throws IOException {
		FileInputStream fis = new FileInputStream(f);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int r;
		while( (r = fis.read(buffer)) > 0 ) {
			baos.write(buffer, 0, r);
		}
		fis.close();
		return baos.toByteArray();
	}
	
	public static final URIRef readFileToDataRef( File f ) throws IOException {
		return new BaseRef(URIUtil.makeDataUri(readFile(f)));
	}
}
