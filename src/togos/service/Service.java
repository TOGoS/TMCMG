package togos.service;

public interface Service
{
	public void start();
	
	/**
	 * Tells the service to 'hurry up and quit'.
	 * The service might not stop immediately -
	 * i.e. it may run for a short while after halt() is called.
	 */
	public void halt();
}
