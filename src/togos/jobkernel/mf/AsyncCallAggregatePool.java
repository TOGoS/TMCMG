package togos.jobkernel.mf;

import java.util.WeakHashMap;

import togos.mf.api.Request;

public class AsyncCallAggregatePool
{
	public static AsyncCallAggregatePool instance = new AsyncCallAggregatePool();
	
	protected WeakHashMap aggregates = new WeakHashMap();
	
	public synchronized AsyncCallAggregate getAggregate( Request req ) {
		AsyncCallAggregate ag = (AsyncCallAggregate)aggregates.get(req);
		if( ag == null ) aggregates.put( req, ag = new AsyncCallAggregate() );
		return ag;
	}
}
