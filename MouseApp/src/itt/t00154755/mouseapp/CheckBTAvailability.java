package itt.t00154755.mouseapp;

import java.util.Set;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @author Christopher
 *
 * This Activity is used to check the current state of the bluetooth adapter,
 * if bluetooth is not enabled it will prompt the user allow the application 
 * turn on the bluetooth settings.
 */
public class CheckBTAvailability extends Activity implements OnClickListener
{
	protected static final int DISCOVERY_REQUEST = 1;
	private BluetoothAdapter btAdapt;
	public TextView tvStatus;
	public Button bConnect;
	public Button bDisconnect;
	public TextView tvCaliDetails;
	public Button bCalibrate;
	public Button bSaveContinue;
	private String toastText = "";
	private BluetoothDevice remoteDevice;
	public static String thresholdValue;
	private CalibrateTheshold ct;
	

	BroadcastReceiver bluetoothState = new BroadcastReceiver() 
	{
		@Override
		public void onReceive(Context context, Intent intent) 
		{
			// String prevStateExtra = BluetoothAdapter.EXTRA_PREVIOUS_STATE;
			String stateExtra = BluetoothAdapter.EXTRA_STATE;
			int state = intent.getIntExtra(stateExtra, -1);

			switch (state) 
			{
				case (BluetoothAdapter.STATE_TURNING_ON): 
				{
					toastText = "Bluetooth is turning on";
					makeShortToast(toastText);
					break;
				}
				case (BluetoothAdapter.STATE_ON): 
				{
					toastText = "Bluetooth is on";
					makeShortToast(toastText);
					setupUI();
					break;
				}
				case (BluetoothAdapter.STATE_TURNING_OFF): 
				{
					toastText = "Bluetooth is turning off";
					makeShortToast(toastText);
					break;
				}
				case (BluetoothAdapter.STATE_OFF): 
				{
					toastText = "Bluetooth is off";
					makeShortToast(toastText);
					setupUI();
					break;
				}
			}
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkbt);
		// add the setupUI() method to the onCreate method.
		setupUI();
	}

	
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) 
	{
		// 
		super.onCreateOptionsMenu(menu);
		MenuInflater blowup = getMenuInflater();
		blowup.inflate(R.menu.checkbtmenu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		// 
		switch(item.getItemId())
		{
		case R.id.aboutus:
			Intent aboutBT = new Intent("itt.t00154755.mouseapp.ABOUTCHECKBT");
			startActivity(aboutBT);
			break;
		case R.id.prefs:
			Intent prefs = new Intent("itt.t00154755.mouseapp.PREFS");
			startActivity(prefs);
			break;
		case R.id.exit:
			finish();
			break;
		}
		// return if it not in the switch and case
		return false;
	}
	
	private void setupUI() 
	{
		// get the object on the user interface - used to check the bluetooth
		final TextView tvStatus = (TextView) findViewById(R.id.currentstatus);
		final Button bConnect = (Button) findViewById(R.id.connect);
		final Button bDisconnect = (Button) findViewById(R.id.disconnect);
		
		// get the object on the user interface - used to calibrate the threshold value
		final TextView tvCaliDetails = (TextView) findViewById(R.id.calibrateDetails);
		final Button bCalibrate = (Button) findViewById(R.id.calibrate);
		final Button bSaveContinue = (Button) findViewById(R.id.saveandcontinue);
		
		// set the on click listener on all of the button
		// use the onClick() to fire the actions
		bConnect.setOnClickListener(this);
		bDisconnect.setOnClickListener(this);
		bCalibrate.setOnClickListener(this);
		bSaveContinue.setOnClickListener(this);
		
		bConnect.setVisibility(View.GONE);
		bDisconnect.setVisibility(View.GONE);
		bCalibrate.setVisibility(View.GONE);
		bCalibrate.setVisibility(View.GONE);
		
		
		btAdapt = BluetoothAdapter.getDefaultAdapter();
		if (btAdapt.isEnabled())
		{
			String address = btAdapt.getAddress();
			String name = btAdapt.getName();
			String statusText = "Device Name: " + name + "\nMAC Adress: " + address;
			
			// turn on the calibrate buttons
			// turn on the the disconnect button
			bCalibrate.setVisibility(View.VISIBLE);
			bCalibrate.setVisibility(View.VISIBLE);
			bDisconnect.setVisibility(View.VISIBLE);
			tvStatus.setText(statusText);
			tvCaliDetails.setText("Calibration Details");
		}
		else 
		{
			// turn the connect button on
			bConnect.setVisibility(View.VISIBLE);
			tvStatus.setText("Bluetooth is Currently Unavailable");
		}
	}

	@Override
	public void onClick(View v) 
	{
		// 
		switch(v.getId())
		{
			case R.id.connect:
				// in order the turn the blue-tooth on the user must grant certain permissions
				// but, before the request is made the blue-tooth adapter must be set
				// to scan for eternal devices and also must be dicovery so that 
				// other devices can find it
				String scanModeChange = BluetoothAdapter.ACTION_SCAN_MODE_CHANGED;
				String beDiscoverable = BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE;
				IntentFilter filter = new IntentFilter(scanModeChange);
				registerReceiver(bluetoothState, filter);
				// once the receiver is registered the user is
				// asked to grant permission
				startActivityForResult(new Intent(beDiscoverable),
						DISCOVERY_REQUEST);
				break;
			case R.id.disconnect:
				// this method will turn off the disconnect button
				// and displaying the connect button
				btAdapt.disable();	
				makeShortToast("Bluetooth Off");
				break;
			case R.id.calibrate:
				ct = new CalibrateTheshold(new Handler(), this);
				ct.start();
				makeShortToast("Starting Calibration Process");
				break;
			case R.id.saveandcontinue:
				// save the prefs and continue to the
				// connection activity
				//Create the intent
		        Intent i = new Intent(this, App.class);
		        float threshold = ct.getThreshold();
		        //Create the bundle
		        Bundle bundle = new Bundle();
		        //Add your data to bundle
		        bundle.putFloat("THRESHOLD", threshold);  
		        //Add the bundle to the intent
		        i.putExtras(bundle);
		        startActivity(i);
				break;
		}	
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		//
		if (requestCode == DISCOVERY_REQUEST) 
		{
			makeShortToast("Dicovery in progress");
			setupUI();
			findDevices();
		}
	}

	private void findDevices()
	{
		//
		String lastUsedRemoteDevice = getLastUsedRemoteDevice();
		if (lastUsedRemoteDevice != null) 
		{
			toastText = "Checking for known paired devices, namely: "
					+ lastUsedRemoteDevice;
			makeShortToast(toastText);

			Set<BluetoothDevice> pairedDevices = btAdapt.getBondedDevices();
			for (BluetoothDevice pairedDevice : pairedDevices)
			{
				if (pairedDevice.getAddress().equals(lastUsedRemoteDevice)) 
				{
					toastText = "Found Device" + pairedDevice.getName() + "@"
							+ lastUsedRemoteDevice;
					makeShortToast(toastText);
					remoteDevice = pairedDevice;
				}
			}
		}
		if (remoteDevice == null) 
		{
			toastText = "Starting discovery for remote devices...";
			makeShortToast(toastText);

			if (btAdapt.startDiscovery()) 
			{
				toastText = "Discovery thread started... Scanning for devices...";
				makeShortToast(toastText);
				registerReceiver(discoveryResult, new IntentFilter(
						BluetoothDevice.ACTION_FOUND));
			}
		}
	}

	BroadcastReceiver discoveryResult = new BroadcastReceiver() 
	{
		public void onReceive(Context context, Intent intent) 
		{
			String remoteDeviceName = intent
					.getStringExtra(BluetoothDevice.EXTRA_NAME);
			// BluetoothDevice remoteDevice;
			remoteDevice = intent
					.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			toastText = "Discovered" + remoteDeviceName;
			makeShortToast(toastText);
		}
	};

	private String getLastUsedRemoteDevice() 
	{
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		String result = prefs.getString("LAST_REMOTE_DEVICE_ADDRESS", null);
		return result;
	}
	
	
	private void makeShortToast(String toastText)
	{
		Toast.makeText(CheckBTAvailability.this, toastText,
				Toast.LENGTH_SHORT).show();
	}
	
	@Override
	protected void onPause() {
		// 
		super.onPause();
		btAdapt.cancelDiscovery();
		unReg();
	}@Override
	protected void onResume() {
		// 
		super.onResume();
	}
	
	public void unReg()
	{
		unregisterReceiver(discoveryResult);
		unregisterReceiver(bluetoothState);
	}
	
	protected void displayTheshold(String thresholdString) 
	{
		//
		if (thresholdString.length() == 0 || thresholdString == null)
		{
			
		}
		else
		{
			tvCaliDetails.setText(thresholdString);
		}
		
		
	}	
	
	/*
	 * This Thread is used to calibrate the threshold value, this value is used
	 * as the zero value.  The Threshold value is taken by sampling the accelerometer
	 * values and finding the average value by dividing the total by the number of samples.
	 * 
	 * This value is then set as the zero value and anything value greater a use as
	 * positive values or less than the threshold are negivite.  The phone must be
	 * in a resting position on a flat surface. 
	 *
	 */
	private class CalibrateTheshold extends Thread implements SensorEventListener
	{
		private static final String TAG = "CalibrateTheshold Sampling Process";
		// sensor manager variables
		private SensorManager sm;
		private Sensor s;
		private long startTimer = System.currentTimeMillis();
		private long endTimer = startTimer + 100;
		// the threshold value for the application
		private float threshold;
		private Handler ctHandler;
		private CheckBTAvailability checkBTAvail;
		
		
		public CalibrateTheshold(Handler ctHandler, CheckBTAvailability checkBTAvail) 
		{
			//
			this.ctHandler = ctHandler;
			this.checkBTAvail = checkBTAvail;
			
			registerListener();
		}
		private void registerListener() 
		{
			Log.d(TAG, "In AcceleratorUpdater reg listener");
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
			//
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			float x = calibrateX(event.values[0]);
			float y = calibrateY(event.values[1]);
			
			float threshold = x/y;
			
			setThreshold(threshold);
		}
		
		private float calibrateX( float x )
		{
			boolean calibrating = true;
			float calX = 0;
			int count = 0;
			
			while ( calibrating )
			{
				calX += x;
				count++;
				if ( count == 100 )
				{
					calibrating = false;
				}
			}
			
			calX = ( calX / count );

			return calX;
		}
		
		private float calibrateY( float y )
		{
			
			boolean calibrating = true;
			float calY = 0;
			int count = 0;
			
			while ( calibrating )
			{
				calY += y;
				count++;
				if ( count == 100 )
				{
					calibrating = false;
				}
			}
			
			calY = ( calY / count );
			
			return calY;
		}
		
		public void setThreshold(float threshold)
		{
			this.threshold = threshold;
		}
		public float getThreshold()
		{
			return threshold;
		}
		
		@Override
		public void run()
		{
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// 
				e.printStackTrace();
			}
			if (startTimer != endTimer)
			{
				ctHandler.post(new Runnable()
				{
					@Override
					public void run() 
					{
						String thresholdString = String.valueOf(getThreshold());
						checkBTAvail.displayTheshold(thresholdString);
					}
				});
			}
			else
			{
				sm.unregisterListener(this);
			}
		}
	}
}// end of the class
