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
import android.view.Surface;
import android.view.WindowManager;

/**
 * @author Christopher Donovan
 * @author AccelometerService.java
 *         <p>
 *         This service runs in the background for the life of the application. It contains the Accelerometer sensor which runs returns an event each time the
 *         sensor changes. The event contains an integer array with the values of the x, y, and z axis. the service sends the data back to the main UI thread
 *         through a handler this data is then passed on to the client which in turn forwards it to the server.
 * 
 * 
 * 
 */
public class AccelometerService extends Service implements SensorEventListener
{

	// used for debugging
	private static final String TAG = "Accelometer Service";
	private static final boolean D = true;

	// the data string
	private String accelerometerData = " 1 0 0 ";

	// the x, y axis's
	private int xIntAxis;
	private int yIntAxis;
	// sensor movement direction
	public static final int LEFTDOWN = 1;
	public static final int RIGHTUP = 2;
	public static final int LEFTUP = 3;
	public static final int RIGHTDOWN = 4;

	// these variables are read in from the main activity
	private Context context;
	private Handler appHandler;
	private WindowManager appWindow;

	// the  which sends the data
	private Thread sendDataThread;

	// sensor manager variables
	private SensorManager accelerometerManager;
	private Sensor accelerometerSensor;

	// boolean to check if the accelerometer is registered
	private boolean isRegistered = false;


