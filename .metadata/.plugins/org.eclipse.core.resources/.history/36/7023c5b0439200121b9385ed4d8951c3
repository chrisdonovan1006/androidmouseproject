// package
package itt.t00154755.mouseapp;
// imports
import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * 
 * @author Christopher Donovan
 * <p>
 * {@link http://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android-and-a-desktop/}
 * {@link http://developer.android.com/guide/topics/connectivity/bluetooth.html#EnablingDiscoverability}
 */
public class AppClient
{
	private final String TAG = "Android Phone";
	private final UUID SPP_UUID = UUID.fromString("2fc42b80-923a-11e2-9e96-0800200c9a66");
	private final String ADDRESS = "00:15:83:3D:0A:57";
	private BluetoothAdapter btAdapter;
	private String acceloData;
	private AppUtils cUtils = new AppUtils();
	private BluetoothSocket btSocket;
	private BluetoothDevice btDevice;

	public AppClient() 
	{
		this.btAdapter = BluetoothAdapter.getDefaultAdapter();
		cUtils.info(TAG, "in the app client constructor");
	}
	
	public void connectToServer() 
	{
		// MY_UUID is the app's UUID string, also used by the server code
	    try {
	        while (true)
	        {
	        	cUtils.info(TAG, "getting local device");
	        	// remote MAC here:
	        	btDevice = btAdapter.getRemoteDevice(ADDRESS);
				
	        	cUtils.info(TAG, "create the RFComm record");
	        	btSocket = btDevice.createRfcommSocketToServiceRecord(SPP_UUID);
		
	        	// Cancel discovery because it will slow down the connection
	        	cUtils.info(TAG, "cancel discovery");
	        	btAdapter.cancelDiscovery();
	        
	        	cUtils.info(TAG, "app client connect");
	        	btSocket.connect();
				
	        	// Do work to manage the connection (in a separate thread)
	        	ClientCommsThread cct = new ClientCommsThread(btSocket, acceloData);
	        	cct.start();
	        }
		} 
	    catch (IOException ioe) 
	    {
	        // print the error stack
			cUtils.error(TAG, "failed to connect to the server ", ioe);
		}    
	}

	public void getAccelerometerDataString(String acceloData) 
	{
		this.acceloData = acceloData;
	}
	
	private class ClientCommsThread extends Thread 
	{
		private static final String TAG = "Client Comms Thread";
		private BluetoothSocket socket;
		private String acceloData;

		public ClientCommsThread(BluetoothSocket socket, String acceloData) 
		{
			System.out.println(TAG);
			this.socket = socket;
			this.acceloData = acceloData;
		}

		@Override
		public void run() 
		{
			try 
			{
				while (true) 
				{
					socket.getOutputStream().write(acceloData.getBytes());
				}
			} 
			catch (Exception e) 
			{
				// print the error stack
				cUtils.error(TAG, "failed to write to the server ", e);
			} 
			finally 
			{
				try 
				{
					if (socket != null) 
					{
						socket.close();
					}
				} 
				catch (IOException e) 
				{
					// print the error stack
					cUtils.error(TAG, "failed to close the socket ", e);
				}
			}// end of finally
		}// outer try
	}// end of Client Comms Thread
}// end of the class