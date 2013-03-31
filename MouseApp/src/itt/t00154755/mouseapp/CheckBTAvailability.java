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
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Christopher
 * 
 *         This Activity is used to check the current state of the bluetooth
 *         adapter, if bluetooth is not enabled it will prompt the user allow
 *         the application turn on the bluetooth settings.
 */
public class CheckBTAvailability extends Activity implements OnClickListener
{
	protected static final int		DISCOVERY_REQUEST	= 1;
	private BluetoothAdapter		btAdapt;
	public TextView					tvStatus;
	public TextView					tvCaliDetails;

	public ProgressBar				pbCalibrate;
	public Button					bConnect;
	public Button					bDisconnect;
	public Button					bCalibrate;
	public Button					bSaveContinue;

	private String					toastText			= "";
	private BluetoothDevice			remoteDevice;
	public String					thresholdValue;
	private CalibrateThesholdTask	ct;

	BroadcastReceiver				bluetoothState		= new BroadcastReceiver()
														{
															@Override
															public void onReceive(
																	Context context,
																	Intent intent )
															{
																// String
																// prevStateExtra
																// =
																// BluetoothAdapter.EXTRA_PREVIOUS_STATE;
																String stateExtra = BluetoothAdapter.EXTRA_STATE;
																int state = intent
																		.getIntExtra(
																				stateExtra,
																				-1);

																switch ( state )
																{
																	case ( BluetoothAdapter.STATE_TURNING_ON ):
																	{
																		toastText = "Bluetooth is turning on";
																		makeShortToast(toastText);
																		break;
																	}
																	case ( BluetoothAdapter.STATE_ON ):
																	{
																		toastText = "Bluetooth is on";
																		makeShortToast(toastText);
																		setupUI();
																		break;
																	}
																	case ( BluetoothAdapter.STATE_TURNING_OFF ):
																	{
																		toastText = "Bluetooth is turning off";
																		makeShortToast(toastText);
																		break;
																	}
																	case ( BluetoothAdapter.STATE_OFF ):
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
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkbt);
		// add the setupUI() method to the onCreate method.

		// get the object on the user interface - used to check the bluetooth
		tvStatus = (TextView ) findViewById(R.id.tvCurrentStatus);
		pbCalibrate = (ProgressBar ) findViewById(R.id.pbCalibrate);
		bConnect = (Button ) findViewById(R.id.bConnect);
		bDisconnect = (Button ) findViewById(R.id.bDisconnect);

		// get the object on the user interface - used to calibrate the
		// threshold value
		tvCaliDetails = (TextView ) findViewById(R.id.tvCalibrateDetails);
		bCalibrate = (Button ) findViewById(R.id.bCalibrate);
		bSaveContinue = (Button ) findViewById(R.id.bSaveContinue);
		tvCaliDetails.setMovementMethod(new ScrollingMovementMethod());

		setupUI();
	}

	@Override
	public boolean onCreateOptionsMenu( android.view.Menu menu )
	{
		//
		super.onCreateOptionsMenu(menu);
		MenuInflater blowup = getMenuInflater();
		blowup.inflate(R.menu.checkbtmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item )
	{
		//
		switch ( item.getItemId() )
		{
			case R.id.aboutus:
				Intent aboutBT = new Intent(
						"itt.t00154755.mouseapp.ABOUTCHECKBT");
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

	private void setupUI( )
	{
		// set the on click listener on all of the button
		// use the onClick() to fire the actions
		bConnect.setOnClickListener(this);
		bDisconnect.setOnClickListener(this);
		bCalibrate.setOnClickListener(this);
		bSaveContinue.setOnClickListener(this);

		bConnect.setVisibility(View.GONE);
		bDisconnect.setVisibility(View.GONE);
		bCalibrate.setVisibility(View.GONE);
		pbCalibrate.setVisibility(View.GONE);

		btAdapt = BluetoothAdapter.getDefaultAdapter();
		if ( btAdapt.isEnabled() )
		{
			String address = btAdapt.getAddress();
			String name = btAdapt.getName();
			String statusText = "Device Name: " + name + "\nMAC Adress: "
					+ address;

			// turn on the calibrate buttons
			// turn on the the disconnect button
			bCalibrate.setVisibility(View.VISIBLE);
			pbCalibrate.setVisibility(View.VISIBLE);
			bDisconnect.setVisibility(View.VISIBLE);
			tvStatus.setText(statusText);
			tvCaliDetails.setText("Calibration Details");
		} else
		{
			// turn the connect button on
			bConnect.setVisibility(View.VISIBLE);
			tvStatus.setText("Bluetooth is Currently Unavailable");
		}
	}

	@Override
	public void onClick( View v )
	{
		//
		switch ( v.getId() )
		{
			case R.id.bConnect:
				// in order the turn the blue-tooth on the user must grant
				// certain permissions
				// but, before the request is made the blue-tooth adapter must
				// be set
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
			case R.id.bDisconnect:
				// this method will turn off the disconnect button
				// and displaying the connect button
				btAdapt.disable();
				makeShortToast("Bluetooth Off");
			break;
			case R.id.bCalibrate:
				// create a new task, this refers to this class
				ct = new CalibrateThesholdTask(this);
				// execute the task
				ct.execute();
			break;
			case R.id.bSaveContinue:
				// save the prefs and continue to the
				// connection activity
				// Create the intent
				Intent i = new Intent(this, App.class);
				// float threshold = ct.getThreshold();
				// Create the bundle
				Bundle bundle = new Bundle();
				// Add your data to bundle
				bundle.putFloat("THRESHOLD", 0.0f);
				// Add the bundle to the intent
				i.putExtras(bundle);
				startActivity(i);
			break;
		}
	}

	@Override
	protected void onActivityResult( int requestCode, int resultCode,
			Intent data )
	{
		//
		if ( requestCode == DISCOVERY_REQUEST )
		{
			makeShortToast("Dicovery in progress");
			setupUI();
			findDevices();
		}
	}

	private void findDevices( )
	{
		//
		String lastUsedRemoteDevice = getLastUsedRemoteDevice();
		if ( lastUsedRemoteDevice != null )
		{
			toastText = "Checking for known paired devices, namely: "
					+ lastUsedRemoteDevice;
			makeShortToast(toastText);

			Set<BluetoothDevice> pairedDevices = btAdapt.getBondedDevices();
			for ( BluetoothDevice pairedDevice : pairedDevices )
			{
				if ( pairedDevice.getAddress().equals(lastUsedRemoteDevice) )
				{
					toastText = "Found Device" + pairedDevice.getName() + "@"
							+ lastUsedRemoteDevice;
					makeShortToast(toastText);
					remoteDevice = pairedDevice;
				}
			}
		}
		if ( remoteDevice == null )
		{
			toastText = "Starting discovery for remote devices...";
			makeShortToast(toastText);

			if ( btAdapt.startDiscovery() )
			{
				toastText = "Discovery thread started... Scanning for devices...";
				makeShortToast(toastText);
				registerReceiver(discoveryResult, new IntentFilter(
						BluetoothDevice.ACTION_FOUND));
			}
		}
	}

	BroadcastReceiver	discoveryResult	= new BroadcastReceiver()
										{
											public void onReceive(
													Context context,
													Intent intent )
											{
												String remoteDeviceName = intent
														.getStringExtra(BluetoothDevice.EXTRA_NAME);
												// BluetoothDevice remoteDevice;
												remoteDevice = intent
														.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
												toastText = "Discovered"
														+ remoteDeviceName;
												makeShortToast(toastText);
											}
										};

	private String getLastUsedRemoteDevice( )
	{
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		String result = prefs.getString("LAST_REMOTE_DEVICE_ADDRESS", null);
		return result;
	}

	private void makeShortToast( String toastText )
	{
		Toast.makeText(CheckBTAvailability.this, toastText, Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	protected void onPause( )
	{
		//
		super.onPause();
		btAdapt.cancelDiscovery();

	}

	@Override
	protected void onResume( )
	{
		//
		super.onResume();
	}
}// end of the class
