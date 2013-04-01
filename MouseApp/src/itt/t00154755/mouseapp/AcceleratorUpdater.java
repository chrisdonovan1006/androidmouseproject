
package itt.t00154755.mouseapp;

import java.io.IOException;
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
public class AcceleratorUpdater extends TimerTask implements SensorEventListener
{

	private static final String TAG = "AcceleratorUpdater Timer";

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
	public AcceleratorUpdater ( Handler accHandler, App app )
	{

		// assign the class variables
		// to the incoming parameters
		this.accHandler = accHandler;
		this.app = app;

		// register the listener for the Accelerometer Object
		Log.d(TAG, "In AcceleratorUpdater update const");
		registerListener();
	}

	/*
	 * Method that registered the Listener for the Accelerometer
	 * Object.
	 */
	private void registerListener()
	{

		// sensor manager variables
		SensorManager sm;
		Sensor s;

		// uses the app reference to register the listener
		// i do it this way here because this class those
		// not extend activity the getSystemService()
		// is a method that belongs to the Activity Class
		// so i refer the method to the App Activity which dose
		// extend Activity

		Log.d(TAG, "In AcceleratorUpdater reg listener");
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

	@Override public void onAccuracyChanged( Sensor sensor, int accuracy )
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
	@Override public void onSensorChanged( SensorEvent event )
	{

		//
		Log.d(TAG, "In sensorchanged of of AcceleratorUpdater");
		// alpha is calculated as t / (t + dT)
		// with t, the low-pass filter's time-constant
		// and dT, the event delivery rate

		final float alpha = 0.8f;

		float[] gravity = new float[3];
		gravity[0] = alpha * gravity[0] + ( 1 - alpha ) * event.values[0] * 10;
		gravity[1] = alpha * gravity[1] + ( 1 - alpha ) * event.values[1] * 10;
		// i do not use the axis
		// gravity[2] = alpha * gravity[2] + ( 1 - alpha ) * event.values[2] * 10;

		float[] linear_acceleration = new float[3];
		linear_acceleration[0] = event.values[0] - gravity[0];
		linear_acceleration[1] = event.values[1] - gravity[1];
		// i do not the z axis
		// linear_acceleration[2] = event.values[2] - gravity[2];

		// once gravity has been removed from the values
		// i convert the float[] to an int[] array
		covertFloatArrayToIntegerArray(linear_acceleration);
	}

	private synchronized void covertFloatArrayToIntegerArray( float[] linear_acceleration )
	{

		// remove the ceil integer x and y values from the float array.
		int xIntAxis = (int ) Math.ceil(linear_acceleration[0]);
		int yIntAxis = (int ) Math.ceil(linear_acceleration[1]);

		// add only the positive values
		acceloData = "" + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";

		// the String value
		setAcceloData(acceloData);
		Log.d(TAG, acceloData);
	}

	@Override public void run()
	{

		Log.d(TAG, "In AcceleratorUpdater run");
		accHandler.post(new Runnable()
		{

			@Override public void run()
			{

				String acceloData = getAcceloData();

				if ( acceloData == null )
				{
					Log.d(TAG, "AcceleratorUpdater data is null");
					return;
				}
				else
				{
					try
					{
						app.passStringDataToServer(acceloData);
					}
					catch ( IOException e )
					{
						e.printStackTrace();
					}
				}
			}
		});
	}

	// get and set methods for the String Object
	public String getAcceloData()
	{

		return acceloData;
	}

	public void setAcceloData( String acceloData )
	{

		this.acceloData = acceloData;
	}
}
