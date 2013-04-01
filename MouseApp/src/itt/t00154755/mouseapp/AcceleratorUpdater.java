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

public class AcceleratorUpdater extends TimerTask implements
		SensorEventListener
{

	private static final String	TAG	= "AcceleratorUpdater Timer";
	Handler	accHandler;
	App		app;
	String	acceloData;

	public AcceleratorUpdater ( Handler accHandler, App app )
	{
		super();
		this.accHandler = accHandler;
		this.app = app;

		Log.d(TAG, "In AcceleratorUpdater update const");
		registerListener();
	}

	private void registerListener( )
	{
		// sensor manager variables
		SensorManager sm;
		Sensor s;

		Log.d(TAG, "In AcceleratorUpdater reg listener");
		sm = (SensorManager ) app.getSystemService(Context.SENSOR_SERVICE);

		if ( sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0 )
		{
			s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
			sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
		}
	}

	@Override
	public void onAccuracyChanged( Sensor sensor, int accuracy )
	{

	}

	@Override
	public void onSensorChanged( SensorEvent event )
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
		gravity[2] = alpha * gravity[2] + ( 1 - alpha ) * event.values[2] * 10;

		float[] linear_acceleration = new float[3];
		linear_acceleration[0] = event.values[0] - gravity[0];
		linear_acceleration[1] = event.values[1] - gravity[1];
		linear_acceleration[2] = event.values[2] - gravity[2];

		covertFloatArrayToIntegerArray(linear_acceleration);
	}

	private void covertFloatArrayToIntegerArray( float[] linear_acceleration )
	{
		int xIntAxis = (int ) linear_acceleration[0];
		int yIntAxis = (int ) linear_acceleration[1];

		acceloData = "" + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis)+ "\n";

		setAcceloData(acceloData);
		Log.d(TAG, acceloData);
	}

	/**
	 * Used to create a new String of events each time that onSensorChanged() is
	 * called.
	 * 
	 * @param event
	 *            the SensorEvent from the accelerometer sensor
	 * @return acceloData the string representation of the array events
	 */

	@Override
	public void run( )
	{
		Log.d(TAG, "In AcceleratorUpdater run");
		accHandler.post(new Runnable()
		{
			@Override
			public void run( )
			{
				String acceloData = getAcceloData();
				
					if (acceloData == null)
					{
						System.out.println("AcceleratorUpdater data is null");
						return;
					}else
					{
						try
						{
							app.passStringDataToServer(acceloData);
						} catch ( IOException e )
						{
							e.printStackTrace();
						}
					}
				}
		});
	}

	public String getAcceloData( )
	{
		return acceloData;
	}

	public void setAcceloData( String acceloData )
	{
		this.acceloData = acceloData;
	}
}