
package itt.t00154755.mouseapp;

import java.io.IOException;
import java.util.Timer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

/**
 * 
 * @author Christopher
 * 
 *         This is the main activity and the point at which the user interacts
 *         with the application. The Accelerometer data is read from here and
 *         passed on to the client and from the client to the server.
 * 
 */
public class App extends Activity
{

	private static final String TAG = "Main App";

	private Button startConnectionProcess;

	private Timer updateTimer;

	private AppClient appClient;

	/*
	 * i want to start the connection process and ensure that it is running
	 * before i begin the accelerometer thread and start passing the values to
	 * it.
	 */
	@Override protected void onCreate( Bundle savedInstanceState )
	{

		super.onCreate(savedInstanceState);
		// set the content view to the main xml file
		setContentView(R.layout.main);
		// setup the startConnectionProcess button
		startConnectionProcess = (Button ) findViewById(R.id.send);
		// add the onClickListener
		startConnectionProcess.setOnClickListener(new Button.OnClickListener()
		{

			// override the onClick method from the onClickListener
			@Override public void onClick( View v )
			{

				// starts the connection process - server must be running
				Log.i(TAG, "....Client connecting to server...");
				appClient = new AppClient();
				appClient.connectToServer();
			}
		});

	}// end of onStart() method

	@Override protected void onStart()
	{

		super.onStart();
	}// end of onStart() method

	@Override protected void onPause()
	{

		super.onPause();
	}// end of onPause() method

	@Override protected void onStop()
	{

		super.onStop();
		// cancel the update timer
		// when the app is stopped
		if ( updateTimer != null )
		{
			updateTimer.cancel();
		}
		System.exit(-1);
	}// end of onStop() method

	/**
	 * This method is where the update timer is set up the
	 * the timer event must only be started once and allowed
	 * to run in the background uninterrupted until the app
	 * is stopped.
	 * 
	 * The Timer object has a schedule method that takes four
	 * arguments in my case:
	 * 
	 * <pre>
	 * 	updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 100, 100);
	 *  
	 *  AcceleratorUpdater: is a TimerTask that i created.
	 *  Handler: 			is a reference to this (App) Activity, which the AcceleratorUpdater
	 *  					needs to be able to refer back to the Main UI thread.
	 *  Delay (100): 		is the startup time after the update timer is fired; milliseconds.
	 *  Period (100):		update time every 100 milliseconds the timer will pass the data back to the Main UI
	 *  					Thread via the Handler.
	 * </pre>
	 * 
	 * This method will be started as soon as the connection is established.
	 */
	public void startTheUpdateTimerTask()
	{

		Log.d(TAG, "starting the update timer, updates every .001 of a second");
		updateTimer = new Timer();
		updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 100, 100);

	}// end of startTheUpdateTimerTask() method

	protected void passStringDataToServer( String acceloData ) throws IOException
	{

		// pass the string which contains the data array to the server

		appClient.writeOutToTheServer(acceloData);
		Log.i(TAG, "data st 1 " + acceloData);
	}// end of passStringDataToServer() method

	@Override public boolean onCreateOptionsMenu( Menu menu )
	{

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app, menu);
		return true;
	}// end of onCreateOptionsMenu() method

}// end of the class