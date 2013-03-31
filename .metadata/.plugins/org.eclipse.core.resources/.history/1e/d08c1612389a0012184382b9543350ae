package itt.t00154755.mouseserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.bluetooth.RemoteDevice;
import javax.microedition.io.StreamConnection;

public class ServerCommsThread extends Thread
{
	private final String		TAG			= "Server Communication Thread";
	private StreamConnection	conn;											// client
																				// connection
	private InputStream			dataIn;
	private volatile boolean	isRunning	= false;

	public ServerCommsThread ( StreamConnection streamConnection )
	{
		this.conn = streamConnection;

		RemoteDevice reDevice;
		try
		{
			reDevice = RemoteDevice.getRemoteDevice(conn);

			System.out.println(TAG + "...Server is Connected to: \n"
					+ reDevice.getBluetoothAddress() + "\n"
					+ reDevice.getFriendlyName(false));

		} catch ( IOException e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		}
		;
	}

	@Override
	public void run( )
	{
		try
		{
			System.out.println("sct run method");
			dataIn = conn.openInputStream();

			readInDataFromTheClient();
		} catch ( IOException e1 )
		{
			//
			e1.printStackTrace();
		}

	}

	private void readInDataFromTheClient( )
	{
		BufferedReader buffIn = new BufferedReader(
				new InputStreamReader(dataIn));
		isRunning = true;
		String acceloData;
		while ( isRunning )
		{
			if ( buffIn == null )
			{
				System.out.println("buff in is empty");
				isRunning = false;
			} else
			{
				try
				{
					System.out.println("read line");
					acceloData = buffIn.readLine();
					System.out.println("send to robot");
					sendToRobot(acceloData);
				} catch ( IOException e )
				{
					//
					e.printStackTrace();
				}
			}

		}
	}

	private void sendToRobot( String acceloData )
	{
		System.out.println("sned to robot method");
		CursorRobot cr = new CursorRobot(acceloData);
		System.out.println("start the robot");
		cr.startCursorRobot();
	}
}// end of Class