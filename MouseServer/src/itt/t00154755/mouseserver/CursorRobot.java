// packages

package itt.t00154755.mouseserver;

// imports
import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.StringTokenizer;

/**
 * @author Christopher Donovan
 * 
 */
public class CursorRobot extends RobotUtils implements Runnable, KeyListener
{

	private static final String TAG = "Cursor Robot Thread";

	private Robot robot;

	
	private static final Point STARTLOCATION = MouseInfo.getPointerInfo().getLocation();
	private int[] convertedValues;
	private Point currentLocation;
	

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
	public static final int SCROOL_WHEEL_CLICK = 9;


	public CursorRobot( String dataIn )
	{
		System.out.print(TAG + "\ncreating the robot");
		convertedValues = covertStringToIntArray(dataIn);
		
		robot = initRobot();
		setCurrentLocation(STARTLOCATION);
		//addMouseMotionListener(this);
		//addMouseListener(this);

	}


	public void init()
	{
		super.init();
		
		
	}
	
	
	private Robot initRobot()
	{
		try
		{
			robot = new Robot();

			// addMouseMotionListener(robot);
			// addMouseListener(robot);
		}
		catch ( AWTException eAWT )
		{
			// print the error stack
			System.out.print(TAG + "\n");
			eAWT.printStackTrace();
			eAWT.getCause();
			System.exit(-1);
		}
		return robot;
	}


	@Override
	public synchronized void run()
	{
		int state = convertedValues[0];
		if ( state > 0 && state < 5 )
		{
			// move the mouse if the state is between 1 and 4 inclusive
			moveCursor();
		}
		else
		{
			moveClicked(state);
		}
	}


	private void moveClicked( int state )
	{
		switch ( state )
		{
			case SCROOL_WHEEL_CLICK:
				robot.keyPress(KeyEvent.VK_1);
				System.out.println("scrool");
				robot.keyRelease(KeyEvent.VK_1);
			break;
			case LEFT_BUTTON_CLICK:
				robot.keyPress(KeyEvent.VK_2);
				System.out.println("left");
				robot.keyRelease(KeyEvent.VK_2);
			break;
			case RIGHT_BUTTON_CLICK:
				robot.keyPress(KeyEvent.VK_3);
				System.out.println("right");
				robot.keyRelease(KeyEvent.VK_3);
			break;
		}
	}


	/**
	 * @throws HeadlessException
	 */
	private synchronized void moveCursor() throws HeadlessException
	{

		System.out.println(TAG + "\nmoving the mouse");
		// the force of the movement
		int direction = convertedValues[0];
		int moveX = convertedValues[1];
		int moveY = convertedValues[2];

		// the current location of the cursor
		Point currLoc = getCurrentLocation();
		int currX = currLoc.x;
		int currY = currLoc.y;

		while ( true )
		{
			switch ( direction )
			{
				case LEFTDOWN:
					moveLeftDown(moveX, moveY, currX, currY);
				break;
				case RIGHTUP:
					moveRightUp(moveX, moveY, currX, currY);
				break;
				case LEFTUP:
					moveLeftUp(moveX, moveY, currX, currY);
				break;
				case RIGHTDOWN:
					moveRightDown(moveX, moveY, currX, currY);
				break;
			}
		}
	}


	/**
	 * @param moveX
	 * @param moveY
	 * @param currX
	 * @param currY
	 * @throws HeadlessException
	 */
	private void moveRightDown( int moveX, int moveY, int currX, int currY ) throws HeadlessException
	{
		int moveToX;
		int moveToY;
		// amount to move = force + current
		moveToX = currX + moveX;
		moveToY = currY - moveY;
		if ( moveToX < getWidth() || moveToY < getHeight() )
		{
			moveToX = (int ) ( getWidth() / 2 );
			moveToY = (int ) ( getHeight() / 2 );
		}
		System.out.println("move rd: " + moveToX + ", " + moveToY);
		robot.mouseMove(moveToX, moveToY);
		setCurrentLocation(MouseInfo.getPointerInfo().getLocation());
	}


	/**
	 * @param moveX
	 * @param moveY
	 * @param currX
	 * @param currY
	 * @throws HeadlessException
	 */
	private void moveLeftUp( int moveX, int moveY, int currX, int currY ) throws HeadlessException
	{
		int moveToX;
		int moveToY;
		// amount to move = force + current
		moveToX = currX - moveX;
		moveToY = currY + moveY;
		if ( moveToX < getWidth() || moveToY < getHeight() )
		{
			moveToX = (int ) ( getWidth() / 2 );
			moveToY = (int ) ( getHeight() / 2 );
		}
		System.out.println("move lu: " + moveToX + ", " + moveToY);
		robot.mouseMove(moveToX, moveToY);
		setCurrentLocation(MouseInfo.getPointerInfo().getLocation());
	}


	/**
	 * @param moveX
	 * @param moveY
	 * @param currX
	 * @param currY
	 * @throws HeadlessException
	 */
	private void moveRightUp( int moveX, int moveY, int currX, int currY ) throws HeadlessException
	{
		int moveToX;
		int moveToY;
		// amount to move = force + current
		moveToX = currX + moveX;
		moveToY = currY + moveY;
		if ( moveToX < getWidth() || moveToY < getHeight() )
		{
			moveToX = (int ) ( getWidth() / 2 );
			moveToY = (int ) ( getHeight() / 2 );
		}
		System.out.println("move ru: " + moveToX + ", " + moveToY);
		robot.mouseMove(moveToX, moveToY);
		setCurrentLocation(MouseInfo.getPointerInfo().getLocation());
	}


	/**
	 * @param moveX
	 * @param moveY
	 * @param currX
	 * @param currY
	 * @throws HeadlessException
	 */
	private void moveLeftDown( int moveX, int moveY, int currX, int currY ) throws HeadlessException
	{
		int moveToX;
		int moveToY;
		// amount to move = force + current
		moveToX = currX - moveX;
		moveToY = currY - moveY;
		if ( moveToX < getWidth() || moveToY < getHeight() )
		{
			moveToX = (int ) ( getWidth() / 2 );
			moveToY = (int ) ( getHeight() / 2 );
		}

		System.out.println("move ld: " + moveToX + ", " + moveToY);
		robot.mouseMove(moveToX, moveToY);
		setCurrentLocation(MouseInfo.getPointerInfo().getLocation());
	}


	/**
	 * 
	 * @param acceloData
	 * @return
	 */
	private synchronized int[] covertStringToIntArray( String acceloData )
	{
		// TODO: convert the incoming String to an int[] that will
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
	 * @return the currentLocation
	 */
	public synchronized Point getCurrentLocation()
	{
		return currentLocation;
	}


	/**
	 * @param currentLocation
	 *        the currentLocation to set
	 */
	public synchronized void setCurrentLocation( Point currentLocation )
	{
		this.currentLocation = currentLocation;
	}

	/**
	 * @return the width
	 */
	public synchronized double getWidth()
	{
		return SCREENSIZE.width;
	}


	/**
	 * @return the height
	 */
	public synchronized double getHeight()
	{
		return SCREENSIZE.height;
	}

	@Override
	public void keyPressed( KeyEvent arg0 )
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyReleased( KeyEvent arg0 )
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyTyped( KeyEvent arg0 )
	{
		// TODO Auto-generated method stub
		
	}
}// end of class

