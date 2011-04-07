package togos.minecraft.mapgen.job;

import java.util.Iterator;

public interface JobListRunner
{
	public JobListHandle enqueueJobs( Iterator jobs ); 
}
