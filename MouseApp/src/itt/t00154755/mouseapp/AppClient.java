package itt.t00154755.mouseapp;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * 
 * @author Christopher
 * 
 *         {@link} http://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android-
 *         and-a-desktop/
 */
public class AppClient
{

	private static final String TAG = "App Client";
	private BluetoothAdapter btAdapter = null;
	private BluetoothSocket btSocket = null;
	private ClientCommsThread clientCommThread = null;
	private String acceloData = null;


	public AppClient()
	{
		// get the default adapter
		Log.d(TAG, "getting default adapter");
		btAdapter = BluetoothAdapter.getDefaultAdapter();
	}

	public void start()
	{
		connectToSever();
	}

	/**
	 * this method tries to connect to the server,
	 * it either create a connection or not
	 */
	private void connectToSever()
	{
		BluetoothDevice btDevice = null;

		Log.d(TAG, "getting local device");
		btDevice = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");
		Log.d(TAG, "connecting to server");
		try
		{
			btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("27012f0c-68af-4fbf-8dbe-6bbaf7aa432a"));
		}
		catch ( IOException e )
		{
			Log.e(TAG, "error creating the RFCOMM connection");
		}
		Log.d(TAG, "about to connect");

		btAdapter.cancelDiscovery();
		try
		{
			btSocket.connect();
		}
		catch ( IOException e )
		{
			Log.e(TAG, "error connecting to the server");
		}
		Log.d(TAG, "Connected!");

		try
		{
			createClientCommThread(btSocket);
		}
		catch ( IOException e )
		{
			Log.e(TAG, "error creating client comm thread");
		}
	}


	/**
	 * @param btSocket
	 * @throws IOException
	 */
	public synchronized void createClientCommThread( BluetoothSocket socket ) throws IOException
	{
		String accData = getAcceloData();
		clientCommThread = new ClientCommsThread(socket, accData);
		clientCommThread.start();

	}
	
	/**
	 * 
	 * @param acceloData
	 *        the data packet retrieved from the Accelerometer Updater
	 */
	public void sendDataFromTheAccToTheAppClient( String acceloData )
	{
		this.acceloData = acceloData;
		setAcceloData(acceloData);

	}


	/**
	 * @return the acceloData
	 */
	public synchronized String getAcceloData()
	{
		return acceloData;
	}


	/**
	 * @param acceloData
	 *        the acceloData to set
	 */
	public synchronized void setAcceloData( String acceloData )
	{
		this.acceloData = acceloData;
	}


	/**
	 * cancel the currently running thread
	 */
	public void cancel()
	{
		if ( clientCommThread != null )
		{
			clientCommThread.cancel();
			clientCommThread = null;
		}

	}

	/**
	 * 
	 * @author Christopher
	 * 
	 */
	private class ClientCommsThread extends Thread
	{

		private static final String TAG = "Client Comms Thread";
		private BluetoothSocket threadSocket = null;
		private String acceloData = null;


		public ClientCommsThread( BluetoothSocket btSocket, String acceloData )
		{
			Log.i(TAG, "+++ SET UP THE CLIENT COMM THREAD +++");
			threadSocket = btSocket;
			this.acceloData = acceloData;
		}


		@Override
		public void run()
		{

			while ( true )
			{
				try
				{
					//System.out.println(acceloData);
					if (acceloData != null)
					{
						threadSocket.getOutputStream().write(acceloData.getBytes());
					}
					
				}
				catch ( IOException e )
				{
					Log.e(TAG, "error writing to the server");
				}
			}

		}


		public void cancel()
		{
			try
			{
				threadSocket.close();
			}
			catch ( IOException e )
			{
				Log.e(TAG, "Cancelling the Client Comm Thread");
				Log.e(TAG, "error closing the socket");
			}

		}
	}

}
