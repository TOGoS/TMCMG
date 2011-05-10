package togos.jobkernel;

public interface Service
{
	public void start();
	/**
	 * Tells the service to stop running.
	 * The service may not stop immediately -
	 * i.e. it may run for a short while after halt() is called.
	 */
	public void halt();
}
