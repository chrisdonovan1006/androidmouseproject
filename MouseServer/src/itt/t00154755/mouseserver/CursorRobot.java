package itt.t00154755.mouseserver;

import java.awt.AWTException;
import java.awt.Robot;

public class CursorRobot {
	String accelData;
	public void sendToRobot(String acceloData) {
		// TODO Auto-generated method stub
		
	}
	
	public CursorRobot() {
		// empty constructor
	    }

	    /**
	     * this method is call on the Class object in order to start the running.
	     * 
	     * e.g. robot.execute()
	     */
	    public void execute() {
		new Thread(new MouseMoveThread()).start();
	    }

	    /*
	     * this thread will allow the data passed from the accelerometer to control
	     * the on screen cursor via the Robot object
	     */
	    private class MouseMoveThread extends Thread {
		//
		Robot robot;


		@Override
		public void run() {

		    int x = (int) xo;
		    int y = (int) yo;
		    while (true)
			try {
			    robot = new Robot();
			    robot.mouseMove(x, y);
			    System.out.println("\nmouse started at: " + x + " : " + y);

			} catch (AWTException eAWT) {
			    eAWT.printStackTrace();
			} catch (Exception e) {
			    e.printStackTrace();
			}

		}// end of loop
	    }// end of thread

}
