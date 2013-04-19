// package

package itt.t00154755.mouseserver;

// imports
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

// import javax.bluetooth.UUID;

/**
 * 
 * @author Christopher Donovan
 *         <p>
 *         This class creates a stream connection using the Bluecove API. The stream listens for a RFComm client once a client is found the stream connection is
 *         closed, to ensure that only one client is connected.
 *         <p>
 *         {@link http://docs.oracle.com/javase/tutorial/networking/index.html}
 *         <p>
 *         {@link http
 *         ://docs.oracle.com/javase/tutorial/essential/exceptions/index.html}
 * 
 */
public class AppServer implements Runnable
{

	// string name of class
	private final static String TAG = "App Server";
	private LocalDevice pcDevice = null;
	private int errorNum;
	private final String connString = "btspp://localhost:27012f0c68af4fbf8dbe6bbaf7aa432a;name=Java_Server;authenticate=false;encrypt=false;master=false";


	public AppServer()
	{
		try
		{
			pcDevice = LocalDevice.getLocalDevice();
			pcDevice.setDiscoverable(DiscoveryAgent.GIAC);
		}
		catch ( BluetoothStateException e )
		{
			errorNum = 1;
			printOutExceptionDetails(e, errorNum);
		}
		System.out.println("\napp server constructor");
	}


	@Override
	public void run()
	{
		createServerSideListener();
	}


	/**
	 * 
	 */
	private void createServerSideListener()
	{
		StreamConnectionNotifier connNotifier = null;
		StreamConnection connection = null;

		try
		{
			connNotifier = (StreamConnectionNotifier ) Connector.open(connString);

			System.out.println(TAG + "...Server Running on : \n ");
			System.out.println("Local Device Name: " + pcDevice.getFriendlyName());
			System.out.println("Local Device MAC: " + pcDevice.getBluetoothAddress());
		}
		catch ( BluetoothStateException e )
		{
			errorNum = 2;
			printOutExceptionDetails(e, errorNum);
		}
		catch ( IOException e )
		{
			errorNum = 3;
			printOutExceptionDetails(e, errorNum);
		}

		while ( true )
		{
			try
			{
				System.out.println("\n...waiting for the client...");
				connection = connNotifier.acceptAndOpen();

			}
			catch ( IOException e )
			{
				errorNum = 4;
				printOutExceptionDetails(e, errorNum);
			}
			// if a client is accepted

			
			// display the details
			RemoteDevice reDevice = null;
			try
			{
				reDevice = RemoteDevice.getRemoteDevice(connection);

				System.out.println(TAG + "...Server is Connected to: \n" + reDevice.getBluetoothAddress() + "\n" + reDevice.getFriendlyName(false));
			}
			catch ( IOException e )
			{
				errorNum = 6;
				printOutExceptionDetails(e, errorNum);
			}

			// start a new Thread that will handle incoming traffic
			Thread serverThread = new Thread(new ServerCommsThread(connection));
			serverThread.start();
		}
	}


	/**
	 * @param e
	 * @param errorNum
	 */
	private void printOutExceptionDetails( IOException e, int errorNum )
	{
		// print the error stack
		e.printStackTrace();
		e.getCause();
		System.out.println(TAG + "\nshutting down the server " + errorNum);
		System.exit(-1);
	}
}// end of Class
