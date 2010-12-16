package togos.genfs.err;

public class FSError extends Exception {
    private static final long serialVersionUID = 1L;
    
    public FSError() {}
    
    public FSError(Exception cause) { super(cause); }
}
