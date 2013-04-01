// package

package itt.t00154755.mouseapp;

// imports
import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * 
 * @author Christopher Donovan
 *  {@link http
 *         ://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android
 *         -and-a-desktop/} 
 *  {@link http
 *         ://developer.android.com/guide/topics/connectivity
 *         /bluetooth.html#EnablingDiscoverability}
 */
public class AppClient
{

	private static final String TAG = "App Client";
	// bluetooth adapter Object
	private BluetoothAdapter btAdapter;
	// used to check if the device is available
	private boolean available;
	// String value
	public String acceloData;
	// bluetooth socket
	private BluetoothSocket socket;
	// used to count the number of null packets being sent
	public int nullPacketsOut;

	/**
	 * 
	 */
	public AppClient ( )
	{

		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public void connectToServer()
	{

		try
		{
			Log.d(TAG, "getting local device");
			// remote MAC here:
			BluetoothDevice device = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");
			Log.d(TAG, "connecting to service");
			socket = device.createRfcommSocketToServiceRecord(UUID.fromString("27012f0c-68af-4fbf-8dbe-6bbaf7aa432a"));
			Log.d(TAG, "about to connect");

			btAdapter.cancelDiscovery();
			socket.connect();
			Log.d(TAG, "Connected!");
			
			setAvailable(true);

		}
		catch ( Exception e )
		{
			Log.e(TAG, "Error connecting to device", e);
		}
	}

	/**
	 * @param socket
	 * @throws IOException
	 */
	public void writeOutToTheServer( String acceloData ) throws IOException
	{

		ClientCommsThread cct = new ClientCommsThread(socket, acceloData);
		cct.start();
	}

	public boolean isAvailable()
	{

		return available;
	}

	/**
	 * @param available the available to set
	 */
	public void setAvailable( boolean available )
	{
	
		this.available = available;
	}

}// end of the class