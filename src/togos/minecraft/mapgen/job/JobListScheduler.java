package togos.minecraft.mapgen.job;

import java.util.Iterator;

public interface JobListScheduler
{
	public JobListHandle enqueueJobs( Iterator jobs, String listName ); 
}
