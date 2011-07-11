package togos.rdf;

import java.util.List;

public interface RDFDescription extends RDFExpression
{
	public String getTypeName();
	/** Returns a list of Map.Entry for all arguments,
	 * where the entry key is the fully-namespaced attribute name as a string,
	 * and the value is an RDFExpression */
	public List getAttributeEntries();
	public List getAttributeValues(String name);
	/**
	 * base32(sha1(typeName+k1@v1ID+k2@v2ID+...))
	 * @return
	 */
	public String getIdentifier();
}
