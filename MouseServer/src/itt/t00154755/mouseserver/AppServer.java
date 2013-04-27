// package

package itt.t00154755.mouseserver;

// imports
import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * 
 * This class creates a stream connection using the Bluecove API. The stream listens for a RFComm client once a client is found the stream connection is
 * closed, to ensure that only one client is connected. The value passed form the connection string are used to created the service record on the client
 * side.
 * <p>
 * http://bluecove.org/bluecove/apidocs/index.html?javax/bluetooth/L2CAPConnectionNotifier.html
 * <p>
 * {@link http://docs.oracle.com/javase/tutorial/networking/index.html}
 * <p>
 * {@link http://docs.oracle.com/javase/tutorial/essential/exceptions/index.html}
 * <p>
 * {@link http://www.miniware.net/mobile/articles/viewarticle.php?id=22}
 * 
 * @author Christopher Donovan
 * @since 26/04/2013
 * @version 4.06
 * 
 */
public class AppServer extends AppServerUtils implements Runnable
{

	// string name of class
	private final static String TAG = "App Server";
	private LocalDevice pcDevice;
	private int exceptionCode;

	/*
	 * This sting is used to create a listening connection stream in order for the remote device to connect
	 * it must use the same UUID.
	 * this is the UUID that listen for the connection
	 * btspp://localhost:5a17e500ad3a11e29e960800200c9a66;
	 * 
	 * this string is used to create the service record
	 * i have turned all of the values to false as the data is not critical
	 * name=Java_Server;authenticate=false;encrypt=false;master=false
	 * 
	 * http://bluecove.org/bluecove/apidocs/index.html?javax/bluetooth/L2CAPConnectionNotifier.html
	 */

	private final String CONNECTION_STRING = "btspp://localhost:5a17e500ad3a11e29e960800200c9a66;" + "name=Java_Server;authenticate=false;encrypt=false;master=false";


	public AppServer()
	{
		try
		{
			// the local device will be the PC / Laptop on
			// which the server is running
			pcDevice = LocalDevice.getLocalDevice();
			pcDevice.setDiscoverable(DiscoveryAgent.GIAC);
		}
		catch ( BluetoothStateException e )
		{

			exceptionCode = 1;
			printOutExceptionDetails(TAG, e, exceptionCode);
		}
		System.out.println("\napp server constructor");
	}


	@Override
	public void run()
	{
		// call to create a listening server
		createServerSideListener();
	}


	/*
	 * Creates a
	 */
	private void createServerSideListener()
	{
		StreamConnectionNotifier connNotifier = null;
		StreamConnection connection = null;

		try
		{
			// open a Connector using the CONNECTION_STRING
			connNotifier = (StreamConnectionNotifier ) Connector.open(CONNECTION_STRING);
			// display the details of the local device
			System.out.println(TAG + "...Server Running on : \n ");
			System.out.println("Local Device Name: " + pcDevice.getFriendlyName());
			System.out.println("Local Device MAC: " + pcDevice.getBluetoothAddress());
		}
		catch ( BluetoothStateException e )
		{
			exceptionCode = 2;
			printOutExceptionDetails(TAG, e, exceptionCode);
		}
		catch ( IOException e )
		{
			exceptionCode = 3;
			printOutExceptionDetails(TAG, e, exceptionCode);
		}

		try
		{
			System.out.println("\n...waiting for the client...");
			connection = connNotifier.acceptAndOpen();

		}
		catch ( IOException e )
		{
			exceptionCode = 4;
			printOutExceptionDetails(TAG, e, exceptionCode);
		}
		// if a client is accepted
		InputStream inStream = null;

		try
		{
			inStream = connection.openInputStream();
		}
		catch ( IOException e )
		{
			System.out.println("Error creating the Input stream");
		}
		// display the details
		RemoteDevice reDevice = null;
		try
		{
			reDevice = RemoteDevice.getRemoteDevice(connection);

			System.out.println(TAG + "...Server is Connected to: \n"
							   + reDevice.getBluetoothAddress()
							   + "\n"
							   + reDevice.getFriendlyName(false));
		}
		catch ( IOException e )
		{
			exceptionCode = 6;
			printOutExceptionDetails(TAG, e, exceptionCode);
		}
		// pass the inputStream
		startServerCommsThread(inStream);
	}


	/**
	 * @param in
	 *        the incoming input stream
	 */
	private void startServerCommsThread( InputStream inStream )
	{
		// create the class reference
		AppStreamReader appStreamReader = new AppStreamReader();
		// create a new Thread that will handle incoming traffic
		Thread serverThread = new Thread(appStreamReader);
		// start the thread
		serverThread.start();

		while ( true )
		{
			appStreamReader.setStream(inStream);
		}
	}
}// end of Class
