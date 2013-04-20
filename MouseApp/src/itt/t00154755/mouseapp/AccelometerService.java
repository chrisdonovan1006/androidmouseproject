package itt.t00154755.mouseapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * AccelometerService
 * 
 * @author
 */
public class AccelometerService extends Service implements SensorEventListener
{

	private static final String TAG = "Accelometer Service";
	private static final boolean D = true;
	private static final int LEFTDOWN = 1;
	private static final int RIGHTUP = 2;
	private static final int LEFTUP = 3;
	private static final int RIGHTDOWN = 4;
	private App app;
	private Handler appHandler;
	// sensor manager variables
	private SensorManager sm;
	private Sensor s;
	private AsyncTask<SensorEvent, Void, String> sensorValuesTask;
	private SensorEvent newevent;

	public AccelometerService()
	{
		app = new App();
	}


	public void initAccelometerService()
	{
		makeToastShort("AccelometerService started");
	}


	public void endAccelometerService()
	{
		unregisterListener();
		// stop the service
	    stopSelf();
		makeToastShort("AccelometerService stopped");
	}


	@Override
	public int onStartCommand( Intent intent, int flags, int startId )
	{
		registerListener();
		return START_STICKY;
	}


	// Method that registered the Listener for the Accelerometer
	// Object.
	private void registerListener()
	{
		if ( D )
			Log.d(TAG, "+++ REGISTER-LISTENER FOR SENSOR +++");

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


	private void unregisterListener()
	{
		if ( D )
			Log.d(TAG, "+++ UNREGISTER-LISTENER FOR SENSOR +++");
		sm.unregisterListener(this, s);

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
		//long timestamp = event.timestamp;
		newevent = event;
		passOnTheEvent( newevent );
	}

	private void passOnTheEvent( SensorEvent event )
	{
		sensorValuesTask = new SensorValuesTask(app);
		sensorValuesTask.execute(event);
	}

	private class SensorValuesTask extends AsyncTask<SensorEvent, Void, String>
	{

		public App app;
		public String acceloData;


		protected void onProgressUpdate( Void... progress )
		{
		}


		public SensorValuesTask( App app )
		{
			this.app = app;
			acceloData = "1,0,0";
		}


		@Override
		protected String doInBackground( SensorEvent... values )
		{
			SensorEvent event = values[0];
			// remove the integer x and y values from the float array.
			int xIntAxis = (int ) event.values[0];
			int yIntAxis = (int ) event.values[1];

			if ( xIntAxis < 0 && yIntAxis < 0 )
			{
				// add only the positive values
				if(D) Log.d(TAG, "move mouse: " + LEFTDOWN);
				acceloData = "" + LEFTDOWN + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
			}
			else
				if ( xIntAxis > 0 && yIntAxis > 0 )
				{
					// add only the positive values
					if(D) Log.d(TAG, "move mouse: " + RIGHTUP);
					acceloData = "" + RIGHTUP + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
				}
				else
					if ( xIntAxis < 0 && yIntAxis > 0 )
					{
						// add only the positive values
						if(D) Log.d(TAG, "move mouse: " + LEFTUP);
						acceloData = "" + LEFTUP + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
					}
					else
						if ( xIntAxis > 0 && yIntAxis < 0 )
						{
							// add only the positive values
							if(D) Log.d(TAG, "move mouse: " + RIGHTDOWN);
							acceloData = "" + RIGHTDOWN + "," + Math.abs(xIntAxis) + "," + Math.abs(yIntAxis) + "\n";
						}
			return acceloData;
		}


		protected void onPostExecute( String acceloData )
		{
			if(D) Log.d(TAG, "onPostExecute sending:  " + acceloData);
			app.write(acceloData);
		}
	}


	private void makeToastShort( String string )
	{
		// message back to UI
		Message message = appHandler.obtainMessage(App.MESSAGE_TOAST_ACCELO);
		Bundle bundle = new Bundle();
		bundle.putString(App.TOAST, string);
		message.setData(bundle);
		appHandler.handleMessage(message);
	}


	@Override
	public IBinder onBind( Intent intent )
	{
		//
		return null;
	}




}// end class
