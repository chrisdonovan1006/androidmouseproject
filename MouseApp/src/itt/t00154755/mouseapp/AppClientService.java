// package

package itt.t00154755.mouseapp;

// imports
import java.io.IOException;
import java.io.InputStream;
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
 * @author Christopher Donovan {@link http
 *         ://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android
 *         -and-a-desktop/} {@link http
 *         ://developer.android.com/guide/topics/connectivity
 *         /bluetooth.html#EnablingDiscoverability}
 */
public class AppClientService
{

	private static final String TAG = "App Client";
	private final UUID uuid = UUID.fromString("04c603b0-0001-0008-0000-0805f9b34fb");
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
	public static final byte[] VOL_UP = null;
	public static final byte[] VOL_DOWN = null;

	// String value
	public String acceloData;

	// used to count the number of null packets being sent
	public int nullPacketsOut;


	/**
	 * 
	 */
	public AppClientService( Context context, Handler handler )
	{
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		state = NONE;

		appHandler = handler;
	}


	private synchronized void setState( int state )
	{
		this.state = state;
	}


	public synchronized int getState()
	{
		return state;
	}


	public synchronized void start()
	{
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

		setState(LISTEN);
	}


	public synchronized void connect( BluetoothDevice device )
	{
		if ( state == CONNECTING )
		{
			if ( connectThread != null )
			{
				connectThread.cancel();
				connectThread = null;
			}
		}

		if ( connectedThread != null )
		{
			connectedThread.cancel();
			connectThread = null;
		}

		connectThread = new ConnectThread(device);
		connectThread.start();
		setState(CONNECTING);
	}


	public synchronized void connected( BluetoothSocket socket, BluetoothDevice device )
	{
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

		connectedThread = new ConnectedThread(socket);
		connectedThread.start();

		Message message = appHandler.obtainMessage(App.MESSAGE_DEVICE_NAME);
		Bundle bundle = new Bundle();
		bundle.putString(App.DEVICE_NAME, device.getName());
		message.setData(bundle);
		appHandler.sendMessage(message);

		setState(CONNECTED);
	}


	public synchronized void stop()
	{
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


	public void write( byte[] out )
	{
		ConnectedThread ct;

		synchronized ( this )
		{
			if ( state != CONNECTED )
				return;

			ct = connectedThread;
		}

		ct.write(out);
	}


	public void write( int out )
	{
		ConnectedThread ct;

		synchronized ( this )
		{
			if ( state != CONNECTED )
				return;

			ct = connectedThread;
		}

		ct.write(out);
	}


	private void connectionLost()
	{
		setState(LISTEN);

		Message message = appHandler.obtainMessage(App.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(App.TOAST, "unable to connect to the server");
		message.setData(bundle);
		appHandler.sendMessage(message);
	}


	private void connectionFailed()
	{
		setState(LISTEN);

		Message message = appHandler.obtainMessage(App.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(App.TOAST, "the connection was lost");
		message.setData(bundle);
		appHandler.sendMessage(message);
	}

	private class ConnectThread extends Thread
	{

		private final BluetoothDevice device;

		private final BluetoothSocket socket;


		public ConnectThread( BluetoothDevice device )
		{

			this.device = device;
			BluetoothSocket temp = null;
			try
			{
				// Log.d(TAG, "getting local device");
				// remote MAC here:
				// device = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");
				Log.d(TAG, "connecting to service");
				temp = device.createRfcommSocketToServiceRecord(uuid);
			}
			catch ( Exception e )
			{
				Log.e(TAG, "Error connecting to device", e);
			}
			socket = temp;
		}


		public void run()
		{
			Log.d(TAG, "about to connect");

			btAdapter.cancelDiscovery();
			try
			{
				socket.connect();
			}
			catch ( IOException e )
			{
				connectionFailed();
				try
				{
					socket.close();
				}
				catch ( IOException e1 )
				{
					Log.e(TAG, "Error closing the socket connect thread", e1);
				}
				AppClientService.this.start();
				return;
			}
			Log.d(TAG, "Connected!");

			synchronized ( AppClientService.this )
			{
				connectThread = null;
			}

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
		private final InputStream inputStream;
		private final OutputStream outputStream;


		public ConnectedThread( BluetoothSocket socket )
		{
			this.socket = socket;
			InputStream tempIn = null;
			OutputStream tempOut = null;

			try
			{
				tempIn = socket.getInputStream();
				tempOut = socket.getOutputStream();
			}
			catch ( IOException e )
			{
				Log.e(TAG, "temp socket not created", e);
			}

			inputStream = tempIn;
			outputStream = tempOut;
		}


		public void run()
		{
			byte[] buffer = new byte[1024];

			while ( true )
			{
				try
				{
					int bytes = inputStream.read();

					appHandler.obtainMessage(App.READ, bytes, -1, buffer).sendToTarget();
				}
				catch ( IOException e )
				{
					Log.e(TAG, "connection lost trying to read message", e);
					connectionLost();
				}
			}
		}


		public void write( int out )
		{
			try
			{
				outputStream.write(out);
			}
			catch ( IOException e )
			{
				Log.e(TAG, "error during write(int)", e);
			}
		}


		public void write( byte[] out )
		{
			try
			{
				outputStream.write(out);
			}
			catch ( IOException e )
			{
				Log.e(TAG, "error during write(byte)", e);
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
