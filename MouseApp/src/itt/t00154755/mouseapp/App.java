package itt.t00154755.mouseapp;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class App extends Activity 
{
	protected static final String TAG = "Main App";
	private AppUtils cUtils = new AppUtils();
	private AppClient appClient;
	private Timer updateTimer;
	private Button send;
	private  boolean connected = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		send = (Button) findViewById(R.id.send);

		send.setOnClickListener(new Button.OnClickListener() 
		{
			@Override
			public void onClick(View v) 
			{
				// starts the connection process - server must be running
				cUtils.info(TAG, "client connecting to server");
				appClient = new AppClient();
				appClient.connectToServer();
				
				setConnected(true);
				
				while(connected == true)
				{
					whenConnected();
				}
			}
		});
	}

	@Override
	protected void onStart() 
	{
		super.onStart();
	}
	
	@Override
	protected void onPause() 
	{
		super.onPause();
	}
	
	@Override
	protected void onStop() 
	{
		super.onStop();
		if (updateTimer != null)
		{
			updateTimer.cancel();
		}
		
	}

	private void whenConnected() 
	{
		cUtils.debug(TAG, "starting the update timer, updates every .0032 of a second");
		updateTimer = new Timer();
		updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 5000, 64);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app, menu);
		return true;
	}

	protected void passStringDataToServer(String acceloData) throws IOException 
	{
		// pass the string which contains the data array to the server
		appClient.getAccelerometerDataString(acceloData);
		cUtils.info(TAG, "passing data to the server..");
	}	
	
	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}

	private class AcceleratorUpdater extends TimerTask implements SensorEventListener 
	{
		Handler accHandler;
		App app;
		String acceloData;

		public AcceleratorUpdater(Handler accHandler, App app) 
		{
			super();
			this.accHandler = accHandler;
			this.app = app;

			cUtils.debug(TAG, "In AcceleratorUpdater update constructor");
			registerListener();
		}

		private void registerListener() 
		{
			// sensor manager variables
			SensorManager sm;
			Sensor s;

			cUtils.debug(TAG, "In AcceleratorUpdater reg listener");
			
			sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

			if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) 
			{
				s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
				sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) 
		{
			
		}

		@Override
		public void onSensorChanged(SensorEvent event) 
		{
			cUtils.debug(TAG, "In sensorchanged of of AcceleratorUpdater");
			acceloData = "" + event.values[0] + "," + event.values[1]
					+ "," + event.values[2];
	
				setAcceloData(acceloData);
		}

		@Override
		public void run() 
		{
			cUtils.debug(TAG, "In AcceleratorUpdater run");
			accHandler.post(new Runnable() 
			{
				String accelData = getAcceloData();
				
				@Override
				public void run()
				{
					try 
					{
						app.passStringDataToServer(accelData);
					} 
					catch (IOException e) 
					{
						Log.e(TAG, e.getMessage());
					}	
				}
			});
		}

		public String getAcceloData() 
		{
			return acceloData;
		}

		public void setAcceloData(String acceloData) 
		{
			this.acceloData = acceloData;
		}
	}

}// end of the class