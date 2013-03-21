package itt.t00154755.mouseserver;

import java.io.IOException;
import java.io.InputStream;


public class ServerCommsOut extends Thread 
{
		// class variables
		private static final String TAG = "Server Communication Thread";
		private ServerUtils sUtils = new ServerUtils();
		private InputStream dataIn;
		private CursorRobot cRobot;

		/**
		 * 
		 * AppServer constructor initializes a new server once it is calls.
		 * 
		 * @param dataIn 
		 * 		the InputStream that was opened in the Server Class.
		 * 
		 */
		public ServerCommsOut(InputStream dataIn) 
		{
			sUtils.info( TAG );
			this.dataIn = dataIn;
		}
		
		/**
		 * 
		 * Override the run method of the Thread Class.
		 */
		@Override
		public void run() 
		{
			// create a sort of infinite loop that runs
			// while the program is running

			// read in the bytes
			try {
				String acceloData;
				// create a new byte array
				byte[] buffer = new byte[1024];
				int bytes;
				
				while ((bytes = dataIn.read(buffer)) > 0) 
				{
					acceloData = (new String(buffer, 0, bytes));
						
					cRobot.sendToRobot(acceloData);
					System.out.println(TAG + " Send to the Robot");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}// end of run methods
}// end of Class