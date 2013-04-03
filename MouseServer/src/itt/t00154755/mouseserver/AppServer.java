// package

package itt.t00154755.mouseserver;

// imports
import java.io.IOException;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

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
	private final UUID PRIVATE_UUID = new UUID("427e7ad09c7811e29e960800200c9a66", false);
	//private final UUID SPP_UUID = new UUID("1101", false);
	// 00000003-0000-1000-8000-00805F9B34FB - RFCOMM
	// 00001101-0000-1000-8000-00805F9B34FB - SPP
	private final String connString = "btspp://localhost:" + PRIVATE_UUID.toString() + ";name=Java_Server";


	public AppServer()
	{
		System.out.println("app server constructor");
	}


	@Override
	public void run()
	{
		createRFCOMMConnectionUsingPrivateUUID();
	}


	private void createRFCOMMConnectionUsingPrivateUUID()
	{
		LocalDevice pcDevice = null;
		StreamConnectionNotifier connNotifier = null;
		StreamConnection connection = null;
		
			try
			{
				pcDevice = LocalDevice.getLocalDevice();
				pcDevice.setDiscoverable(DiscoveryAgent.GIAC);

				connNotifier = (StreamConnectionNotifier ) Connector.open(connString);

				System.out.println(TAG + "...Server Running on : \n ");
				System.out.println("Local Device Name: " + pcDevice.getFriendlyName());
				System.out.println("Local Device MAC: " + pcDevice.getBluetoothAddress());				
			}
			catch ( BluetoothStateException e )
			{
				// print the error stack
				e.printStackTrace();
				e.getCause();
				System.out.println(TAG + "shutting down the server 1");
				System.exit(-1);
			}
			catch ( IOException e )
			{
				// print the error stack
				e.printStackTrace();
				e.getCause();
				System.out.println(TAG + "shutting down the server 2");
				System.exit(-1);
			}
			
			while ( true )
			{
				try
				{
					System.out.println("...waiting for the client...");
					connection = connNotifier.acceptAndOpen();
				}
				catch ( IOException e )
				{
					// print the error stack
					e.printStackTrace();
					e.getCause();
					System.out.println(TAG + "shutting down the server 3");
					System.exit(-1);
				}
				// if a client is accepted
				if (connection != null)
				{
					
					// start a new Thread that will handle incoming traffic
    				Thread clientThread = new Thread(new ServerCommsThread(connection));
    				clientThread.start();
    				
					// display the details
					RemoteDevice reDevice = null;
					try
					{
						reDevice = RemoteDevice.getRemoteDevice(connection);
						
						System.out.println(TAG + "...Server is Connected to: \n" +
								reDevice.getBluetoothAddress() + "\n" + reDevice.getFriendlyName(false));
					}
					catch ( IOException e )
					{
						// print the error stack
						e.printStackTrace();
						e.getCause();
						System.out.println(TAG + "shutting down the server 4");
						System.exit(-1);
					}
					
    				// close the connection
    				try
					{
						connection.close();
					}
					catch ( IOException e )
					{
						// print the error stack
						e.printStackTrace();
						e.getCause();
						System.out.println(TAG + "shutting down the server 5");
						System.exit(-1);
					}
				}

				
			}
		
	}
}// end of Class
