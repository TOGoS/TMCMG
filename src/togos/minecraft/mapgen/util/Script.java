package togos.minecraft.mapgen.util;

import togos.jobkernel.uri.BaseRef;
import togos.mf.value.URIRef;
import togos.noise2.DigestUtil;

/**
 * For tracking script source, source URN, filename, 
 * and compiled 
 *
 */
public class Script
{
	public final byte[] source;
	public final String sourceFilename;
	public final URIRef sourceRef;
	public Object program;
	
	public Script( byte[] source, String sourceFilename ) {
		this.source = source;
		this.sourceFilename = sourceFilename;
		this.sourceRef = new BaseRef(DigestUtil.getSha1Urn(source));
	}
}
