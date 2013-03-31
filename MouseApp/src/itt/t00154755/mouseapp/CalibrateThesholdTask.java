/**
 * 
 */
package itt.t00154755.mouseapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.util.Log;

/**
 * @author Christopher Donovan
 * 
 *         This Thread is used to calibrate the threshold value, this value is
 *         used as the zero value. The Threshold value is taken by sampling the
 *         accelerometer values and finding the average value by dividing the
 *         total by the number of samples.
 * 
 *         This value is then set as the zero value and anything value greater a
 *         use as positive values or less than the threshold are negivite. The
 *         phone must be in a resting position on a flat surface.
 * 
 */
public class CalibrateThesholdTask extends AsyncTask<Void, String, String>
		implements SensorEventListener
{

	// this is a reference to the class that calls this asynctask
	private CheckBTAvailability	cba;
	private static final String	TAG			= "CalibrateThesholdTask Sampling Process";
	// sensor manager variables
	private SensorManager		sm;
	private Sensor				s;
	private long				startTimer	= System.currentTimeMillis();
	private long				endTimer	= startTimer + 1000;
	private float				threshold;
	float						xy[];

	/**
	 * 
	 */
	public CalibrateThesholdTask ( CheckBTAvailability cba )
	{
		this.cba = cba;
		registerListener();
	}

	@Override
	protected void onPreExecute( )
	{
		cba.tvCaliDetails
				.setText("Starting the Calibartion Process...\nDo not vibrate the phone");
	}

	@Override
	protected String doInBackground( Void... params )
	{
		publishProgress("newline", "Calibrating...");
		String thresholdString = "";
		while ( startTimer < endTimer )
		{
			float[] xxyy = getXy();
			float x = xxyy[0];
			float y = xxyy[1];

			setThreshold(x);
			setThreshold(y);
			Log.i(TAG, "curent total:" + getThreshold());

			for ( int i = 1; i < 1000; i++ )
			{
				publishProgress("timer", "" + i);
			}
		}
		thresholdString = String.valueOf(getThreshold());
		publishProgress("textview", thresholdString);
		sm.unregisterListener(this);
		publishProgress("newline", "Calibrated...");

		return thresholdString;
	}

	@Override
	protected void onProgressUpdate( String... values )
	{
		if ( values[0].equals("textview") )
		{
			cba.tvCaliDetails.setText("Threshold: " + values[1]);
		}
		if ( values[0].equals("newline") )
		{
			cba.tvCaliDetails.setText(cba.tvCaliDetails.getText() + "\n"
					+ values[1]);
		} else
		{
			cba.pbCalibrate.setProgress(Integer.parseInt(values[1]));
		}
	}

	@Override
	protected void onPostExecute( String result )
	{
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}

	private void registerListener( )
	{
		Log.d(TAG, "In AcceleratorUpdater reg listener");
		sm = (SensorManager ) cba.getSystemService(Context.SENSOR_SERVICE);

		if ( sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0 )
		{
			s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
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
		setXy(event.values);
	}

	/**
	 * @return the xy
	 */
	public float[] getXy( )
	{
		return xy;
	}

	/**
	 * @param xy
	 *            the xy to set
	 */
	public void setXy( float[] xy )
	{
		this.xy = xy;
	}

	public void setThreshold( float threshold )
	{
		this.threshold += threshold;
	}

	public float getThreshold( )
	{
		return threshold;
	}

	public void run( )
	{

	}

}
