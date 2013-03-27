// package
package itt.t00154755.mouseserver;

// imports
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
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
	private final String TAG = "App Server";
	private final LocalDevice pcDevice;
	private final String connString = "btspp://localhost:"
			+ "27012f0c68af4fbf8dbe6bbaf7aa432a;name=Java Server;"
			+ "authenticate=false;encrypt=false;master=false";

	public AppServer() throws BluetoothStateException
	{
		pcDevice = LocalDevice.getLocalDevice();
	}

	public void run() 
	{
		StreamConnectionNotifier connectionNotifier = null;
		try 
		{
			connectionNotifier = (StreamConnectionNotifier) Connector.open(connString);
		} 
		catch (IOException e) 
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		}
		
		System.out.println(TAG + "...Server Running on : \n " 
		+ pcDevice.getBluetoothAddress()
		+ "\n" + pcDevice.getFriendlyName());
		
		StreamConnection streamConnection = null;
		
		try
		{
			streamConnection = connectionNotifier.acceptAndOpen();
		} 
		catch (IOException e) 
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		}
		InputStream dataIn = null;
		try 
		{
			dataIn = new DataInputStream(streamConnection.openInputStream());
		} 
		catch (IOException e)
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		}
		RemoteDevice reDevice;
		try {
			reDevice = RemoteDevice.getRemoteDevice(streamConnection);
			
			System.out.println(TAG + "...Server is Connected to: \n " 
					+ reDevice.getBluetoothAddress()
					+ "\n" + reDevice.getFriendlyName(true));
			
		} catch (IOException e) {
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);
		};
		
		
		ServerCommsThread sct = new ServerCommsThread(dataIn);
		sct.start();
	}
}// end of Class
