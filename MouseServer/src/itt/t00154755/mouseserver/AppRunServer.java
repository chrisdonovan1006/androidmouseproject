package itt.t00154755.mouseserver;

/**
 * 
 * 
 * Class used to run the server listener.
 * This class simply creates an reference to the AppServer,
 * and a thread to run the Server on.
 * 
 * * @author Christopher Donovan
 * 
 * @since 26/04/2013
 * @version 4.06
 */
public class AppRunServer
{

	/**
	 * Constructor:
	 * used by the AppServerGUI GUI to start the server.
	 */
	public void startServer()
	{
		// create a new thread that calls the runnable object.
		Thread runningServer = new Thread(new AppServer());
		// start the Thread
		runningServer.start();
	}

}// end of main method
