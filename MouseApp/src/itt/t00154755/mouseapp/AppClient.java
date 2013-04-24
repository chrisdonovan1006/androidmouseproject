package itt.t00154755.mouseapp;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Christopher Donovan
 * @author AppClient.java
 * @since 23/04/2013
 * 
 * @category
 *           This class runs in the background and is used to create the connection
 *           to the server which must be running or a IOException will be thrown.
 *           <p>
 *           The main activity calls the connectToServer(), this class then tries to establish a connect visa the blue-tooth socket using a random UUID, if
 *           successful the socket will be connected using the RFCOMM protocol designed for peer to peer blue-tooth connections.
 *           <p>
 *           Referred to BluetoothChat example in the android SDK.
 * 
 */
// using the socket.isConnected() - requires minimum API 14
@SuppressLint ( "NewApi" )
public class AppClient
{

	// used for debugging
	private static final String TAG = "MainMouseApp Client";
	private static final boolean D = true;

	// used to keep track of the current state of the client
	public static final int WAITING = 0;
	public static final int CONNECTED = 1;
	// private final int RUNNING = 2;

	// class fields
	private BluetoothAdapter btAdapter;
	private ClientCommsThread clientCommThread;
	private int state;
	private Handler appHandler;


	/**
	 * 
	 * @param appHandler
	 *        the main UI threads handler
	 */
	public AppClient(Context context, Handler appHandler )
	{
		// get the default adapter
		if ( D )
			Log.d(TAG, "getting default adapter");
		// get the current blue-tooth adapter handler
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		// set the default state to waiting
		state = WAITING;
		// This is the main UI threads handler that will allow
		// the client to communicate with the main activity
		this.appHandler = appHandler;
	}


	// starts the connectToServer process
	public void start()
	{
		connectToSever();
	}


	/**
	 * this method connects to the server it opens an RFCOMM serial port connection
	 * over TCP this ensure that the data
	 */
	private void connectToSever()
	{
		BluetoothDevice btDevice = null;
		BluetoothSocket btSocket = null;

		if ( D )
			Log.d(TAG, "getting local device");

		// returns the device on which the app is running
		btDevice = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");
		// check to ensure that the bluetooth is turned on
		
		// must cancel the discovery process before trying to connect
		// as the discovery process is resource heavy
		btAdapter.cancelDiscovery();

		if ( D )
			Log.d(TAG, "connecting to server");
		try
		{
			/*
			 * This method create an RFComm connection with the server,
			 * because i am using the createRfcommSocketToServiceRecord and not
			 * the createInscureRfcommSocketToServiceRecord it means that the user
			 * phone must be bonded/paired with the server host machine. I choose
			 * implement it this way because the device will be accessing the Robot
			 * Class object.
			 * 
			 * RFComm provides a secure SPP connection over TCP which offer a better
			 * connection than UDP. The UUID that i am using was generated here
			 * 
			 * @link http://www.famkruithof.net/uuid/uuidgen
			 * 
			 * more on RFComm here:
			 * 
			 * @link http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord(java.util.UUID)
			 * 
			 * @link http://developer.bluetooth.org/TechnologyOverview/Pages/RFCOMM.aspx
			 * 
			 * @link http://en.wikipedia.org/wiki/Bluetooth_protocols#Radio_frequency_communication_.28RFCOMM.29
			 */
			btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("27012f0c-68af-4fbf-8dbe-6bbaf7aa432a"));
		}
		catch ( IOException e )
		{
			if ( D )
				Log.e(TAG, "error creating the RFCOMM connection");
			e.printStackTrace();
		}

		if ( D )
			Log.d(TAG, "about to connect");

		while ( state == WAITING )
		{
			try
			{
				// try to connect to using the socket
				btSocket.connect();
			}
			catch ( IOException e1 )
			{
				if ( D )
					Log.e(TAG, "error connecting using the socket");
				e1.printStackTrace();
			}

			if ( btSocket.isConnected() )
			{
				if ( D )
					Log.d(TAG, "Connected!");

				sendConnectedToastToUIHandler();

				setState(CONNECTED);
			}

		}

