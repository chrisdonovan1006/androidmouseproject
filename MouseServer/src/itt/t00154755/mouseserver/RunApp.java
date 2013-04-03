package itt.t00154755.mouseserver;

import java.io.IOException;

/**
 * 
 * @author Christopher
 * 
 */
public class RunApp 
{

	/**
	 * This is the main method use to satrt the server app.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException 
	{
		Thread runningServer = new Thread(new AppServer());
		runningServer.start();
	}// end of main method

}// end of main method
