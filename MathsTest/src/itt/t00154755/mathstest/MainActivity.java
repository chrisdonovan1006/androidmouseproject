package itt.t00154755.mathstest;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.GridView;

public class MainActivity extends Activity
{
	GridView numbersGridView;
	@Override
	protected void onCreate( Bundle savedInstanceState )
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		final GridView numbersGridView = (GridView) findViewById(R.id.numbersGrid);
		
	    // Instance of GridImageAdapter Class
	    numbersGridView.setAdapter(new GridImageAdapter(this));
	}


	@Override
	public boolean onCreateOptionsMenu( Menu menu )
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
