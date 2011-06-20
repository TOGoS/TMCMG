package togos.noise2.vm;

public interface OpWriter
{
	// @param dest usually means the variable (must already be declared, if non-null)
	// in which to put the resulting value.  But the writer may decide to put the value
	// into a new value or to inline any expression.
	
	// @param type common name for data type, e.g. 'double', 'boolean', 'double[]', ...
	
	public String declareInput( String type, String nameSuggestion );
	public String declareVar( String type, String nameSuggestion );
	public String writeConstant( String dest, double value  );
	public String writeOp( String dest, String op, String[] operands );
}
