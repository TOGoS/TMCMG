package togos.mf.value;

import java.util.List;
import java.util.Map;

/** A common thing to pass back and forth as contents of a request or response */
public interface Arguments {
	/** Should return empty list if no positional arguments. */
	public List getPositionalArguments();
	/** should return empty map if no named arguments. */
	public Map getNamedArguments();
}
