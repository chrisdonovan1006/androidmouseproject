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
 * @author Christopher Donovan 
 * 
 * This is the bluetooth service class it first checks to ensure that bluetooth 
 * 
 * 
 * 
 * {@link http
 *         ://mobisocial.stanford.edu/news/2011/03/bluetooth-across-android-and-a-desktop/}
 * {@link http
 *         ://developer.android.com/guide/topics/connectivity/bluetooth.html#EnablingDiscoverability}
 */
public class AppClientService
{

	private static final String TAG = "App Client";
	private static final boolean D = true;
	
	
	private final UUID SPP_UUID = UUID.fromString("427e7ad0-9c78-11e2-9e96-0800200c9a66");
	//00000003-0000-1000-8000-00805F9B34FB - RFCOMM
	//00001101-0000-1000-8000-00805F9B34FB - SPP
	
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
	
	public static final int EXIT_CMD = -1;
	public static final int KEY_ENTER = 0;
	

	// String value
	public String acceloData;

	// used to count the number of null packets being sent
	public int nullPacketsOut;


	/**
	 * 
	 */
	public AppClientService( Context context, Handler handler )
	{
		if(D) Log.i(TAG, "+++ APP CLIENT SERVICE +++");
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		setState(NONE);

		appHandler = handler;
	}


	private synchronized void setState( int stateIn )
	{
		if(D) Log.i(TAG, "+++ SET STATE +++ --> \n" + state + " : " + stateIn);
		state = stateIn;
	}


	public synchronized int getState()
	{
		if(D) Log.i(TAG, "+++ GET STATE +++");
		return state;
	}


	public synchronized void start()
	{
		if(D) Log.i(TAG, "+++ START METHOD +++");
		
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
		setState(LISTEN);
	}


	public synchronized void connect( BluetoothDevice device )
	{
		if(D) Log.i(TAG, "+++ CONNECT METHOD +++");
		if(D) Log.i(TAG, "connecting to: " + device);
		
		
		// cancel any threads trying to connect
		if ( getState() == CONNECTING )
		{
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
		
		if (getState() == LISTEN)
		{
			// start the thread that will connect to PC
			connectThread = new ConnectThread(device);
			connectThread.start();
			setState(CONNECTING);
		}
		
		
		setState(CONNECTING);
	}


	public synchronized void connected( BluetoothSocket socket, BluetoothDevice device )
	{
		if(D) Log.i(TAG, "+++ CONNECTED METHOD +++");
		
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
		if (getState() == CONNECTING)
		{
    		
			connectedThread = new ConnectedThread(socket);
    		connectedThread.start();
    		
    		// message back to UI
    		Message message = appHandler.obtainMessage(App.MESSAGE_DEVICE_NAME);
    		Bundle bundle = new Bundle();
    		bundle.putString(App.DEVICE_NAME, device.getName());
    		message.setData(bundle);
    		appHandler.sendMessage(message);
    
    		setState(CONNECTED);	
		}
		setState(CONNECTED);
	}


	public synchronized void stop()
	{
		if(D) Log.i(TAG, "+++ STOP ALL THREADS +++");
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
		if(D) Log.i(TAG, "+++ WRITE BYTES +++");
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
		if(D) Log.i(TAG, "+++ WRITE INTS +++");
		ConnectedThread ct;

		synchronized ( this )
		{
			if ( state != CONNECTED )
				return;

			ct = connectedThread;
		}

		ct.write(out);
	}
	
	public void writeString( String out )
	{
		if(D) Log.i(TAG, "+++ WRITE STRINGS +++");
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
		if(D) Log.i(TAG, "+++ CONNECTION LOST +++");
		
		setState(LISTEN);
		
		Message message = appHandler.obtainMessage(App.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(App.TOAST, "unable to connect to the server");
		message.setData(bundle);
		appHandler.sendMessage(message);
		
		//restart the connecting process
		AppClientService.this.start();
	}


	private void connectionFailed()
	{
		if(D) Log.i(TAG, "+++ CONNECTION FAILED +++");
		
		setState(LISTEN);

		Message message = appHandler.obtainMessage(App.MESSAGE_TOAST);
		Bundle bundle = new Bundle();
		bundle.putString(App.TOAST, "the connection was lost");
		message.setData(bundle);
		appHandler.sendMessage(message);
		
		//restart the connecting process
		AppClientService.this.start();
	}

	private class ConnectThread extends Thread
	{
		private BluetoothDevice device;
		private final BluetoothSocket socket;

		public ConnectThread( BluetoothDevice deviceIn )
		{
			if(D) Log.e(TAG, "+++ CONNECT THREAD +++");
			
			device = deviceIn;
			BluetoothSocket temp = null;
			try
			{
				// Log.d(TAG, "getting local device");
				// remote MAC here:
				//device = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");
				if(D) Log.e(TAG, "+++ CONNECTING TO SERVER +++");
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
			if(D) Log.e(TAG, "+++ ABOUT TO CONNECT +++");
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
			
			if(D) Log.e(TAG, "+++ CONNECTED +++");
			
			// reset the connect thread
			synchronized ( AppClientService.this )
			{
				connectThread = null;
			}
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
		private final InputStream inputStream;
		private final OutputStream outputStream;


		public ConnectedThread( BluetoothSocket socketIn )
		{
			if(D) Log.i(TAG, "+++ CONNECTED THREAD+++");
			
			socket = socketIn;
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
			if(D) Log.i(TAG, "+++ READ DATA IN +++");
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
					// Start the service over to restart listening mode
                    AppClientService.this.start();
                    break;
				}
			}
		}


		public void write( int out )
		{
			if(D) Log.i(TAG, "+++ WRITE INT OUT +++");
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
			if(D) Log.i(TAG, "+++ WRITE BYTE OUT +++");
			try
			{
				outputStream.write(out);
				
				 // Share the sent message back to the UI Activity
                appHandler.obtainMessage(App.WRITE, -1, -1, out)
                        .sendToTarget();
			}
			catch ( IOException e )
			{
				Log.e(TAG, "error during write(byte)", e);
			}
		}
		
		public void write( String out )
		{
			if(D) Log.i(TAG, "+++ WRITE STRING OUT +++");
			try
			{
				outputStream.write(out.getBytes());
			}
			catch ( IOException e )
			{
				Log.e(TAG, "Error writing the string data to the server", e);
			}
			
			/*PrintStream printStream = new PrintStream(outputStream);
			printStream.print(out);
			printStream.close();*/
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
