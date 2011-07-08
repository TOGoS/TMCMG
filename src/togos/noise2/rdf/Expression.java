package togos.noise2.rdf;

import java.util.List;
import togos.noise2.lang.SourceLocation;

public interface Expression
{
	public SourceLocation getSourceLocation();
	public String getTypeName();
	/** Returns a list of Map.Entry for all arguments */
	public List getAttributeEntries();
	public List getAttributeValues(String name);
	/**
	 * base32(sha1(typeName+k1@v1ID+k2@v2ID+...))
	 * @return
	 */
	public String getIdentifier();
}
