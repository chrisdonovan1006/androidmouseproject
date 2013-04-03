// packages

package itt.t00154755.mouseserver;

// imports
import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

/**
 * 
 * @author Christopher Donovan
 * 
 */
public class CursorRobot implements Runnable
{

	private static final String TAG = "Cursor Robot Thread";

	private int[] convertedString;

	private String acceloData;

	private Robot robot;

	private Point startLocation;


	public CursorRobot()
	{

		initRobot();
	}


	public void dataFromServer( String dataIn )
	{

		this.acceloData = dataIn;
		this.convertedString = covertStringToIntArray(acceloData);

	}


	private void initRobot()
	{

		try
		{
			startLocation = MouseInfo.getPointerInfo().getLocation();
			robot = new Robot();
		}
		catch ( AWTException eAWT )
		{
			// print the error stack
			System.out.print(TAG + "\n");
			eAWT.printStackTrace();
			eAWT.getCause();
			System.exit(-1);
		}
	}


	@Override
	public void run()
	{

		System.out.println(TAG + "mouse move");
		// the force of the movement
		int moveX = convertedString[0];
		int moveY = convertedString[1];

		// the current location of the cursor
		int startX = startLocation.x;
		int startY = startLocation.y;

		// amount to move = force + current
		int moveToX = moveX + startX;
		int moveToY = moveY + startY;

		while ( true )
		{
			robot.mouseMove(moveToX, moveToY);
		}

	}


	private synchronized int[] covertStringToIntArray( String acceloData )
	{

		String delims = "[,]";
		String[] tokens = acceloData.split(delims);
		int[] data = new int[tokens.length];

		for ( int i = 0; i < tokens.length; i++ )
		{
			try
			{
				data[i] = Integer.parseInt(tokens[i]);
			}
			catch ( NumberFormatException nfe )
			{
				// print the error stack
				System.out.print(TAG + "\n");
				nfe.printStackTrace();
				nfe.getCause();
				System.exit(-1);
			}
		}

		return data;
	}

}// end of class

