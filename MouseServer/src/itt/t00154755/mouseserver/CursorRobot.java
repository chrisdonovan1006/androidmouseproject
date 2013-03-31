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
public class CursorRobot extends Thread
{
	private static final String	TAG			= "Server Communication Thread";
	private Robot				robot;
	private String				acceloData;
	private int					moveX;
	private int					moveY;
	private int					startX;
	private int					startY;
	private boolean				isMoving	= false;

	public CursorRobot ( String acceloData )
	{
		this.acceloData = acceloData;
		startCursorRobot();
	}

	public void startCursorRobot( )
	{
		System.out.println("robot constructor");
		int[] convertedString = covertStringToIntArray(acceloData);
		this.moveX = convertedString[0];
		this.moveY = convertedString[1];
	}
	
	private void moveTheMouseToNewPosition( int moveX, int moveY )
	{
		System.out.println("mouse move method");
		try
		{
			robot = new Robot();

			Point startLocation = getCurrentMousePosition();

			this.startX = startLocation.x;
			this.startY = startLocation.y;

			int moveToX = moveX + startX;
			int moveToY = moveY + startY;
			
			isMoving = true;
			while ( isMoving )
			{
				robot.mouseMove(moveToX, moveToY);
			}

			System.out.println(TAG + "\nx: " + moveToX + " y: " + moveToY);

		} catch ( AWTException e1 )
		{
			e1.printStackTrace();
			isMoving = false;
		}
	}

	private Point getCurrentMousePosition( )
	{
		Point startLocation = MouseInfo.getPointerInfo().getLocation();

		return startLocation;
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
			} catch ( NumberFormatException nfe )
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
	
	
	public void run()
	{
		moveTheMouseToNewPosition(moveX, moveY);
	}
}// end of class

