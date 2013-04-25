package itt.t00154755.mouseserver;

import java.io.IOException;
import java.io.InputStream;

public class ServerCommsThread implements Runnable
{

	private final String TAG = "Server Communication Thread";
	private InputStream in = null;
	
	public ServerCommsThread( InputStream inStream )
	{
		System.out.println(TAG + "...constructor");
		in = inStream;
	}

	// the run method will read the data in to a string
	// the data will then pass to the cursor class
	@Override
	public void run()
	{
		System.out.println(TAG + " in the run() ");

		byte[] bytes = new byte[1024];
		int r;
		try
		{
			// create a new cursor robot object
			CursorRobot cursorRobot = new CursorRobot();
			// create a new thread and pass in the runnable object
			Thread sendDataThread = new Thread(cursorRobot);
			// start the thread
			sendDataThread.start();
			while ( ( r = in.read(bytes) ) > 0 )
			{
				// read the data in from the client
				String acceloData = ( new String(bytes, 0, r) );
				//System.out.println(TAG + acceloData);
				
				//pass the data to the robot class
				cursorRobot.setAcceloData(acceloData);
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
				in.close();
			}
			catch ( IOException ex )
			{
				System.out.println("error closing the input stream");
			}
			System.out.println(TAG + " - shutting down the server 1");
			System.exit(-1);
		}
		finally
		{
			try
			{
				// try to close the inout stream
				in.close();
			}
			catch ( IOException e )
			{
				System.out.println("error closing the input stream");
			}
			System.out.println(TAG + " - shutting down the server - finally");
			System.exit(-1);
		}
	}

}// end of Class
