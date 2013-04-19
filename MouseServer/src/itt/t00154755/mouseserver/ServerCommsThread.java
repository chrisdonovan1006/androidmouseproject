package itt.t00154755.mouseserver;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.StreamConnection;

public class ServerCommsThread implements Runnable
{

	private final String TAG = "Server Communication Thread";
	private StreamConnection connection = null;


	public ServerCommsThread( StreamConnection connection )
	{
		System.out.println(TAG + "...constructor");
		this.connection = connection;
	}


	@Override
	public void run()
	{
		InputStream in = null;
		
		try
		{
			in = connection.openInputStream();
		}
		catch ( IOException e )
		{
			System.out.println("Error creating the Input stream");
		}

		// keep reading
		while ( true )
		{
			try
			{
				byte[] bytes = new byte[1024];
				int r;
				while ( ( r = in.read(bytes) ) > 0 )
				{
					String lineIn = new String(bytes, 0, r);

					if ( lineIn != null )
					{
						System.out.println(TAG + "\npassing data to the robot");
						// start a new Thread that will handle incoming traffic
						Thread robotThread = new Thread(new CursorRobot( lineIn ));
						robotThread.start();
					}
				}

			}
			catch ( IOException e )
			{
				// print the error stack
				e.printStackTrace();
				e.getCause();
				System.out.println(TAG + "shutting down the server 2");
				System.exit(-1);
			}
		}
	}

}// end of Class