	/**
	 * 
	 * @param context
	 *        the global application interface
	 * @param appHandler
	 *        allow data transfer to the main UI
	 * @param appWindow
	 *        the device window
	 */
	public AccelometerService( Context context,
							   Handler appHandler,
							   WindowManager appWindow )
	{
		//
		this.context = context;
		this.appHandler = appHandler;
		this.appWindow = appWindow;
	}


	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		// return start sticky this allows the service to run
		// until told otherwise
		return START_STICKY;
	}


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
		makeToastShort("AccelometerService started");
		registerListener();
	}


	public void endAccelometerService()
	{
		// unregister the listener
		unregisterListener();
		// stop the service
		stopSelf();
		makeToastShort("AccelometerService stopped");
	}


	private void makeToastShort( String string )
	{
		// send message back to UI thread
		Message message = appHandler.obtainMessage(MainMouseApp.MESSAGE_TOAST_ACCELO);
		Bundle bundle = new Bundle();
		bundle.putString(MainMouseApp.TOAST, string);
		message.setData(bundle);
		appHandler.handleMessage(message);
	}


	private void sendDataToUIActivity( String string )
	{
		// send message back to UI thread
		Message message = appHandler.obtainMessage(MainMouseApp.MESSAGE_DATA_ACCELO);
		Bundle bundle = new Bundle();
		bundle.putString(MainMouseApp.DATA, string);
		message.setData(bundle);
		appHandler.handleMessage(message);
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
												  SensorManager.SENSOR_DELAY_UI);
			// set registered
			isRegistered = true;
		}
	}


	/**
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
		try
		{
			prepareTheData(event);
		}
		catch ( InterruptedException e )
		{
			// this shouldn't happen

		}
	}


	/*
	 * The event is passed to this method
	 */
	private void
			prepareTheData( SensorEvent event ) throws InterruptedException
	{
		// step 1. filter the data using a low and high filter
		// float[] filteredEvent = applyFilters(event);

		float xFloatAxis = 0;
		float yFloatAxis = 0;

		// @link http://developer.android.com/reference/android/view/Display.html
		// @link http://www.monkeycoder.co.nz/Community/posts.php?topic=1943
		// @link http://stackoverflow.com/questions/4757632/screen-rotation-using-display-getrotation
		
		// best explanation here
		// @link http://android-developers.blogspot.ie/2010/09/one-screen-turn-deserves-another.html

		// step 2. find the current rotation of the phone
		int rotation = appWindow.getDefaultDisplay().getRotation();
		/*
		 * DisplayMetrics dm = new DisplayMetrics();
		 * appWindow.getDefaultDisplay().getMetrics(dm);
		 * int width = dm.widthPixels;
		 * int height = dm.heightPixels;
		 * //
		 * if ( ( rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180 ) && height > width
		 * || ( rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270 )
		 * && width > height )
		 */
		{
			switch ( rotation )
			{
				case Surface.ROTATION_0:
					xFloatAxis = event.values[0];
					yFloatAxis = event.values[1];
					break;
				case Surface.ROTATION_90:
					xFloatAxis = -event.values[1];
					yFloatAxis = event.values[0];
					break;
				case Surface.ROTATION_180:
					xFloatAxis = -event.values[1];
					yFloatAxis = -event.values[0];
					break;
				case Surface.ROTATION_270:
					xFloatAxis = event.values[0];
					yFloatAxis = -event.values[1];
					break;
				default:
					Log.e(TAG, "Set the rotation to 0 position.");
					xFloatAxis = event.values[0];
					yFloatAxis = event.values[1];
					break;
			}
		}

		Log.d(TAG, "filtered values: " + xFloatAxis + ", " + yFloatAxis);

		// step 3. determine the phones current tilting position
		determinePhonePosition(xFloatAxis, yFloatAxis);

		// make this thread sleep for 100th of a second
		// Thread.sleep(100);
		// step 4. send the data back to the main UI thread
		sendCurrentReadingsToUI();
	}


	/*
	 * 
	 * @link http://developer.android.com/guide/topics/sensors/sensors_motion.html#sensors-motion-accel
	 * 
	 * @param event
	 * 
	 * @return
	 */
	/*
	 * private float[] applyFilters( SensorEvent event )
	 * {
	 * // In this example, alpha is calculated as t / (t + dT),
	 * // where t is the low-pass filter's time-constant and
	 * // dT is the event delivery rate.
	 * long eTime = event.timestamp;
	 * // g = 0.9 * g + 0.1 * v
	 * // g = 9.80665 m/s2
	 * 
	 * // alpha must be: > 0 & < 1
	 * final float alpha = 0.9f;
	 * float[] gravity = new float[3];
	 * 
	 * // Isolate the force of gravity with the low-pass filter.
	 * gravity[0] = alpha * gravity[0] + ( 1 - alpha ) * event.values[0];
	 * gravity[1] = alpha * gravity[1] + ( 1 - alpha ) * event.values[1];
	 * gravity[2] = alpha * gravity[2] + ( 1 - alpha ) * event.values[2];
	 * 
	 * float[] linear_acceleration = new float[3];
	 * // Remove the gravity contribution with the high-pass filter.
	 * linear_acceleration[0] = event.values[0] - gravity[0];
	 * linear_acceleration[1] = event.values[1] - gravity[1];
	 * linear_acceleration[2] = event.values[2] - gravity[2];
	 * 
	 * return gravity;
	 * }
	 */

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
		xIntAxis = (int ) xFloatAxis;
		yIntAxis = (int ) yFloatAxis;

		if ( xIntAxis < 0 && yIntAxis < 0 )
		{
			// add only the positive values
			// a number format exception will be
			// thrown on the server side if negative
			// values are passed across.
			if ( D )
				Log.d(TAG, "move mouse: " + LEFTDOWN);
			setAccelerometerData(" " + LEFTDOWN
								 + " "
								 + Math.abs(xIntAxis)
								 + " "
								 + Math.abs(yIntAxis)
								 + " ");
		}
		else
			if ( xIntAxis > 0 && yIntAxis > 0 )
			{
				// add only the positive values
				if ( D )
					Log.d(TAG, "move mouse: " + RIGHTUP);
				setAccelerometerData(" " + RIGHTUP
									 + " "
									 + " "
									 + Math.abs(xIntAxis)
									 + " "
									 + Math.abs(yIntAxis)
									 + " ");
			}
			else
				if ( xIntAxis < 0 && yIntAxis > 0 )
				{
					// add only the positive values
					if ( D )
						Log.d(TAG, "move mouse: " + LEFTUP);
					setAccelerometerData(" " + LEFTUP
										 + " "
										 + " "
										 + Math.abs(xIntAxis)
										 + " "
										 + Math.abs(yIntAxis)
										 + " ");
				}
				else
					if ( xIntAxis > 0 && yIntAxis < 0 )
					{
						// add only the positive values
						if ( D )
							Log.d(TAG, "move mouse: " + RIGHTDOWN);
						setAccelerometerData(" " + RIGHTDOWN
											 + " "
											 + " "
											 + Math.abs(xIntAxis)
											 + " "
											 + Math.abs(yIntAxis)
											 + " ");
					}

	}

	/*
	 * Create a new Thread that begin passing the data to the main UI
	 * Thread.
	 */
	private void sendCurrentReadingsToUI()
	{
		//  create a new thread and start sending the data to the main UI thread
		sendDataThread = new Thread(new SendDataThread()); // Thread created
		sendDataThread.start(); // Thread started

	}

	/*
	 * private inner class that implements the Runnable interface.
	 */
	private class SendDataThread implements Runnable
	{
		// send the data
		public void run()
		{
			sendDataToUIActivity(getAccelerometerData());
		}
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
		// create and pushed out
		return null;
	}

}// end class
