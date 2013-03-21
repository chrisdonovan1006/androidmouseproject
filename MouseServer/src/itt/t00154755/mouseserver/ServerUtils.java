package itt.t00154755.mouseserver;

/**
 * @author Christopher Donovan
 * <p>
 * This is a utility class used to store some methods.
 * 
 */
public class ServerUtils 
{
	/**
	 * Used to output an error message to the screen.
	 * 
	 * @param tag 
	 * 		each class has a String name to identify errors or info.
	 * @param e
	 * 		the Exception that is being thrown.
	 * @param e
	 * 		the position of the error (eg. 1st, 2nd, etc..).
	 */
	public void error( String tag, Exception e, int position ) 
	{
		System.err.print( "...Exception: " + tag + "\n" + e.getMessage() + "\n" 
				+ e.getCause() + "\n" + position + "\n" );
		
	}// end of error message
	
	/**
	 * Used to output a message to the screen.
	 * 
	 * @param s
	 * 		the String that will be output to the screen.
	 */
	public void info( String s ) 
	{
		System.out.print( "..."+ s + "\n");
	}// end of info methods
	
}// end of class