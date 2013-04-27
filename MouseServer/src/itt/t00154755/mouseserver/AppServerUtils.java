package itt.t00154755.mouseserver;

/**
 * 
 * This is the utilities class for the server.
 * 
 * @author Christopher Donovan
 * @since 26/04/2013
 * @version 4.06
 *
 */
public abstract class AppServerUtils
{

	/**
	 * This method will print the error message thrown by
	 * the exception to the screen, and then exit the server
	 * application.
	 * 
	 * @param Tag
	 *        the name of the class calling this method
	 * @param e
	 *        the exception being thrown
	 * @param errorNum
	 *        the user defined error number
	 */
	public void
			printOutExceptionDetails( String TAG, Exception e, int errorNum )
	{
		// print the error stack
		e.printStackTrace();
		e.getCause();
		System.out.println(TAG + " ...Shutting down the server " + errorNum);
		System.exit(-1);
	}
}
