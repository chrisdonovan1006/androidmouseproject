// packages
package itt.t00154755.mouseserver;

// imports
import java.awt.AWTException;
import java.awt.Robot;

/**
 * 
 * @author Christopher Donovan
 * 
 */
public class CursorRobot 
{
	private static final String TAG = "Server Communication Thread";
	String acceloData;
	int moveX = 640;
	int moveY = 400;

	public void sendToRobot(String acceloData)
	{
		this.acceloData = acceloData;
	}

	private synchronized int[] covertStringToIntArray(String acceloData) 
	{
		String delims = "[,]";
		String[] tokens = acceloData.split(delims);
		int[] data = new int[tokens.length];

		for (int i = 0; i < tokens.length; i++) 
		{
			try
			{
				data[i] = Integer.parseInt(tokens[i]);
			} 
			catch (NumberFormatException nfe) 
			{
				// print the error stack
				nfe.printStackTrace();
				nfe.getCause();
				System.exit(-1);
			}
		}
		return data;
	}

	public CursorRobot() 
	{
		execute();
	}

	/**
	 * this method is call on the Class object in order to start the running.
	 * 
	 * e.g. robot.execute()
	 */
	public void execute() 
	{
		new Thread(new MouseMoveThread()).start();
	}

	/*
	 * this thread will allow the data passed from the accelerometer to control
	 * the on screen cursor via the Robot object
	 */
	private class MouseMoveThread extends Thread 
	{
		//
		Robot robot;

		@Override
		public void run() 
		{
			while (true) 
			{
				if (acceloData.length() == 0 || acceloData == "")
				{
					System.out.println("Waiting for data");
				} 
				else
				{
					int[] accData = covertStringToIntArray(acceloData);
					int x = accData[0];
					int y = accData[1];
					

					if (accData.length == 0 || accData == null) 
					{
						System.out.println(TAG + "Waiting for data");
					} 
					else 
					{
						try 
						{
							robot = new Robot();
								moveX += x;
								moveY += y;

								Thread.sleep(100);
								robot.mouseMove(moveX, moveY);

								System.out.println("mouse started at: " + x
										+ " : " + y);
						} 
						catch (AWTException eAWT) 
						{
							// print the error stack
							eAWT.printStackTrace();
							eAWT.getCause();
							System.exit(-1);
						} 
						catch (Exception e) 
						{
							// print the error stack
							e.printStackTrace();
							e.getCause();
							System.exit(-1);
						}
					}
				}// end of loop
			}
		}
	}
}// end of class

