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


	public CursorRobot(String dataIn)
	{
		System.out.print(TAG + "\ncreating the robot");
		acceloData = dataIn;
		initRobot();
	}

	private void initRobot()
	{

		try
		{
			robot = new Robot();
			
			startLocation = MouseInfo.getPointerInfo().getLocation();
			
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
		convertedString = covertStringToIntArray(acceloData);
		
		
		System.out.println(TAG + "\nmoving the mouse");
		// the force of the movement
		int direction = convertedString[0];
		int moveX = convertedString[1];
		int moveY = convertedString[2];

		
		
		// the current location of the cursor
		int startX = startLocation.x;
		int startY = startLocation.y;

		// amount to move = force + current
		int moveToX = moveX + startX;
		int moveToY = moveY + startY;
		
		while ( true )
		{
			
			if (direction == 1)
			{
				robot.mouseMove(-moveToX, -moveToY);
			}
			else if (direction == 2)
			{
				robot.mouseMove(moveToX, moveToY);
			}
			else if (direction == 3)
			{
				robot.mouseMove(-moveToX, moveToY);
			}
			else if (direction == 4)
			{
				robot.mouseMove(moveToX, -moveToY);
			}
			
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

