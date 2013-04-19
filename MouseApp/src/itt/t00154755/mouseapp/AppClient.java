package itt.t00154755.mouseapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Build;
import android.util.Log;

/**
 * 
 * @author Christopher
 * 
 *         {@link} http://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android-
 *         and-a-desktop/
 */
@TargetApi ( Build.VERSION_CODES.ICE_CREAM_SANDWICH )
public class AppClient
{

	private static final String TAG = "App Client";
	private BluetoothAdapter btAdapter = null;
	private ClientCommsThread clientCommThread = null;

	private final int WAITING = 0;
	private final int CONNECTED = 1;
	// private final int RUNNING = 2;

	private int state;


	public AppClient()
	{
		// get the default adapter
		Log.d(TAG, "getting default adapter");
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		state = WAITING;
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
		BluetoothSocket btSocket = null;

		Log.d(TAG, "getting local device");
		btDevice = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");
		btAdapter.cancelDiscovery();

		Log.d(TAG, "connecting to server");
		try
		{
			btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString("27012f0c-68af-4fbf-8dbe-6bbaf7aa432a"));
		}
		catch ( IOException e )
		{
			Log.e(TAG, "error creating the RFCOMM connection");
		}

		try
		{
			Log.d(TAG, "about to connect");
			while ( state == WAITING )
			{
				btSocket.connect();
				if ( btSocket.isConnected() )
				{
					setState(CONNECTED);
				}
				Log.d(TAG, "Connected!");
			}

		}
		catch ( IOException e )
		{
			Log.e(TAG, "error connecting to the server\n" + e.getLocalizedMessage() + "\n" + e.getCause());
			e.printStackTrace();
			cancel(btSocket);
		}

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
	 * @return the state
	 */
	public synchronized int getState()
	{
		return state;
	}


	/**
	 * @param state
	 *        the state to set
	 */
	public synchronized void setState( int state )
	{
		this.state = state;
	}


	/**
	 * @param btSocket
	 * @throws IOException
	 */
	public synchronized void createClientCommThread( BluetoothSocket socket ) throws IOException
	{
		clientCommThread = new ClientCommsThread(socket);
		clientCommThread.start();
	}


	/**
	 * 
	 * @param acceloData
	 *        the data packet retrieved from the Accelerometer Updater
	 */
	public void sendDataFromTheAccToTheAppClient( String acceloData )
	{
		clientCommThread.writeStringToAppClient(acceloData);
	}


	/**
	 * cancel the currently running thread
	 */
	public void cancel( BluetoothSocket socket )
	{
		if ( socket != null )
		{
			try
			{
				socket.close();
				socket = null;
			}
			catch ( IOException e )
			{

				e.printStackTrace();
			}
		}

		if ( clientCommThread != null )
		{
			clientCommThread.cancel();
			clientCommThread = null;
		}

		setState(WAITING);
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
		private boolean isConnected = false;


		@SuppressLint ( "NewApi" )
		public ClientCommsThread( BluetoothSocket btSocket )
		{
			Log.i(TAG, "+++ SET UP THE CLIENT COMM THREAD +++");
			threadSocket = btSocket;
			if ( threadSocket.isConnected() )
			{
				isConnected = true;
			}

		}


		public void writeStringToAppClient( String acceloData )
		{
			this.acceloData = acceloData;

			if ( acceloData == null || acceloData.length() > 0 )
			{
				Log.i(TAG, acceloData);
				String defaultData = "1,0,0";
				setAcceloData(defaultData);
			}
			else
			{
				Log.i(TAG, acceloData);
				setAcceloData(acceloData);
			}
			DataOutputStream outStream = null;
			try
			{
				outStream = new DataOutputStream(threadSocket.getOutputStream());
			}
			catch ( IOException e1 )
			{
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try
			{
				outStream.write(getAcceloData().getBytes());
				outStream.flush();
			}
			catch ( IOException e )
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


		@Override
		public void run()
		{
			while ( isConnected )
			{
				continue;
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
	}

}
