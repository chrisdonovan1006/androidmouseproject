// package
package itt.t00154755.mouseserver;

// imports
import java.io.IOException;
import java.util.ArrayList;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

/**
 * 
 * @author Christopher Donovan
 *         <p>
 *         This class creates a stream connection using the Bluecove API. The
 *         stream listens for a RFComm client once a client is found the stream
 *         connection is closed, to ensure that only one client is connected.
 *         <p>
 *         {@link http://docs.oracle.com/javase/tutorial/networking/index.html}
 *         <p>
 *         {@link http
 *         ://docs.oracle.com/javase/tutorial/essential/exceptions/index.html}
 * 
 */
public class AppServer extends Thread
{
	// string name of class
	private final String					TAG			= "App Server";
	private final String					connString	= "btspp://localhost:"
																+ "27012f0c68af4fbf8dbe6bbaf7aa432a;name=Java Server;"
																+ "authenticate=false;encrypt=false;master=false";
	StreamConnectionNotifier				connectionNotifier;
	StreamConnection						streamConnection;
	private ArrayList<ServerCommsThread>	clients;

	public AppServer ( ) throws BluetoothStateException
	{
		System.out.println("app server constructor");
		clients = new ArrayList<ServerCommsThread>();

		initLocalDevice();
		createConnection();
		addClient();

	}

	private void initLocalDevice( )
	{
		try
		{
			LocalDevice pcDevice = LocalDevice.getLocalDevice();
			pcDevice.setDiscoverable(DiscoveryAgent.GIAC);
			
			System.out.println(TAG + "...Server Running on : \n ");
			System.out.println("Local Device Name: "
					+ pcDevice.getFriendlyName());
			System.out.println("Local Device MAC: "
					+ pcDevice.getBluetoothAddress());
		} catch ( BluetoothStateException bse )
		{
			//
			bse.printStackTrace();
		}

	}

	private void createConnection( )
	{
		try
		{
			connectionNotifier = (StreamConnectionNotifier ) Connector
					.open(connString);
		} catch ( IOException e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		}

	}

	private void addClient( )
	{
		try
		{
			streamConnection = connectionNotifier.acceptAndOpen();
		} catch ( IOException e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		}

		ServerCommsThread client = new ServerCommsThread(streamConnection);
		// create client handler
		clients.add(client);
		client.start();
	}
}// end of Class
