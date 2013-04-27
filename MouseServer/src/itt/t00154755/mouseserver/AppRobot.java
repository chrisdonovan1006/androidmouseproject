// packages

package itt.t00154755.mouseserver;

// imports

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

/**
 * 
 * This is the class that controls the mapping of the accelerometer readings
 * to the cursor on the screen. It's also controls the moveClicked() and KeyPressed()
 * method of the robot object.
 * 
 * @author Christopher Donovan
 * @since 26/04/2013
 * @version 4.06
 * 
 */
public class AppRobot extends AppServerUtils implements Runnable
{

	// sensor movement direction
	public static final int LEFTDOWN = 4;
	public static final int RIGHTUP = 3;
	public static final int LEFTUP = 2;
	public static final int RIGHTDOWN = 1;

	// used to signal which mouse option is selected
	// started an 6 - 9 because the direction value
	// is add to the same type of string that counts 1-4
	public static final int MOUSE_MOVE = 6;
	public static final int RIGHT_BUTTON_CLICK = 7;
	public static final int LEFT_BUTTON_CLICK = 8;
	public static final int SEND_TEXT_CLICK = 9;

	// used to find the center of the screen
	public static int ctrWidth;
	public static int ctrHeight;

	// use for debugging
	private static final String TAG = "Cursor Robot Thread";

	// the robot class
	private Robot robot;
	// class variable that gets the screen size of the connected monitor
	private final static Dimension SCREENSIZE = Toolkit.getDefaultToolkit()
													   .getScreenSize();

	// string and converted integer array
	private String acceloData;
	private int[] convertedValues;

	// set and get the current x and y values
	private int currentX;
	private int currentY;

	// use to control the speed of the robot
	private int speedIncrease = 2;


	/**
	 * Class Constructor, initiates the robot object
	 * and positions it in the middle of the screen.
	 * 
	 */
	public AppRobot()
	{
		initRobot();
	}


	/**
	 * @param acceloData
	 */
	public void setAcceloData( String acceloData )
	{
		this.acceloData = acceloData;
		convertedValues = convertStringToIntArray(acceloData);
	}


	/**
	 * @return the currentX
	 */
	public int getCurrentX()
	{
		return currentX;
	}


	/**
	 * @param currentX
	 *        the currentX to set
	 */
	public void setCurrentX( int currentX )
	{
		this.currentX = currentX;
	}


	/**
	 * @return the currentY
	 */
	public int getCurrentY()
	{
		return currentY;
	}


	/**
	 * @param currentY
	 *        the currentY to set
	 */
	public void setCurrentY( int currentY )
	{
		this.currentY = currentY;
	}


	/**
	 * This run() method overrides the Runnable run() method.
	 * This method determines if the incoming packet should be
	 * sent to the moveCursor() or mouseClicked() method based
	 * on the first value in the array.
	 * 
	 * It does this every 100m/s
	 */
	@Override
	public void run()
	{
		while ( true )
		{
			try
			{
				Thread.sleep(50);
			}
			catch ( InterruptedException e )
			{
				// catch the interrupted exception
				e.printStackTrace();
			}
			// the current array is null no data has been passed to the setAcceloData method.
			if ( convertedValues == null )
				return;

			int direction = convertedValues[0];
			// move the mouse if the state is between 1 and 4 inclusive
			if ( direction > 0 && direction < 5 )
			{
				moveCursor(direction);
			}
			else
			{
				// mouse click
				mouseClicked(direction);
			}
		}

	}


	// Initiate the robot object and position it in the center of the screen
	private void initRobot()
	{
		ctrHeight = getCtrHeight();
		ctrWidth = getCtrWidth();

		try
		{
			// create a new robot object.
			robot = new Robot();
			robot.mouseMove(ctrWidth, ctrHeight);
			// sets the current position to the center of the screen.
			setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
			setCurrentY(MouseInfo.getPointerInfo().getLocation().y);
		}
		catch ( AWTException eAWT )
		{
			// print the error stack
			System.out.print(TAG + "\n");
			eAWT.printStackTrace();
			eAWT.getCause();
			System.exit(-1);
		}

		System.out.println("starting move at: " + MouseInfo.getPointerInfo()
														   .getLocation()
														   .toString());
	}


