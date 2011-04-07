package togos.minecraft.mapgen.job;

public interface JobStatusListener
{
	public void jobStatusUpdated( Job j, int oldStatus, int newStatus );
}