		try
		{
			// String name = btDevice.getName();
			// sendDeviceNameToUIHandler(name);

			// once the connection is established
			// send the socket which should be connected
			createClientCommThread(btSocket);
		}
		catch ( IOException e )
		{
			if ( D )
				Log.e(TAG, "error creating the client comms thread");
			e.printStackTrace();
		}

	}


	/**
	 * sends a toast back to the UI Handler once the connection is made
	 */
	private void sendConnectedToastToUIHandler()
	{
		// message back to UI
		Message message = appHandler.obtainMessage(MainMouseApp.MESSAGE_TOAST_CLIENT);
		Bundle bundle = new Bundle();
		bundle.putString(MainMouseApp.TOAST,
						 "The device is now connected to the server");
		message.setData(bundle);
		appHandler.handleMessage(message);
	}


	/**
	 * @param name
	 *        the friendly name of the connected blue-tooth device
	 */
	/*
	 * private void sendDeviceNameToUIHandler( String name )
	 * {
	 * // message back to UI
	 * Message message = appHandler.obtainMessage(MainMouseApp.MESSAGE_DEVICE_NAME);
	 * Bundle bundle = new Bundle();
	 * bundle.putString(MainMouseApp.DEVICE_NAME, name);
	 * message.setData(bundle);
	 * appHandler.handleMessage(message);
	 * }
	 */

	/**
	 * @return the state
	 *         the current state of the connection process
	 */
	public synchronized int getState()
	{
		return state;
	}


	/**
	 * @param state
	 *        the state to set
	 */
	public synchronized void setState( int stateIn )
	{
		if ( D )
			Log.i(TAG, "state : " + state + " => to: " + stateIn);
		state = stateIn;
	}


	/**
	 * This method create a new Thread based on the inner
	 * class ClientCommThread which takes in the blue-tooth socket
	 * 
	 * @param btSocket
	 *        the currently open blue-tooth socket
	 * @throws IOException
	 *         if the socket is not open
	 */
	public synchronized void
			createClientCommThread( BluetoothSocket btSocket ) throws IOException
	{
		clientCommThread = new ClientCommsThread(btSocket);
		clientCommThread.start();
	}


	/**
	 * @param acceloData
	 *        a byte[] representation of the accelerometer data reading
	 */
	public synchronized void write( byte[] acceloData )
	{
		// method referenced
		if ( D )
			Log.i(TAG, "Pass the data to the thread");
		// Create temporary object
		ClientCommsThread cct;
		// Synchronize a copy of the ConnectedThread
		synchronized ( this )
		{
			if ( state != CONNECTED )
				return;
			cct = clientCommThread;
		}
		// Perform the write unsynchronized
		cct.writeToAppClient(acceloData);

	}


	/**
	 * cancel the currently running thread
	 */
	public void closeClient()
	{
		if ( clientCommThread != null )
		{
			clientCommThread.cancel();
			clientCommThread = null;
		}
		setState(WAITING);
	}

	/**
	 * @author Christopher Donovan
	 * 
	 *         This the private inner class that extends Thread
	 *         each time this class is called a new thread is created.
	 * 
	 *         This class creates an Output Stream using the btSocket
	 *         and fires the data to the server.
	 */
	private class ClientCommsThread extends Thread
	{

		private static final String TAG = "Client Comms Thread";
		private BluetoothSocket btSocket = null;
		private final OutputStream outStream;


		/*
		 * Class constructor reads-in blue-tooth socket
		 */
		public ClientCommsThread( BluetoothSocket btSocket )
		{
			this.btSocket = btSocket;
			OutputStream tmpOut = null;
			if ( D )
				Log.i(TAG, "+++ SET UP THE CLIENT COMM THREAD +++");
			try
			{
				tmpOut = btSocket.getOutputStream();
			}
			catch ( IOException e )
			{
				if ( D )
					Log.e(TAG, "error trying to open the output stream");
			}
			outStream = tmpOut;
		}


		/**
		 * @param acceloData
		 *        the byte[] representation of the accelerometer data
		 */
		public void writeToAppClient( byte[] acceloData )
		{
			try
			{
				outStream.write(acceloData);
			}
			catch ( IOException e )
			{
				if ( D )
					Log.e(TAG, "error while writing to the server");
				e.printStackTrace();
				cancel();
				try
				{
					outStream.close();
					cancel();
				}
				catch ( IOException e1 )
				{
					if ( D )
						Log.e(TAG, "error trying to close the outstream");
					e1.printStackTrace();
				}
			}
			catch ( Exception e2 )
			{
				if ( D )
					Log.e(TAG, "error trying to write to the server");
				cancel();
				e2.printStackTrace();
			}
		}


		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run()
		{

		}


		/**
		 * close the btSocket and cancel the thread
		 */
		public synchronized void cancel()
		{
			try
			{
				if ( btSocket != null )
				{
					btSocket.close();
					btSocket = null;
				}

				if ( clientCommThread != null )
				{
					clientCommThread = null;
				}
			}
			catch ( IOException e )
			{
				if ( D )
					Log.e(TAG, "Cancelling the Client Comm Thread");
				if ( D )
					Log.e(TAG, "error closing the socket");
				e.printStackTrace();
			}
		}
	}

}
