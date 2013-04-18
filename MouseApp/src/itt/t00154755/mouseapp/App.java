package itt.t00154755.mouseapp;

import java.util.Timer;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Christopher
 * 
 *         This is the main activity and the point at which the user interacts
 *         with the application. The Accelerometer data is read from here and
 *         passed on to the client and from the client to the server.
 * 
 *         ref: BluetoothChat application in the SDK examples
 * 
 */
public class App extends Activity
{

	// used for debugging
	private static final String TAG = "Main App";
	private static final boolean D = true;

	// used to handle messages
	public static final int MESSAGE_STATE_CHANGED = 1;
	public static final int MESSAGE_DEVICE_NAME = 2;
	public static final int MESSAGE_TOAST = 3;

	// used to signal which mouse option is selected
	public static final int RIGHT_BUTTON_CLICK = 1;
	public static final int LEFT_BUTTON_CLICK = 2;
	public static final int SCROOL_WHEEL_CLICK = 3;
	public static final int MOUSE_MOVE = 4;

	// message types
	public static final String DEVICE_NAME = "name";
	public static final String TOAST = "make_toast";

	// request types
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	private BluetoothAdapter btAdapter = null;
	private AppClientService appClientService = null;
	private AppClient appClient = null;
	private Timer updateTimer = null;

	/*
	 * use the onCreate() to instantiate the Objects that will be needed
	 * for the activity to start-up.
	 */
	@Override
	public void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		if ( D )
			Log.i(TAG, "+++ ON CREATE +++");
		setContentView(R.layout.main);

		// ensure that bluetooth isEnabled before continuing
		ensureBluetoothIsEnabled();

