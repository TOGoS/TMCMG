package togos.noise.v3.program.runtime;

import java.util.HashMap;
import java.util.Map;

public class Context extends HashMap<String,Binding<?>>
{
    private static final long serialVersionUID = 4281181357537070099L;
	
    public Context() {
    	super();
    }
    
    public Context(Map<String,Binding<?>> copyFrom) {
    	super(copyFrom);
    }
}
