package itt.t00154755.mouseapp;

import java.util.Timer;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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
public class App extends Activity implements OnClickListener
{

	// used for debugging
	private static final String TAG = "Main App";
	private static final boolean D = true;

	// used to handle messages
	public static final int MESSAGE_STATE_CHANGED = 1;
	public static final int MESSAGE_DEVICE_NAME = 2;
	public static final int MESSAGE_TOAST = 3;
	public static final int READ = 4;
	public static final int WRITE = 5;

	// message types
	public static final String DEVICE_NAME = "device_name";
	public static final String TOAST = "toast";

	// request types
	private static final int REQUEST_CONNECT_DEVICE = 1;
	private static final int REQUEST_ENABLE_BT = 2;

	// name of connected device
	public String connectDeviceName = null;

	// UI objects
	public Button rightbtn;
	public Button leftbtn;
	public EditText editText;
	public Timer updateTimer;
	public BluetoothAdapter btAdapter;
	public AppClientService appClientService;
	public TextView title;


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
		
		// this method allows for the app to be customized
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		// set the content view to the main xml file
		setContentView(R.layout.main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		
		title = (TextView ) findViewById(R.id.title_left_text);
		title.setText(R.string.app_name);
		title = (TextView ) findViewById(R.id.title_right_text);
		
		//  set up the buttons
		rightbtn = (Button ) findViewById(R.id.bRight);
		leftbtn = (Button ) findViewById(R.id.bLeft);
		editText = (EditText ) findViewById(R.id.edText);
		
		
		//  check to ensure the device has bluetooth capabilities
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		// if it dosen't finish the app here
		if ( btAdapter == null )
		{
			//  toast are short pop-up messages
			Toast.makeText(this, "bluetooth not available on you device....exiting", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
	}// end of onCreate() method

	
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
			// set the service if buletooth isEabled
			// and the service is not already running
			if ( appClientService == null )
			{
				setUpService();
			}
		}

	}// end of onStart() method


