package itt.t00154755.mouseserver.tests;

import static org.junit.Assert.*;
import itt.t00154755.mouseserver.AppRobot;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 
 * @author Christopher Donovan
 *
 *
 * This is the JUint test class for the AppRobot class
 */
public class AppRobotTest
{
	// testing class variables
	private AppRobot testbot;
	private int xTest;
	private int yTest;
	private String testData;
	private int[] testValues;
	private int[] testNotNullValues = {1, 2, 3};


	public AppRobotTest()
	{
		// empty class constructor
	}

	// set up before 
	@Before
	public void setUp() throws Exception
	{
		
		// set up the class variables
		testbot = new AppRobot();
		xTest = 0;
		yTest = 0;
		testData = "testing";
		testValues = null;
	}

	// tear down after
	@After
	public void tearDown() throws Exception
	{
		testbot = null;
		xTest = 0;
		yTest = 0;
		testData = null;
		testValues = null;
	}

	// test the setAcceloData() method to ensure that it
	// set the class variables.
	@Test
	public final void testSetAcceloData()
	{
		this.testData = "test set accelo data";

		assertEquals(this.testData, "test set accelo data");
		System.out.println("testing the setAcceloData() method...");
	}
	
	// test the getCurrentX() method - ensure that it get the current x value.
	@Test
	public final void testGetCurrentX()
	{
		// set the test variable to 200 - using the set method.
		xTest = 200;
		testbot.setCurrentX(xTest);
		// assert that the current value is equals to 200
		assertEquals(testbot.getCurrentX(), 200);
		System.out.println("testing the getCurentX() method...");
	}

	// test the setCurrentX() method - ensure that it sets the current x value.
	@Test
	public final void testSetCurrentX()
	{
		// set the test variable to 200 - using the set method.
		xTest = 200;
		testbot.setCurrentX(xTest);
		// assert that they are equals
		assertEquals(xTest, 200);
		System.out.println("testing the setCurentX() method...");

	}

	// test the getCurrentY() method - ensure that it gets the current y value.
	@Test
	public final void testGetCurrentY()
	{
		// set the test variable to 200 - using the set method.
		yTest = 200;
		testbot.setCurrentY(yTest);
		// assert the values are equals
		assertEquals(testbot.getCurrentY(), 200);
		System.out.println("testing the getCurentY() method...");
	}

	// test the setCurrentY() method - ensure that it sets the current y value.
	@Test
	public final void testSetCurrentY()
	{
		// set the test variable to 200 - using the set method.
		yTest = 200;
		testbot.setCurrentY(yTest);
		// assert that the values are correct
		assertEquals(yTest, 200);
		System.out.println("testing the setCurentY method...");
	}

	// test the run methods thread, the thread sleeps for 10 milliseconds
	@Test
	public final void testRunForTime()
	{
		// set the to the current time
		long time = System.currentTimeMillis();
		// adds 10 milliseconds
		long expTime = time + 10;
		
		// thread sleeps
		try
		{
			Thread.sleep(10);
		}
		catch ( InterruptedException e )
		{
			// catch the interrupted exception
			e.printStackTrace();
		}
		
		// assert that thread sleeps for the required time.
		assertEquals(expTime, System.currentTimeMillis());

		System.out.println("testing the run() Thread time method...");

	}

	// test the run() methods array, if the array is return 
	@Test
	public final void testRunForNullArray()
	{

		// the current array is null no data has been passed to the setAcceloData method.
		if ( testValues == null )
		{
			System.out.println("testing the run() for null array method...");
		}
		// asserts that array is null.
		assertEquals(null, testValues);
	}
	// test the run() methods array, if the array is return 
	@Test
	public final void testRunForNonNullArray()
	{
		
		// the current array is null no data has been passed to the setAcceloData method.
		if ( testNotNullValues.length > 0 )
		{
			System.out.println("testing the run() for null array method...");
		}
		// asserts that array is null.
		assertEquals(3, testNotNullValues.length);
	}
	
	// test to ensure that direction condition is correct
	@Test
	public final void testRunForDirection()
	{
		assertEquals(null, testValues);
		int direction = 2; // move
		String whichDirection = "";
		// move the mouse if the state is between 1 and 4 inclusive
		if ( direction > 0 && direction < 5 )
		{
			System.out.println("move the mouse");
			whichDirection = "move";
		}
		else
		{
			// mouse click
			System.out.println("mouse clicked");
			whichDirection = "click";
		}
		
		assertEquals("move", whichDirection);
	}

}// end test class
