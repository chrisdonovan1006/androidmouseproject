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
	private static final String	TAG	= "Main App";
	private Button				send;
	private Timer				updateTimer;
	private AppClient			appClient;

	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		send = (Button ) findViewById(R.id.send);

		send.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick( View v )
			{
				// starts the connection process - server must be running
				Log.i(TAG, "client connecting to server");
				appClient = new AppClient();
				appClient.connectToServer();
				
				while(appClient.isAvailable())
				{
					startTheUpdateTimerTask();
				}
			}
		});
	}

	@Override
	protected void onStart( )
	{
		super.onStart();
	}

	@Override
	protected void onPause( )
	{
		super.onPause();
	}

	@Override
	protected void onStop( )
	{
		super.onStop();
		if ( updateTimer != null )
		{
			updateTimer.cancel();
		}
	}

	private void startTheUpdateTimerTask( )
	{
		Log.d(TAG, "starting the update timer, updates every .001 of a second");
		updateTimer = new Timer();
		updateTimer.schedule(new AcceleratorUpdater(new Handler(), this), 100,
				100);

	}

	protected void passStringDataToServer( String acceloData )
			throws IOException
	{
		// pass the string which contains the data array to the server

		appClient.writeOutToTheServer(acceloData);
		Log.i(TAG, "data st 1 " + acceloData);
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.app, menu);
		return true;
	}

	
}// end of the class