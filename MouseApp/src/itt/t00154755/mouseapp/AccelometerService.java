package itt.t00154755.mouseapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * MyService
 * 
 * @author Nazmul Idris
 * @version 1.0
 * @since Jul 21, 2008, 12:03:01 PM
 */
public class AccelometerService extends Service implements SensorEventListener
{

	private static final String TAG = "Accelometer Service";
	private static final boolean D = true;
	static final String ACCELEROMETER_DATA = "itt.t00154755.AccelometerService.ACCELEROMETER_DATA";
	private static final int LEFTDOWN = 1;
	private static final int RIGHTUP = 2;
	private static final int LEFTUP = 3;
	private static final int RIGHTDOWN = 4;
	private String acceloData;


	/** not using ipc... dont care about this method */
	public IBinder onBind( Intent intent )
	{
		return null;
	}


	@Override
	public void onCreate()
	{
		super.onCreate();
		registerListener();

		makeToastShort("AccelometerService started");
	}


	@Override
	public void onDestroy()
	{
		super.onDestroy();
		makeToastShort("MyService stopped");
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
		//

	}


	@Override
	public void onSensorChanged( SensorEvent event )
	{
		if ( D )
			Log.d(TAG, "+++ SENSOR CHANGE +++");

		getSensorValuesFromSensorEvent(event.values);

	}


	private void getSensorValuesFromSensorEvent( float[] values )
	{
		if ( D )
			Log.d(TAG, "+++ CONVERTING THE DATA TO INT +++");
		// remove the integer x and y values from the float array.
		int xIntAxis = (int ) values[0];
		int yIntAxis = (int ) values[1];

		if ( xIntAxis < 0 && yIntAxis < 0 )
		{
			// add only the positive values
			acceloData = "" + LEFTDOWN + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
			setAcceloData(acceloData);
			Log.d(TAG, "move mouse: " + LEFTDOWN);
		}
		else
			if ( xIntAxis > 0 && yIntAxis > 0 )
			{
				// add only the positive values
				acceloData = "" + RIGHTUP + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
				setAcceloData(acceloData);
				Log.d(TAG, "move mouse: " + RIGHTUP);
			}
			else
				if ( xIntAxis < 0 && yIntAxis > 0 )
				{
					// add only the positive values
					acceloData = "" + LEFTUP + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
					setAcceloData(acceloData);
					Log.d(TAG, "move mouse: " + LEFTUP);
				}
				else
					if ( xIntAxis > 0 && yIntAxis < 0 )
					{
						// add only the positive values
						acceloData = "" + RIGHTDOWN + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
						setAcceloData(acceloData);
						Log.d(TAG, "move mouse: " + RIGHTDOWN);
					}

		sendBackToActivity();

	}


	/**
	 * 
	 */
	private void sendBackToActivity()
	{
		// Send back reading to Activity
		Intent intent = new Intent();
		String data = getAcceloData();
		intent.putExtra("DATA", data);
		sendBroadcast(intent);
	}


	private void setAcceloData( String acceloData )
	{
		this.acceloData = acceloData;

	}


	private String getAcceloData()
	{
		//
		return acceloData;
	}


	private void makeToastShort( String string )
	{
		//
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

}// end class MyService
