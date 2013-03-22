package togos.rdf;

import java.util.List;
import java.util.Map;

/**
 * Represents RDF properties of a single subject.
 */
public interface RDFDescription extends RDFExpression
{
	/**
	 * Fully namespaced name of the type of thing
	 * this is describing.
	 */
	public String getTypeName();
	/**
	 * Returns a list of Map.Entry for all arguments,
	 * where the entry key is the fully-namespaced attribute name as a string,
	 * and the value is an RDFExpression
	 */
	public List<Map.Entry<String,Object>> getAttributeEntries();
	public List<Object> getAttributeValues(String name);
	/**
	 * base32(sha1(typeName+k1@v1ID+k2@v2ID+...))
	 * RDFExpressionUtil.generateIdentifier can implement this for you.
	 * @return
	 */
	public String getIdentifier();
}
