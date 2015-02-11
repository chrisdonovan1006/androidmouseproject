package itt.t00154755.mouseapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;

/**
 * @author Christopher Donovan
 * @author AppService.java, extends Service implements SensorEventListener, AppCommunicator
 *         <p>
 *         This service runs in the background for the life of the application. It contains the Accelerometer sensor which runs returns an event each time the
 *         sensor changes. The event contains an integer array with the values of the x, y, and z axis. the service sends the data back to the main UI thread
 *         through a handler this data is then passed on to the client which in turn forwards it to the server.
 * 
 *         Refs:
 *         <p>
 *         Android Developers Web-site
 *         <p>
 *         {@link http://developer.android.com/guide/components/services.html} {@link http://developer.android.com/guide/topics/sensors/sensors_overview.html}
 *         <p>
 *         Professional Android 2 Application Development
 *         <p>
 *         Charter 14 - Blue-tooth, Networks, and WiFi {@link http
 *         ://www.wrox.com/WileyCDA/WroxTitle/Professional-Android-2-Application-Development.productCd-0470565527.html}
 *         <p>
 *         The New Boston Android Video Tutorials
 *         <p>
 *         {@link http://thenewboston.org/list.php?cat=6}
 */
public class AppService extends Service implements SensorEventListener, AppCommunicator
{

	// used for debugging
	private static final String TAG = "Accelometer Service";
	// if true show the logging data on the screen, if false don't
	private static final boolean D = false;

	// the data string
	private String accelerometerData;

	// sensor movement direction
	public static final int LEFTDOWN = 4;
	public static final int RIGHTUP = 3;
	public static final int LEFTUP = 2;
	public static final int RIGHTDOWN = 1;

	// these variables are read in from the main activity
	private Context context;
	private Handler appHandler;

	// sensor manager variables
	private SensorManager accelerometerManager;
	private Sensor accelerometerSensor;

	// boolean to check if the accelerometer is registered
	private boolean isRegistered = false;

	// message types
	private static final int TOAST = 1;
	private static final double GTHRESHOLD = 0.5;

	// private static final int DATA = 2;

	/**
	 * Default AppService Constructor
	 */
	public AppService()
	{
		// default appService constructor
	}


	/**
	 * AppService Constructor
	 * 
	 * @param context
	 *        the global application interface
	 * @param appHandler
	 *        allow data transfer to the main UI
	 * @param appWindow
	 *        the device window
	 */
	public AppService( Context context,
					   Handler appHandler,
					   WindowManager appWindow )
	{
		//
		this.context = context;
		this.appHandler = appHandler;
		// this.appWindow = appWindow;
	}


