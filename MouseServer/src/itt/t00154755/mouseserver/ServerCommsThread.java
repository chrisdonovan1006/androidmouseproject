package itt.t00154755.mouseserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

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
		String acceloData = null;
		try
		{
			dataIn = conn.openInputStream();
			acceloData = readInDataFromTheClient();
		} catch ( IOException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		isRunning = true;
		while ( isRunning )
		{
			if ( acceloData == null )
			{
				System.out.println("no data");
				try
				{
					sendDataToTheServer();
				} catch ( IOException e )
				{
					e.printStackTrace();
					closeTheStream();
				}
				isRunning = false;
			} else
			{
				
			}
		}

	}

	/**
	 * @throws IOException
	 */
	private void sendDataToTheServer( ) throws IOException
	{
		final String WAITING = "waiting";
		OutputStream dataOut = conn.openOutputStream();
		PrintWriter writeOut = new PrintWriter(dataOut);
		writeOut.print(WAITING);
		writeOut.flush();
	}

	private String readInDataFromTheClient( ) throws IOException
	{
		BufferedReader buffIn = new BufferedReader(
				new InputStreamReader(dataIn));
		isRunning = true;
		String acceloData = null;
		while ( isRunning )
		{
			if ( buffIn == null )
			{
				System.out.println("buff in is empty");
				isRunning = false;
			} else
			{
				System.out.println("read line");
				acceloData = buffIn.readLine();
				sendToRobot(acceloData);
			}

		}
		return acceloData;
	}

	private void sendToRobot( String acceloData )
	{
		System.out.println("send to robot method");
		CursorRobot cr = new CursorRobot(acceloData);
		System.out.println("start the robot");
		cr.start();
	}

	private void closeTheStream( )
	{
		try
		{
			dataIn.close();
		} catch ( IOException e )
		{
			e.printStackTrace();
		}
	}
}// end of Class