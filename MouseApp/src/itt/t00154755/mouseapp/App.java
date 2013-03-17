package itt.t00154755.mouseapp;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class App extends Activity {
	protected static final String TAG = "Main App";
	private Button send;
	private Timer updateTimer;
	private AppUtils a;
	private AppClient appClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		send = (Button) findViewById(R.id.send);

		send.setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				// starts the connection process - server must be running
				a.info(TAG, "client connecting to server");
				appClient = new AppClient();
				appClient.connectToSever();
			}
		});

	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		appClient = new AppClient();
		appClient.connectToSever();
		
		if (appClient.socket.isConnected()){
			whenConnected();
		}
		

	}

	private void whenConnected() {
		Log.d(TAG, "starting the update timer, updates every .0032 of a second");
		updateTimer = new Timer();
		updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 5000,
				32);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app, menu);
		return true;
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	protected void passStringDataToServer(String acceloData) throws IOException {
		// pass the string which contains the data array to the server

			appClient.writeOutToTheServer(acceloData);
			Log.i(TAG, "data st 1 " + acceloData);
	}

	private class AcceleratorUpdater extends TimerTask implements
			SensorEventListener {

		Handler accHandler;
		App app;
		String acceloData;

		public AcceleratorUpdater(Handler accHandler, App app) {
			super();
			this.accHandler = accHandler;
			this.app = app;

			Log.d(TAG, "In AcceleratorUpdater update const");
			registerListener();
		}

		private void registerListener() {
			// sensor manager variables
			SensorManager sm;
			Sensor s;

			Log.d(TAG, "In AcceleratorUpdater reg listener");
			sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

			if (sm.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
				s = sm.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
				sm.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			//
			Log.d(TAG, "In sensorchanged of of AcceleratorUpdater");
			acceloData = "" + event.values[0] + "," + event.values[1]
					+ "," + event.values[2];
			
			setAcceloData(acceloData);
			Log.d(TAG, acceloData);

		}

		/**
		 * Used to create a new String of events each time that
		 * onSensorChanged() is called.
		 * 
		 * @param event
		 *            the SensorEvent from the accelerometer sensor
		 * @return acceloData the string representation of the array events
		 */


		@Override
		public void run() {
			Log.d(TAG, "In AcceleratorUpdater run");
			accHandler.post(new Runnable() {
				@Override
				public void run() {

					try {
						app.passStringDataToServer(getAcceloData());
						//updateTimer.cancel();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			});
		}

		public String getAcceloData() {
			return acceloData;
		}

		public void setAcceloData(String acceloData) {
			this.acceloData = acceloData;
		}

	}

}
