package itt.t00154755.mouseserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ServerCommsThread implements Runnable
{

	private final String TAG = "Server Communication Thread";
	private InputStream inputStream = null; // client
	private CursorRobot cr = null;


	public ServerCommsThread( InputStream in )
	{
		System.out.println(TAG + "...constructor");
		inputStream = in;
	}


	@Override
	public void run()
	{

		// keep reading
		while ( true )
		{
			try
			{
				DataInputStream is = new DataInputStream(inputStream);

				byte[] bytes = new byte[1024];
				int r;
				while ( ( r = is.read(bytes) ) > 0 )
				{
					String lineIn = new String(bytes, 0, r);

					if ( lineIn != null )
					{
						System.out.println(TAG + "\npassing data to the robot");
						sendDataToCursorRobot(lineIn);
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


	private void sendDataToCursorRobot( String dataIn )
	{
		System.out.println(TAG + " " + dataIn);
		cr = new CursorRobot();
		cr.dataFromServer(dataIn);
	}

}// end of Class
