package togos.noise2.vm;

public interface OpWriter
{
	/**
	 * @param type common name for data type, e.g. 'double', 'boolean', 'double[]', ...
	 * @param resNameSuggestion
	 * @return
	 */
	public String declareInput( String type, String nameSuggestion );
	public String declareVar( String type, String nameSuggestion );
	public String writeConstant( String dest, double value  );
	public String writeOp( String dest, String op, String[] operands );
}
