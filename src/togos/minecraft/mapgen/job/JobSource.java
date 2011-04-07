package togos.minecraft.mapgen.job;

public interface JobSource
{
	/**
	 * @return a Job that needs to be run, or null if there will never be any more
	 *   jobs, ever (i.e. the scheduler's being shut down).
	 * @throws InterruptedException if the current thread gets interrupted (not normal operation)
	 */
	public Job getNextJob() throws InterruptedException;
}
