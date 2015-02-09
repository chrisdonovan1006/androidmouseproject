package itt.t00154755.mouseserver;

import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * This class get the InputStream and reads in the data converts it to a String and
 * passes it on to the AppRobot class.
 * 
 * * @author Christopher Donovan
 * 
 * @since 26/04/2013
 * @version 4.06
 * 
 */
public class AppStreamReader implements Runnable
{

	// private final String TAG = "Server Communication Thread";
	private InputStream inStream = null;


	public AppStreamReader()
	{
		// System.out.println(TAG + "...constructor");
	}


	public void setStream( InputStream inStream )
	{
		// set the incoming stream
		this.inStream = inStream;
	}


	// the run method will read the data inStream to a string
	// the data will then pass to the cursor class
	@Override
	public void run()
	{
		// System.out.println(TAG + " inStream the run() ");
		byte[] bytes = new byte[1024];
		int r;
		try
		{
			// create a new appRobot object
			AppRobot appRobot = new AppRobot();
			// create a new thread and pass inStream the runnable object
			Thread sendDataThread = new Thread(appRobot);
			// start the thread
			sendDataThread.start();
			while ( ( r = inStream.read(bytes) ) > 0 )
			{
				// read the data inStream from the client
				String acceloData = ( new String(bytes, 0, r) );
				// System.out.println(TAG + acceloData);

				// pass the data to the robot class
				appRobot.setAcceloData(acceloData);
			}
		}
		catch ( IOException e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();

			try
			{
				// try to close the input stream
				inStream.close();
			}
			catch ( IOException ex )
			{
				// System.out.println("error closing the input stream");
			}
			// System.out.println(TAG + " - shutting down the server 1");
			System.exit(-1);
		}
	}

}// end of Class