package togos.noise2.vm.stkernel;

import java.util.List;
import java.util.Map;

public class STVectorKernel implements VectorOp
{
	public final Map vars;
	protected final VectorOp[] ops;
	protected final int maxVectorSize;
	
	public STVectorKernel( Map vars, List ops, int maxVectorSize ) {
		this.vars = vars;
		this.ops = new VectorOp[ops.size()];
		for( int i=0; i<this.ops.length; ++i ) {
			this.ops[i] = (VectorOp)ops.get(i);
		}
		this.maxVectorSize = maxVectorSize;
	}
	
	public final void invoke(int vectorSize) {
		if( vectorSize > maxVectorSize ) {
			throw new RuntimeException("Invoked with vector size too large ("+vectorSize+" / "+maxVectorSize+")");
		}
		for( int i=0; i<ops.length; ++i ) {
			ops[i].invoke(vectorSize);
		}
	}
}
