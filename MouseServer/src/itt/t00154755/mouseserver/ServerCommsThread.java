package itt.t00154755.mouseserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.io.StreamConnection;

public class ServerCommsThread implements Runnable
{

	private final String TAG = "Server Communication Thread";
	private final StreamConnection connection; // client


	public ServerCommsThread( StreamConnection streamConn )
	{
		System.out.println(TAG + " -constructor");
		connection = streamConn;
	}


	@Override
	public void run()
	{

		BufferedReader bReader = null;
		try
		{
			InputStream inStream = connection.openInputStream();
			bReader = new BufferedReader(new InputStreamReader(inStream));

		}
		catch ( IOException e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.out.println(TAG + "shutting down the server 1");
			System.exit(-1);
		}

		// keep reading
		while ( true )
		{
			try
			{
				String lineRead = bReader.readLine();
				System.out.println(lineRead);

				sendDataToCursorRobot(lineRead);
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

		System.out.println(TAG + " -sending the data");
		System.out.println(TAG + " " + dataIn);
		CursorRobot cr = new CursorRobot();
		cr.dataFromServer(dataIn);
	}

}// end of Class