	/**
	 * The onStartCommand() method is used to start a service
	 */
	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		// return start sticky this allows the service to run
		// until told otherwise
		return START_STICKY;
	}


	/**
	 * Called when the is initiated.
	 */
	@Override
	public void onCreate()
	{
		// initiate the service
		super.onCreate();
		initAccelometerService();
	}


	@Override
	public void onDestroy()
	{
		// stop the service and the helper thread
		super.onDestroy();
		endAccelometerService();
	}


	public void initAccelometerService()
	{
		// register the listener
		sendDataToUIThread("AppService started", TOAST);
		registerListener();
	}


	public void endAccelometerService()
	{
		// unregister the listener
		unregisterListener();
		// stop the service
		stopSelf();
		// sendDataToUIThread("AppService stopped", 1);
	}


	/**
	 * @return the accelerometerData
	 */
	public String getAccelerometerData()
	{
		// return the current data
		return accelerometerData;
	}


	/**
	 * @param accelerometerData
	 *        the accelerometerData to set
	 */
	public void setAccelerometerData( String accelerometerData )
	{
		// set the data
		this.accelerometerData = accelerometerData;
	}


	@Override
	public IBinder onBind( Intent intent )
	{
		// this service is not bound to the main
		// because it is running on the main UI and the thread is
		// created and pushed out
		return null;
	}


	@Override
	public void onAccuracyChanged( Sensor sensor, int accuracy )
	{
		// not using the accuracy of the sensor
	}


	/**
	 * This method receives an event each time the
	 * that the sensor changes
	 */
	@Override
	public void onSensorChanged( SensorEvent event )
	{
		// get the current event
		if ( D )
			Log.d(TAG, "+++ SENSOR CHANGE +++");
		// long timestamp = event.timestamp;
		// step 3. determine the phones current tilting position
		determinePhonePosition(event.values[0], event.values[1]);
	}


	/**
	 * AppCommunicator method to transfer data form this class back to the AppMain class.
	 * 
	 */

	@Override
	public void sendDataToUIThread( String data )
	{
		// send message back to UI thread

		Message message = appHandler.obtainMessage(AppMain.MESSAGE_DATA_ACCELO);
		Bundle bundle = new Bundle();
		bundle.putString(AppMain.DATA, data);
		message.setData(bundle);
		appHandler.handleMessage(message);
	}


	/**
	 * AppCommunicator method to transfer data form this class back to the AppMain class.
	 * <p>
	 * <ul>
	 * <li>Type:
	 * <li>TOAST: A short message displayed on the user screen.
	 * <li>DATA: A reading taken from the Accelerometer.
	 * </ul>
	 * 
	 */
	@Override
	public void sendDataToUIThread( String data, int type )
	{
		if ( type == 1 )
		{
			// send message back to UI thread
			Message message = appHandler.obtainMessage(AppMain.MESSAGE_TOAST_ACCELO);
			Bundle bundle = new Bundle();
			bundle.putString(AppMain.TOAST, data);
			message.setData(bundle);
			appHandler.handleMessage(message);
		}
	}


	/**
	 * AppCommunicator method to transfer data form this class back to the AppMain class.
	 * 
	 */
	@Override
	public void sendDataToUIThread( int data )
	{
		// not in use

	}


	@Override
	public void sendDataToUIThread( byte[] data )
	{
		// not in use

	}


	@Override
	public void sendDataToOutputStream( String data )
	{
		// not in use
	}


	@Override
	public void sendDataToOutputStream( int data )
	{
		// not in use
	}


	@Override
	public void sendDataToOutputStream( byte[] data )
	{
		// not in use
	}


	/**
	 * 
	 * @return isRegistered
	 *         returns the current value of isRegistered:
	 *         true if the sensor manager has been registered, otherwise false
	 */
	public boolean isRegistered()
	{
		// returns the current value of isRegistered.
		return isRegistered;
	}


	/*
	 * Method that registered the Listener for
	 * the Accelerometer sensor.
	 */
	private void registerListener()
	{
		// register the listener
		if ( D )
			Log.d(TAG, "+++ REGISTER-LISTENER FOR SENSOR +++");

		accelerometerManager = getAccelerometerManager();
		// check to make sure that the SensorList is not empty
		if ( accelerometerManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
								 .size() != 0 )
		{
			// get the Accelerometer Sensor
			accelerometerSensor = accelerometerManager.getSensorList(Sensor.TYPE_ACCELEROMETER)
													  .get(0);
			// register the listener to the Sensor
			accelerometerManager.registerListener(this,
												  accelerometerSensor,
												  SensorManager.SENSOR_DELAY_GAME);
			// set registered
			isRegistered = true;
		}
	}


	/**
	 * This method returns the Sensor manager object to the service, the context
	 * is the main app that has been passed into the constructor of the service.
	 * As the service runs in the background it needs to refer to the main app context
	 * this allows the service to runs in the background and access the current position
	 * of the phone through the accelerometer sensor.
	 * 
	 * @return the accelerometerManager
	 */
	private SensorManager getAccelerometerManager()
	{
		// get the system service using the main class reference, return the manager object
		return (SensorManager ) context.getSystemService(Context.SENSOR_SERVICE);
	}


	/*
	 * Method that unregisters the Listener for
	 * the Accelerometer sensor.
	 */
	private void unregisterListener()
	{
		// check if the manager is registered
		// if it is then unregister it
		if ( D )
			Log.d(TAG, "+++ UNREGISTER-LISTENER FOR SENSOR +++");

		if ( isRegistered )
		{
			accelerometerManager.unregisterListener(this, accelerometerSensor);
		}

	}


	/*
	 * Using the filters applied the next task is to determine the
	 * current position of the sensors values.
	 * 
	 * @param xFloatAxis
	 * 
	 * @param yFloatAxis
	 */
	private void determinePhonePosition( float xFloatAxis, float yFloatAxis )
	{
		/*
		 * to move the cursor in the direction
		 * i am us the position and negative values
		 * i.e if -x, +y will move the cursor left (-x)
		 * and down (+y)
		 */
		if ( xFloatAxis < GTHRESHOLD && yFloatAxis > GTHRESHOLD )
		{
			// add only the positive values
			// a number format exception will be
			// thrown on the server side if negative
			// values are passed across.
			if ( D )
				Log.d(TAG, "move mouse: " + RIGHTUP);
			setAccelerometerData(" " + RIGHTUP
								 + " "
								 + Math.abs((int ) xFloatAxis)
								 + " "
								 + Math.abs((int ) yFloatAxis)
								 + " ");
		}
		/*
		 * if +x, -y will move the cursor right (+x)
		 * and up (-y)
		 */
		else
			if ( xFloatAxis < GTHRESHOLD && yFloatAxis > GTHRESHOLD )
			{
				// add only the positive values
				if ( D )
					Log.d(TAG, "move mouse: " + LEFTUP);
				setAccelerometerData(" " + LEFTUP
									 + " "
									 + Math.abs((int ) xFloatAxis)
									 + " "
									 + Math.abs((int ) yFloatAxis)
									 + " ");
			}
			else
				if ( xFloatAxis < GTHRESHOLD && yFloatAxis < GTHRESHOLD )
				{
					// add only the positive values
					if ( D )
						Log.d(TAG, "move mouse: " + RIGHTDOWN);
					setAccelerometerData(" " + RIGHTDOWN
										 + " "
										 + Math.abs((int ) xFloatAxis)
										 + " "
										 + Math.abs((int ) yFloatAxis)
										 + " ");
				}
				else
					if ( xFloatAxis > GTHRESHOLD && yFloatAxis > GTHRESHOLD )
					{
						// add only the positive values
						if ( D )
							Log.d(TAG, "move mouse: " + LEFTDOWN);
						setAccelerometerData(" " + LEFTDOWN
											 + " "
											 + Math.abs((int ) xFloatAxis)
											 + " "
											 + Math.abs((int ) yFloatAxis)
											 + " ");
					}
		// send the data back to the AppMain
		sendCurrentReadingsToUI(getAccelerometerData());

	}


	/*
	 * Create a new Thread that begin passing the data to the main UI
	 * Thread.
	 */
	private void sendCurrentReadingsToUI( String data )
	{
		SendDataThread sendDataThread = new SendDataThread(data);
		// create a new thread and start sending the data to the main UI thread
		Thread sendData = new Thread(sendDataThread); // Thread created
		sendData.start(); // Thread started
	}

	/*
	 * private inner class that implements the Runnable interface.
	 */
	private class SendDataThread implements Runnable
	{

		String data;


		public SendDataThread( String data )
		{
			this.data = data;
		}


		// send the data
		public void run()
		{
			if ( data == null )
				return;
			sendDataToUIThread(data);
		}
	}
}// end class
