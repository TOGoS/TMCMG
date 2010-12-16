package togos.genfs.err;

public class ServerError extends FSError {
    private static final long serialVersionUID = 1L;
    
    public ServerError( Exception cause ) {
    	super(cause);
    }
}
