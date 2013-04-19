package itt.t00154755.mouseapp;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AppClient2
{

	private static final String TAG = "App Client 2";
	private BluetoothAdapter btAdapter = null;
	private ClientCommsThread clientCommThread = null;

	private final int WAITING = 0;
	private final int CONNECTED = 1;
	// private final int RUNNING = 2;
	private int state;
	private Handler accHandler;


	public AppClient2( Handler accHandler )
	{
		// get the default adapter
		Log.d(TAG, "getting default adapter");
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		state = WAITING;

		this.accHandler = accHandler;
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
			btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("27012f0c-68af-4fbf-8dbe-6bbaf7aa432a"));
		}
		catch ( IOException e )
		{
			Log.e(TAG, "error creating the RFCOMM connection");
		}

		Log.d(TAG, "about to connect");
		try
		{
			btSocket.connect();
		}
		catch ( IOException e1 )
		{
			//
			e1.printStackTrace();
		}
		setState(CONNECTED);
		Log.d(TAG, "Connected!");

		// message back to UI
		Message message = accHandler.obtainMessage(App.MESSAGE_STATE_CHANGED);
		Bundle bundle = new Bundle();
		bundle.putString(App.TOAST, "The device is now connected to teh server");
		message.setData(bundle);
		accHandler.handleMessage(message);

		try
		{
			createClientCommThread(btSocket);
		}
		catch ( IOException e )
		{
			e.printStackTrace();
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


	public void write( String acceloData )
	{
		Log.i(TAG, "Pass the data to the thread");
		clientCommThread.writeToAppClient(acceloData);
		Log.i(TAG, "Values Passed: " + acceloData);
	}

	/**
	 * 
	 * @author Christopher
	 * 
	 */
	private class ClientCommsThread extends Thread
	{

		private static final String TAG = "Client Comms Thread";
		private BluetoothSocket btSocket = null;


		public ClientCommsThread( BluetoothSocket btSocket )
		{
			Log.i(TAG, "+++ SET UP THE CLIENT COMM THREAD +++");
			this.btSocket = btSocket;
		}


		public void writeToAppClient( String acceloData )
		{
			DataOutputStream outStream = null;
			try
			{
				outStream = new DataOutputStream(btSocket.getOutputStream());
				outStream.write(acceloData.getBytes());
				outStream.flush();
			}
			catch ( IOException e )
			{
				//
				e.printStackTrace();
			}
		}


		@Override
		public void run()
		{
			while ( true )
			{
				continue;
			}
		}


		public void cancel()
		{
			try
			{
				btSocket.close();
				btSocket = null;
			}
			catch ( IOException e )
			{
				Log.e(TAG, "Cancelling the Client Comm Thread");
				Log.e(TAG, "error closing the socket");
			}
		}
	}

}
