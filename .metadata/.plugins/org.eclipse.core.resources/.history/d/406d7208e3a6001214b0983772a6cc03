package itt.t00154755.mouseapp;

import java.util.TimerTask;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.Log;

/**
 * 
 * @author Christopher Donovan
 * 
 */
public class AccelerometerUpdaterService extends TimerTask implements SensorEventListener
{

	private static final String TAG = "AcceleratorUpdater Timer";

	private static final boolean D = true;

	// Handler used to communicate with the Main UI Thread
	Handler accHandler;

	// Reference back to the Main UI Thread Activity
	App app;

	// The String that will contain the data from the
	// Accelerometer Object
	String acceloData;


	/**
	 * Constructor for the AcceleratorUpdater Class.
	 * 
	 * @param accHandler
	 *        the handler used to communicate with the Main UI Thread
	 * @param app
	 *        reference to the creator of the Handler object
	 */
	public AccelerometerUpdaterService( Handler accHandlerIn, App appIn )
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


	// Method that registered the Listener for the Accelerometer
	// Object.

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

		sm = (SensorManager ) app.getSystemService(Context.SENSOR_SERVICE);

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


	/**
	 * Used to create a new String of events each time that onSensorChanged() is
	 * called.
	 * 
	 * @param event
	 *        the SensorEvent from the accelerometer sensor
	 * @return acceloData the string representation of the array events
	 */
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
