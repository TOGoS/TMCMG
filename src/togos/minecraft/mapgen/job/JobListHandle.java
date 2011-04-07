package togos.minecraft.mapgen.job;

public interface JobListHandle
{
	/** Wait for all jobs to finish. */
	public void join() throws InterruptedException;
	
	/** Don't try to run any more jobs from the list */
	public void cancel();
}