		final TextView title = (TextView ) findViewById(R.id.title);
		title.setText(R.string.title);

	}// end of onCreate() method


	/**
	 * 
	 */
	private void ensureBluetoothIsEnabled()
	{
		// check to ensure the device has bluetooth capabilities
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		// if it dosen't finish the app here
		if ( btAdapter == null )
		{
			// toast are short pop-up messages
			Toast.makeText(this, "bluetooth not available on you device....exiting", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}


	// executes immediately after the onCreate()
	@Override
	public void onStart()
	{
		super.onStart();
		if ( D )
			Log.i(TAG, "+++ ON START +++");
		// check to ensure that the bluetooth is turned on
		if ( !btAdapter.isEnabled() )
		{
			// if not start a new activity by request the permission for the user
			Intent requestBT = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(requestBT, REQUEST_ENABLE_BT);
		}
		else
		{
			//startTheUpdateTimerTask();
			// set the service if buletooth isEabled
			// and the service is not already running
			if ( appClient == null )
			{
				if ( D )
					Log.i(TAG, "+++ ON START - SET UP THE APP +++");
				setUpApp();
			}
		}

	}// end of onStart() method


	private void setUpApp()
	{
		if ( D )
			Log.i(TAG, "+++ SET UP SERVICE +++");

		final EditText editText = (EditText ) findViewById(R.id.edText);

		final Button rightbtn = (Button ) findViewById(R.id.bRight);
		rightbtn.setOnClickListener(new View.OnClickListener()
		{

			public void onClick( View v )
			{
				// Perform action on click
				// write right click to server
				if ( D )
					Log.i(TAG, "+++ RIGHT CLICK +++");
				sendMessage("" + RIGHT_BUTTON_CLICK);

				makeShortToast("right click");

			}
		});

		final Button leftbtn = (Button ) findViewById(R.id.bLeft);
		leftbtn.setOnClickListener(new View.OnClickListener()
		{

			public void onClick( View v )
			{
				// Perform action on click
				// write left click to server
				if ( D )
					Log.i(TAG, "+++ LEFT CLICK +++");
				sendMessage("" + LEFT_BUTTON_CLICK);

				makeShortToast("left click");
			}
		});
		final Button sendbtn = (Button ) findViewById(R.id.sendTextToServer);
		sendbtn.setOnClickListener(new View.OnClickListener()
		{

			public void onClick( View v )
			{
				// Perform action on click
				// write right click to server
				if ( D )
					Log.i(TAG, "+++ SEND TEXT TO SERVER +++");
				if ( editText.getText().length() > 0 )
				{
					sendMessage(editText.getText().toString());
				}
				else
				{
					makeShortToast("edit text box is empty, enter text to send");
				}
			}
		});
		// create a new sevice
		// this : refers to this class as a context
		// appHandler : is used to handler communication from the service
		// appClientService = new AppClientService(this, appHandler);
		
		appClient = new AppClient();
		appClient.start();
		
	}


	// this method is called after the app has been paused
	// if the bluetooth setup is slow and the app is paused
	// this method will be called
	@Override
	public synchronized void onResume()
	{
		super.onResume();
		if ( D )
			Log.i(TAG, "+++ ON RESUME +++");

		makeShortToast("update timer started");
		// now start the update timer
		startTheUpdateTimerTask();
		
		// check to see if a service has been started
		if ( appClientService != null )
		{
			// if it has but it current state is none, start it up
			if ( appClientService.getState() == AppClientService.NONE )
			{
				Log.i(TAG, "+++ ON RESUME - START THE SERVICE+++");
				//appClientService.start();
				//appClient.connectToSever();
			}
		}

	}


	// utility method to make a short toast
	public void makeShortToast( String toast )
	{
		Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
	}



	/**
	 * This method determines the current activity request and processes
	 * the request.
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult( int requestCode, int resultCode, Intent data )
	{
		switch ( requestCode )
		{
			case REQUEST_CONNECT_DEVICE:
				if ( resultCode == Activity.RESULT_OK )
				{
					Log.i(TAG, "+++ ON ACTIVITY REQUEST - CONNECT +++");
					makeShortToast("connect to device");
					//connectToServer(data);
					makeShortToast("update timer started");
					// now start the update timer
					startTheUpdateTimerTask();
				}
			break;
			case REQUEST_ENABLE_BT:
				if ( resultCode == Activity.RESULT_OK )
				{
					Log.i(TAG, "+++ ON ACTIVITY REQUEST - SETUP +++");
					setUpApp();
				}
				else
				{
					makeShortToast("bluetooth not available");
					finish();
				}
			break;
		}
	}


/*	private void connectToServer( Intent data )
	{
		// remote MAC:
		Log.i(TAG, "+++ CONNECT TO SERVER - USING THE REMOTE ADDRESS +++");
		String remoteDeviceMacAddress = data.getExtras().getString(CheckBTDevices.EXTRA_DEVICE_ADDRESS);
		// String remoteDeviceMacAddress = "00:15:83:3D:0A:57";

		BluetoothDevice device = btAdapter.getRemoteDevice(remoteDeviceMacAddress);
		// BluetoothDevice device = btAdapter.getRemoteDevice("00:15:83:3D:0A:57");

		appClientService.connect(device);
	}*/


	/**
	 * Sends a message.
	 * 
	 * @param message
	 *        A string of text to send.
	 */
	public synchronized void sendMessage( String message )
	{
		// Check that there's actually something to send
		if ( message != null )
		{
			appClient.sendDataFromTheAccToTheAppClient(message);
		}
	}


	/**
	 * This method is used to ensure that the current device is discoverable to
	 * others bluetooth device.
	 */

	public void ensureDiscoverable()
	{
		if ( btAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE )
		{
			Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}


	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case R.id.connect:
				if ( D )
					Log.e(TAG, "+++ CONNECT +++");
				Intent btSearchIntent = new Intent(this, CheckBTDevices.class);
				startActivityForResult(btSearchIntent, REQUEST_CONNECT_DEVICE);
				return true;

			case R.id.discoverable:
				if ( D )
					Log.e(TAG, "+++ DISCOVERABLE +++");
				ensureDiscoverable();
				return true;
			case R.id.prefs:
				if ( D )
					Log.e(TAG, "+++ PREFS +++");
				Intent btPrefsIntent = new Intent(this, Prefs.class);
				startActivity(btPrefsIntent);
				return true;
		}
		return false;
	}

	/**
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.option_menu, menu);
		return true;
	}// end of onCreateOptionsMenu() method


	/**
	 * This method is where the update timer is set up the
	 * the timer event must only be started once and allowed
	 * to run in the background uninterrupted until the app
	 * is stopped.
	 * 
	 * The Timer object has a schedule method that takes four
	 * arguments in my case:
	 * 
	 * <pre>
	 * 	updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 100, 100);
	 *  
	 *  AcceleratorUpdater: is a TimerTask that i created.
	 *  Handler: 			is a reference to this (App) Activity, which the AcceleratorUpdater
	 *  					needs to be able to refer back to the Main UI thread.
	 *  Delay (100): 		is the startup time after the update timer is fired; milliseconds.
	 *  Period (100):		update time every 100 milliseconds the timer will pass the data back to the Main UI
	 *  					Thread via the Handler.
	 * </pre>
	 * 
	 * This method will be started as soon as the connection is established.
	 */
	public void startTheUpdateTimerTask()
	{
		if ( D )
			Log.e(TAG, "+++ START THE TIMER METHOD +++");
		if ( updateTimer == null )
		{
			updateTimer = new Timer();
			Log.d(TAG, "starting the update timer, updates every second");
			updateTimer.schedule(new AccelerometerUpdaterService(new Handler(), this), 500, 100);
		}

	}// end of startTheUpdateTimerTask() method


	// ++++++++++++++++++++++++++++when the program ends clean up here+++++++++++++++++++++++++++++++++++++++

	@Override
	public void onPause()
	{
		if ( D )
			Log.i(TAG, "+++ ON PAUSE +++");
		super.onPause();
	}// end of onPause() method


	// if the user stops the app cancel the timer
	// to free up resources and battery life
	@Override
	public void onStop()
	{
		super.onStop();
		if ( D )
			Log.i(TAG, "+++ ON STOP +++");
			finish();
	}// end of onStop() method


	// destroy the service
	// called after onStop()
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if ( D )
			Log.i(TAG, "+++ ON DESTROY +++");
		// cancel the update timer
		// when the app is stopped
		stopTheUpdateTimerTask();
		
		if ( appClient != null )
		{
			appClient.cancel();
		}
	}

	public void stopTheUpdateTimerTask()
	{
		if ( D )
			Log.i(TAG, "+++ STOP THE TIMER METHOD +++");
		if ( updateTimer != null )
		{
			updateTimer.cancel();
		}
	}// end of startTheUpdateTimerTask() method

}// end of the class
