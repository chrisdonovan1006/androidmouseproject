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

	private static final int LEFTDOWN = 1;
	private static final int RIGHTUP = 2;
	private static final int LEFTUP = 3;
	private static final int RIGHTDOWN = 4;

	private AppClient appClient = null;

	// Reference back to the Main UI Thread Activity
	private App app = null;
	// The String that will contain the data from the
	// Accelerometer Object
	private String acceloData = null;


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

		app = appIn;
		appClient = new AppClient();

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

		covertFloatArrayToIntegerArray(event.values);
	}


	private synchronized void covertFloatArrayToIntegerArray( float[] eventValues )
	{
		if ( D )
			Log.e(TAG, "+++ SET DATA STRING +++\nData: " + acceloData);
		// remove the integer x and y values from the float array.
		int xIntAxis = (int ) eventValues[0];
		int yIntAxis = (int ) eventValues[1];

		if ( xIntAxis < 0 && yIntAxis < 0 )
		{
			// add only the positive values
			acceloData = "" + LEFTDOWN + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
		}
		else
			if ( xIntAxis > 0 && yIntAxis > 0 )
			{
				// add only the positive values
				acceloData = "" + RIGHTUP + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
			}
			else
				if ( xIntAxis < 0 && yIntAxis > 0 )
				{
					// add only the positive values
					acceloData = "" + LEFTUP + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
				}
				else
					if ( xIntAxis > 0 && yIntAxis < 0 )
					{
						// add only the positive values
						acceloData = "" + RIGHTDOWN + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
					}
		
		setAcceloData(acceloData);
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


	@Override
	public void run()
	{
		while ( getAcceloData() != null )
		{
			appClient.sendDataFromTheAccToTheAppClient(getAcceloData());
		}
	}

}
