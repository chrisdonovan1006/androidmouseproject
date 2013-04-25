// packages

package itt.t00154755.mouseserver;

// imports

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

/**
 * @author Christopher Donovan
 * 
 */
public class CursorRobot extends ServerUtils implements Runnable
{
	// sensor movement direction
	public static final int LEFTDOWN = 1;
	public static final int RIGHTUP = 2;
	public static final int LEFTUP = 3;
	public static final int RIGHTDOWN = 4;

	// used to signal which mouse option is selected
	// started an 6 - 9 because the direction value
	// is add to the same type of string that counts 1-4
	public static final int MOUSE_MOVE = 6;
	public static final int RIGHT_BUTTON_CLICK = 7;
	public static final int LEFT_BUTTON_CLICK = 8;
	public static final int SEND_TEXT_CLICK = 9;
	
	// used to find the center of the screen
	public static double width;
	public static double height;
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

	// the starting position of the cursor
	private Point startLocation;
	
	// set and get the current x and y values
	private int currentX;
	private int currentY;
	
	//use to control the speed of the robot
	private int speedIncrease = 1;

	public CursorRobot()
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


	@Override
	public void run()
	{
		while ( true )
		{
			try
			{
				Thread.sleep(10);
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
				moveClicked(direction);
			}
		}

	}

	// Initiate the robot object and position it in the center of the screen
	private void initRobot()
	{
		height = getHeight();
		width = getWidth();
		ctrHeight = getCtrHeight();
		ctrWidth = getCtrWidth();

		try
		{
			robot = new Robot();
			startLocation = new Point( ctrWidth, ctrHeight);
		}
		catch ( AWTException eAWT )
		{
			// print the error stack
			System.out.print(TAG + "\n");
			eAWT.printStackTrace();
			eAWT.getCause();
			System.exit(-1);
		}

		System.out.println("starting move at: " + startLocation);
	}


	private synchronized void moveClicked( int direction )
	{
		switch ( direction )
		{
			case SEND_TEXT_CLICK:
				robot.keyPress(KeyEvent.VK_ENTER);
				System.out.println(acceloData.subSequence(1,
														  acceloData.length() - 1));
				robot.keyRelease(KeyEvent.VK_ENTER);
				break;
			case LEFT_BUTTON_CLICK:
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				System.out.println("left click");
				robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
				break;
			case RIGHT_BUTTON_CLICK:
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				System.out.println("right click");
				robot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
				break;
		}
	}


	/**
	 * method called by the run method 
	 */
	private synchronized void moveCursor(int direction)
	{

		//System.out.println(TAG + "moving the mouse");
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


	private void moveRightDown( int x, int y )
	{
		robot.mouseMove(getCurrentX() + ( x * this.speedIncrease ), getCurrentY() + ( y * this.speedIncrease ));
		System.out.println("move right down" + MouseInfo.getPointerInfo()
													  .getLocation()
													  .toString());
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentY(MouseInfo.getPointerInfo().getLocation().y);
	}


	private void moveLeftUp( int x, int y )
	{
		robot.mouseMove(getCurrentX() - ( x * this.speedIncrease ), getCurrentY() - ( y * this.speedIncrease ));
		System.out.println("move left up" + MouseInfo.getPointerInfo()
													  .getLocation()
													  .toString());
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentY(MouseInfo.getPointerInfo().getLocation().y);
	}


	private void moveRightUp( int x, int y )
	{
		robot.mouseMove(getCurrentX() + ( x * this.speedIncrease ), getCurrentY() - ( y * this.speedIncrease ));
		System.out.println("move right up" + MouseInfo.getPointerInfo()
													  .getLocation()
													  .toString());
		setCurrentX(MouseInfo.getPointerInfo().getLocation().x);
		setCurrentY(MouseInfo.getPointerInfo().getLocation().y);
	}


	private void moveLeftDown( int x, int y )
	{
		robot.mouseMove(getCurrentX() - ( x * this.speedIncrease ), getCurrentY() + ( y * this.speedIncrease ));
		System.out.println("move left down" + MouseInfo.getPointerInfo()
													  .getLocation()
													  .toString());
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
	 * @return the width
	 */
	private double getWidth()
	{
		return SCREENSIZE.width;
	}


	/**
	 * @return the height
	 */
	private double getHeight()
	{
		return SCREENSIZE.height;
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