	private void setUpService()
	{
		if ( D )
			Log.i(TAG, "+++ SET UP SERVICE +++");
		// create a new sevice
		// this : refers to this class as a context
		// appHandler : is used to handler communication from the service
		appClientService = new AppClientService(this, appHandler);
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
		
		
		// check to see if a service has been started
		if ( appClientService != null )
		{
			// if it has but it current state is none, start it up
			if ( appClientService.getState() == AppClientService.NONE )
			{
				appClientService.start();
			}
		}

	}


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
		// cancel the update timer
		// when the app is stopped
		if ( updateTimer != null )
		{
			updateTimer.cancel();
		}
		
	}// end of onStop() method

	
	// destroy the service
	// called after onStop()
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if ( D )
			Log.i(TAG, "+++ ON DESTROY +++");

		if ( appClientService != null )
		{
			appClientService.stop();
		}
		
		// end in the app properly
		System.exit(0);
	}

	/*
	 * Overrides the OnClickListener onClick()
	 * depending on which button is pressed the
	 * a different message will be sent to the
	 * server.
	 */
	@Override
	public void onClick( View v )
	{
		switch ( v.getId() )
		{
			case R.id.bLeft:
				// write left click to server
				if ( D )
					Log.i(TAG, "+++ LEFT CLICK +++");
			break;
			case R.id.bRight:
				// write right click to server
				if ( D )
					Log.i(TAG, "+++ RIGHT CLICK +++");
			break;
		}

	}
	// ================================================AppHandler Method================================================
	/*
	 * this method handles incoming messages from the service
	 * these message include the current state of the service,
	 * or any error messages.
	 */
	private final Handler appHandler = new Handler()
	{
		@Override
		public void handleMessage( Message message )
		{
			switch ( message.what )
			{
				case MESSAGE_STATE_CHANGED:
					switch ( message.arg1 )
					{
						case AppClientService.CONNECTED:
							title.setText(R.string.connected_to);
							title.append(connectDeviceName);
							if ( D )
								Log.i(TAG, "+++ CONNECTED +++");
						break;
						case AppClientService.CONNECTING:
							title.setText(R.string.connecting);
							if ( D )
								Log.i(TAG, "+++ CONNECTING +++");
						break;
						case AppClientService.LISTEN:
						case AppClientService.NONE:
							title.setText(R.string.not_connected);
							if ( D )
								Log.i(TAG, "+++ NOT CONNECTED +++");
						break;
					}
				break;
				case MESSAGE_DEVICE_NAME:
					connectDeviceName = message.getData().getString(DEVICE_NAME);
					Toast.makeText(getApplicationContext(), "Connected to: " + connectDeviceName, Toast.LENGTH_SHORT).show();
					if ( D )
						Log.i(TAG, "+++ TOAST DEVICE NAME +++");
				break;
				case MESSAGE_TOAST:
					Toast.makeText(getApplicationContext(), message.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
					if ( D )
						Log.i(TAG, "+++ TOAST ERROR +++");
				break;
			}

		}
	};

	/**
	 * This method determines the current activity request and processes
	 * the request.
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityRequest( int requestCode, int resultCode, Intent data )
	{
		switch ( requestCode )
		{
			case REQUEST_CONNECT_DEVICE:
				if ( resultCode == Activity.RESULT_OK )
				{
					if ( D )
						Log.i(TAG, "+++ CONNECT TO DEVICE +++");
					//String address = data.getExtras().getString(CheckBTDevices.EXTRA_DEVICE_ADDRESS);
					String remoteDeviceMacAddress = "00:15:83:3D:0A:57";

					BluetoothDevice device = btAdapter.getRemoteDevice(remoteDeviceMacAddress);
					appClientService.connect(device);
					
				}
			break;
			case REQUEST_ENABLE_BT:
				if ( resultCode == Activity.RESULT_OK )
				{
					setUpService();
				}
				else
				{
					Toast.makeText(this, "bluetooth not available", Toast.LENGTH_SHORT).show();
					finish();
				}
			break;
		}
	}

	/**
	 *  This method is used to ensure that the current device is discoverable to
	 *  others bluetooth device. 
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
	public boolean onKeyDown( int keyCode, KeyEvent event )
	{
		if ( keyCode == KeyEvent.KEYCODE_ENTER )
		{
			appClientService.write(AppClientService.KEY_ENTER);
			if ( D )
				Log.e(TAG, "+++ ENTERED ENTER +++");
			return true;
		}

		return super.onKeyDown(keyCode, event);

	}

	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		switch ( item.getItemId() )
		{
			case R.id.search:
				if ( D )
					Log.e(TAG, "+++ SEARCH +++");
				Intent btSearchIntent = new Intent(this, CheckBTDevices.class);
				startActivityForResult(btSearchIntent, REQUEST_CONNECT_DEVICE);
			break;
			case R.id.discoverable:
				if ( D )
					Log.e(TAG, "+++ DISCOVER +++");
				ensureDiscoverable();
			break;
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

//================================================================================================================================================================================
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
		updateTimer = new Timer();
		Log.d(TAG, "starting the update timer, updates every .001 of a second");
		//updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 2500, 1000);

	}// end of startTheUpdateTimerTask() method


	public void stopTheUpdateTimerTask()
	{
		if ( D )
			Log.e(TAG, "+++ STOP THE TIMER METHOD +++");
		updateTimer.cancel();

	}// end of startTheUpdateTimerTask() method


	/**
	 * Sends a message.
	 * 
	 * @param message
	 *        A string of text to send.
	 *//*
	private void sendMessage( String message )
	{
		// Check that we're actually connected before trying anything
		if ( appClientService.getState() != AppClientService.CONNECTED )
		{
			Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
			return;
		}

		// Check that there's actually something to send
		if ( message.length() > 0 )
		{
			// Get the message bytes and tell the AppClientService to write
			byte[] send = message.getBytes();
			appClientService.write(send);
		}
	}*/


	

	/*// ================================================AcceleratorUpdater==========================================================================
	*//**
	 * 
	 * @author Christopher Donovan
	 * 
	 *//*
	private class AcceleratorUpdater extends TimerTask implements SensorEventListener
	{

		private static final String TAG = "AcceleratorUpdater Timer";

		// Handler used to communicate with the Main UI Thread
		Handler accHandler;

		// Reference back to the Main UI Thread Activity
		App app;

		// The String that will contain the data from the
		// Accelerometer Object
		String acceloData;


		*//**
		 * Constructor for the AcceleratorUpdater Class.
		 * 
		 * @param accHandler
		 *        the handler used to communicate with the Main UI Thread
		 * @param app
		 *        reference to the creator of the Handler object
		 *//*
		public AcceleratorUpdater( Handler accHandlerIn, App appIn )
		{
			if ( D )
				Log.e(TAG, "+++ ACCELEROMETER-UPDATER +++");
			// assign the class variables
			// to the incoming parameters
			accHandler = accHandlerIn;
			app = appIn;

			// register the listener for the Accelerometer Object

			registerListener();
		}


		
		 * Method that registered the Listener for the Accelerometer
		 * Object.
		 
		private void registerListener()
		{
			if ( D )
				Log.e(TAG, "+++ REGISTERLISTENER FOR SENSOR +++");
			// sensor manager variables
			SensorManager sm;
			Sensor s;

			// uses the app reference to register the listener
			// i do it this way here because this class those
			// not extend activity the getSystemService()
			// is a method that belongs to the Activity Class
			// so i refer the method to the App Activity which dose
			// extend Activity

			sm = (SensorManager ) getSystemService(Context.SENSOR_SERVICE);

			// check to make sure that the SensorList is not empty
			if ( sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0 )
			{
				// get the Accelerometer Sensor
				s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
				// register the listener to the Sensor
				sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
			}
		}


		@Override
		public void onAccuracyChanged( Sensor sensor, int accuracy )
		{

			// i do not use this method
			// but it must be implemented
		}


		*//**
		 * Used to create a new String of events each time that onSensorChanged() is
		 * called.
		 * 
		 * @param event
		 *        the SensorEvent from the accelerometer sensor
		 * @return acceloData the string representation of the array events
		 *//*
		@Override
		public void onSensorChanged( SensorEvent event )
		{

			if ( D )
				Log.e(TAG, "+++ SENSOR CHANGE +++");
			// alpha is calculated as t / (t + dT)
			// with t, the low-pass filter's time-constant
			// and dT, the event delivery rate

			final float alpha = 0.8f;

			float[] gravity = new float[3];
			gravity[0] = alpha * gravity[0] + ( 1 - alpha ) * ( event.values[0] * 10 );
			gravity[1] = alpha * gravity[1] + ( 1 - alpha ) * ( event.values[1] * 10 );
			// i do not use the axis
			// gravity[2] = alpha * gravity[2] + ( 1 - alpha ) * event.values[2] * 10;

			float[] linear_acceleration = new float[3];
			linear_acceleration[0] = event.values[0] - gravity[0];
			linear_acceleration[1] = event.values[1] - gravity[1];
			// i do not the z axis
			// linear_acceleration[2] = event.values[2] - gravity[2];

			// once gravity has been removed from the values
			// i convert the float[] to an int[] array
			covertFloatArrayToIntegerArray(event.values);
		}


		private synchronized void covertFloatArrayToIntegerArray( float[] linear_acceleration )
		{

			// remove the ceil integer x and y values from the float array.
			int xIntAxis = (int ) Math.ceil(linear_acceleration[0]);
			int yIntAxis = (int ) Math.ceil(linear_acceleration[1]);

			// add only the positive values
			acceloData = "" + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";

			if ( D )
				Log.e(TAG, "+++ SET DATA STRING +++" + acceloData);
			// the String value
			setAcceloData(acceloData);
		}


		@Override
		public void run()
		{

			if ( D )
				Log.e(TAG, "+++ UPDATER RUN +++");
			accHandler.post(new Runnable()
			{

				@Override
				public void run()
				{
					if ( D )
						Log.e(TAG, "+++ PASS DATA TO APP +++" + acceloData);
					app.sendMessage(getAcceloData());

				}
			});
		}


		// get and set methods for the String Object
		public synchronized String getAcceloData()
		{

			return acceloData;
		}


		public synchronized void setAcceloData( String acceloData )
		{

			this.acceloData = acceloData;
		}
	}
*/
}// end of the class
