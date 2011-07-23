package togos.noise2.rdf;

public class TNLNamespace
{
	// '+' in argument lists indicates that the following
	// argument can occur 1...inf times
	
	public static final String TN_NS = "http://ns.nuke24.net/TOGoSNoise/";
	
	public static final String PI = TN_NS+"PI";
	
	// Expressions of these types represent input values.
	// @todo: I expect these to eventually be obsoleted by
	// some generic 'variable' expression.
	public static final String X_VAR = TN_NS+"X";
	public static final String Y_VAR = TN_NS+"Y";
	public static final String Z_VAR = TN_NS+"Z";
	
	// X, Y, Z expression attributes for functions such as perlin, simplex
	public static final String X = TN_NS+"x";
	public static final String Y = TN_NS+"y";
	public static final String Z = TN_NS+"z";
	
	public static final String ADD  = TN_NS+"Add";
	public static final String TERM = TN_NS+"term";
    public static final String[] ADD_ARGS = new String[]{ "+", TERM };
	
	public static final String SUBTRACT   = TN_NS+"Subtract";
	public static final String MINUEND    = TN_NS+"minuend";
	public static final String SUBTRAHEND = TN_NS+"subtrahend";
    public static final String[] SUBTRACT_ARGS = new String[]{ MINUEND, "+", SUBTRAHEND };
    
	public static final String MULTIPLY = TN_NS+"Multiply";
	public static final String FACTOR   = TN_NS+"factor";
    public static final String[] MULTIPLY_ARGS = new String[]{ "+", FACTOR };
	
	public static final String DIVIDE   = TN_NS+"Divide";
	public static final String DIVIDEND = TN_NS+"dividend";
	public static final String DIVISOR  = TN_NS+"divisor";
    public static final String[] DIVIDE_ARGS = new String[]{ DIVIDEND, "+", DIVISOR };
	
	public static final String EXPONENTIATE = TN_NS+"Exponentiate";
	public static final String BASE         = TN_NS+"base";
	public static final String EXPONENT     = TN_NS+"exponent";
	public static final String[] EXPONENTIATE_ARGS = new String[]{ BASE, EXPONENT };
	
	public static final String D5_2PERLIN = TN_NS+"D5.2-Perlin";
	public static final String SIMPLEX    = TN_NS+"Simplex";
	public static final String[] NOISE3D_ARGS = new String[]{ X, Y, Z };
	
	public static final String SIN  = TN_NS+"Sine";
	public static final String COS  = TN_NS+"Cosine";
	public static final String TAN  = TN_NS+"Tangent";
	public static final String ATAN = TN_NS+"Atctangent";
	public static final String[] TRIG1D_ARGS = new String[]{ X };
}
