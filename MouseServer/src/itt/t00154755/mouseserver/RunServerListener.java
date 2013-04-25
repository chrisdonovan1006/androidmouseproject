package itt.t00154755.mouseserver;

/**
 * 
 * @author Christopher Donovan - T00154755
 * 
 * Class used to run the server listener.
 * 
 */
public class RunServerListener
{
	/**
	 * 
	 */
	public void startServer()
	{
		// create a new thread that calls the runnable object.
		Thread runningServer = new Thread(new AppServerListener());
		// start the Thread
		runningServer.start();
	}

}// end of main method
