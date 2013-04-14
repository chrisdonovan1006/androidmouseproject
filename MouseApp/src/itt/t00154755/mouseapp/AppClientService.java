// package

package itt.t00154755.mouseapp;

// imports
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Christopher Donovan
 * 
 *         This is the bluetooth service class it first checks to ensure that bluetooth
 * 
 * 
 * 
 *         {@link http
 *         ://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android-and-a-desktop/} {@link http
 *         ://developer.android.com/guide/topics/connectivity/bluetooth.html#EnablingDiscoverability}
 */
public class AppClientService
{

	private static final String TAG = "App Client Service";
	private static final boolean D = true;

	private final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// 00000003-0000-1000-8000-00805F9B34FB - RFCOMM
	// 00001101-0000-1000-8000-00805F9B34FB - SPP
	// 427e7ad0-9c78-11e2-9e96-0800200c9a66 - generated UUID

	// bluetooth adapter Object
	private final BluetoothAdapter btAdapter;
	private final Handler appHandler;
	private ConnectThread connectThread;
	private ConnectedThread connectedThread;
	private int state;

	// used to check if the device is available
	public static final int NONE = 0;
	public static final int LISTEN = 1;
	public static final int CONNECTING = 2;
	public static final int CONNECTED = 3;

	// String value
	public String acceloData;


	/**
	 * 
	 */
	public AppClientService( Context context, Handler handler )
	{
		if ( D )
			Log.i(TAG, "+++ APP CLIENT SERVICE +++");
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		state = NONE;

		appHandler = handler;
	}


	/**
	 * @return the acceloData
	 */
	public String getAcceloData()
	{
		return acceloData;
	}


	/**
	 * @param acceloData
	 *        the acceloData to set
	 */
	public void setAcceloData( String acceloData )
	{
		this.acceloData = acceloData;
	}


	private synchronized void setState( int stateIn )
	{
		if ( D )
			Log.i(TAG, "+++ SET STATE +++ --> \n" + state + " : " + stateIn);
		state = stateIn;
	}


	public synchronized int getState()
	{
		if ( D )
			Log.i(TAG, "+++ GET STATE +++");
		return state;
	}


	public synchronized void start()
	{
		if ( D )
			Log.i(TAG, "+++ START METHOD +++");

		// cancel any threads trying to connect
		if ( connectThread != null )
		{
			connectThread.cancel();
			connectThread = null;
		}
		// cancel any thread currently running
		if ( connectedThread != null )
		{
			connectedThread.cancel();
			connectThread = null;
		}
		Log.i(TAG, "+++ START METHOD +++");
		setState(LISTEN);
	}


	public synchronized void connect( BluetoothDevice device )
	{
		if ( D )
			Log.i(TAG, "+++ CONNECT METHOD +++");
		if ( D )
			Log.i(TAG, "connecting to: " + device);

		if ( getState() == CONNECTING )
		{
			// cancel any thread currently running
			if ( connectThread != null )
			{
				connectThread.cancel();
				connectThread = null;
			}

		}

		// cancel any thread currently running
		if ( connectedThread != null )
		{
			connectedThread.cancel();
			connectThread = null;
		}

		// start the thread that will connect to PC
		connectThread = new ConnectThread(device);
		connectThread.start();

		setState(CONNECTING);
	}


	public synchronized void connected( BluetoothSocket socket, BluetoothDevice device )
	{
		if ( D )
			Log.i(TAG, "+++ CONNECTED METHOD +++");

		String out = getAcceloData();

		// cancel any thread currently running
		if ( connectThread != null )
		{
			connectThread.cancel();
			connectThread = null;
		}

		// cancel any thread currently running
		if ( connectedThread != null )
		{
			connectedThread.cancel();
			connectThread = null;
		}
		connectedThread = new ConnectedThread(socket, out);
		connectedThread.start();

		// message back to UI
		Message message = appHandler.obtainMessage(App.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(App.DEVICE_NAME, device.getName());
		message.setData(bundle);
		appHandler.sendMessage(message);

		setState(CONNECTED);
	}


	public synchronized void stop()
	{
		if ( D )
			Log.i(TAG, "+++ STOP ALL THREADS +++");
		if ( connectThread != null )
		{
			connectThread.cancel();
			connectThread = null;
		}

		if ( connectedThread != null )
		{
			connectedThread.cancel();
			connectThread = null;
		}

		setState(NONE);
	}


	public void write( String out )
	{
		if ( D )
			Log.i(TAG, "+++ WRITE STRINGS +++");

		setAcceloData(out);
	}


	public void write( int out )
	{
		if ( D )
			Log.i(TAG, "+++ WRITE STRINGS +++");
		if (out == 1)
		{
			setAcceloData("right");
		}else
			if (out == 2)
			{
				setAcceloData("left");
			}
			else if (out == 3)
			{
				setAcceloData("wheel");
			}
	}


	private void connectionFailed()
	{
		if ( D )
			Log.i(TAG, "+++ CONNECTION FAILED +++");

		setState(LISTEN);

		Message message = appHandler.obtainMessage(App.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(App.TOAST, "the connection was lost");
		message.setData(bundle);
		appHandler.sendMessage(message);

		// restart the connecting process
		AppClientService.this.start();
	}

	private class ConnectThread extends Thread
	{

		private BluetoothDevice device;
		private final BluetoothSocket socket;


		public ConnectThread( BluetoothDevice deviceIn )
		{
			if ( D )
				Log.e(TAG, "+++ CONNECT THREAD +++");

			device = deviceIn;
			BluetoothSocket temp = null;
			try
			{
				if ( D )
					Log.d(TAG, "+++ CONNECTING TO SERVER +++");
				Log.d(TAG, "getting remote device");
				

				temp = device.createRfcommSocketToServiceRecord(SPP_UUID);

			}
			catch ( Exception e )
			{
				Log.i(TAG, "Error connecting to device", e);
			}
			socket = temp;
		}


		public void run()
		{
			if ( D )
				Log.e(TAG, "+++ ABOUT TO CONNECT +++");
			// Always cancel discovery because it will slow down a connection
			btAdapter.cancelDiscovery();
			try
			{
				// This is a blocking call and will only return on a
				// successful connection or an exception
				socket.connect();
			}
			catch ( IOException e )
			{

				try
				{
					socket.close();
				}
				catch ( IOException e1 )
				{

					Log.e(TAG, "Error closing the socket connect thread", e1);
				}
				connectionFailed();
				return;
			}

			if ( D )
				Log.e(TAG, "+++ CONNECTED +++");

			// start the connected thread
			connected(socket, device);
		}


		public void cancel()
		{
			try
			{
				socket.close();
			}
			catch ( IOException e )
			{
				Log.e(TAG, "Error closing the socket close() connect thread", e);
			}

		}

	}

	private class ConnectedThread extends Thread
	{

		private final BluetoothSocket socket;
		private final OutputStream outputStream;
		private String out;


		public ConnectedThread( BluetoothSocket socketIn, final String out )
		{
			if ( D )
				Log.i(TAG, "+++ CONNECTED THREAD+++");

			socket = socketIn;
			OutputStream tempOut = null;

			try
			{
				tempOut = socket.getOutputStream();
			}
			catch ( IOException e )
			{
				Log.e(TAG, "temp socket not created", e);
			}

			outputStream = tempOut;
			this.out = out;

		}


		public void run()
		{
			if ( D )
				Log.i(TAG, "+++ WRITE STRING OUT +++");
			while ( true )
			{
				try
				{
					outputStream.write(out.getBytes());
				}
				catch ( IOException e )
				{
					Log.e(TAG, "Error trying to write to the server", e);
				}
			}

		}


		public void cancel()
		{
			try
			{
				socket.close();
			}
			catch ( IOException e )
			{
				Log.e(TAG, "Error closing the socket close() connected thread", e);
			}
		}
	}

}// end of the class