	private synchronized void mouseClicked( int direction )
	{
		switch ( direction )
		{
			case SEND_TEXT_CLICK:
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				System.out.println(acceloData.subSequence(1,
														  acceloData.length() - 1));
				break;
			case LEFT_BUTTON_CLICK:
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
				System.out.println("left click");
				break;
			case RIGHT_BUTTON_CLICK:
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				robot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
				System.out.println("right click");
				break;
		}
	}


	/**
	 * method called by the run method
	 */
	private synchronized void moveCursor( int direction )
	{

		// System.out.println(TAG + "moving the mouse");
		// the direction of the movement
		int x = convertedValues[1];
		int y = convertedValues[2];

		switch ( direction )
		{
			case LEFTDOWN:
				moveLeftDown(x, y);
				break;
			case RIGHTUP:
				moveRightUp(x, y);
				break;
			case LEFTUP:
				moveLeftUp(x, y);
				break;
			case RIGHTDOWN:
				moveRightDown(x, y);
				break;
		}
	}


	/*
	 * move the robot in a right-down direction.
	 */
	private void moveRightDown( int x, int y )
	{
		robot.mouseMove(getCurrentX() + ( x * this.speedIncrease ),
						getCurrentY() + ( y * this.speedIncrease ));
		System.out.println("move right down" + MouseInfo.getPointerInfo()
														.getLocation()
														.toString());
		// reset the robot co-ords to the current position
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentY(MouseInfo.getPointerInfo().getLocation().y);
	}


	/*
	 * move the robot in a left-up direction.
	 */
	private void moveLeftUp( int x, int y )
	{
		robot.mouseMove(getCurrentX() - ( x * this.speedIncrease ),
						getCurrentY() - ( y * this.speedIncrease ));
		System.out.println("move left up" + MouseInfo.getPointerInfo()
													 .getLocation()
													 .toString());
		// reset the robot co-ords to the current position
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentY(MouseInfo.getPointerInfo().getLocation().y);
	}


	/*
	 * move the robot in a right-up direction.
	 */
	private void moveRightUp( int x, int y )
	{
		robot.mouseMove(getCurrentX() + ( x * this.speedIncrease ),
						getCurrentY() - ( y * this.speedIncrease ));
		System.out.println("move right up" + MouseInfo.getPointerInfo()
													  .getLocation()
													  .toString());
		// reset the robot co-ords to the current position
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentY(MouseInfo.getPointerInfo().getLocation().y);
	}


	/*
	 * move the robot in a left-down direction.
	 */
	private void moveLeftDown( int x, int y )
	{
		robot.mouseMove(getCurrentX() - ( x * this.speedIncrease ),
						getCurrentY() + ( y * this.speedIncrease ));
		System.out.println("move left down" + MouseInfo.getPointerInfo()
													   .getLocation()
													   .toString());

		// reset the robot co-ords to the current position
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentY(MouseInfo.getPointerInfo().getLocation().y);
	}


	/**
	 * 
	 * @param acceloData
	 *        the string data passed, to be converted
	 * @return data
	 *         the integer[]
	 */
	private synchronized int[] convertStringToIntArray( String acceloData )
	{
		// convert the incoming String to an integer[] that will,
		// that will store the direction, x movement, y movement values
		StringTokenizer st = new StringTokenizer(acceloData);
		int[] data = new int[acceloData.length()];
		int i = 0;
		while ( st.hasMoreTokens() )
		{
			int val = Integer.parseInt(st.nextToken());
			if ( val >= 0 && val <= 9 )
			{
				data[i] = val;
			}
			i++;
		}

		return data;
	}


	/**
	 * @return the ctrWidth
	 */
	private int getCtrWidth()
	{
		return SCREENSIZE.width / 2;
	}


	/**
	 * @return the ctrHeight
	 */
	private int getCtrHeight()
	{
		return SCREENSIZE.height / 2;
	}

}// end of class

