/**
 * 
 */
package itt.t00154755.mouseapp;

/**
 * @author Christopher Donovan
 * 
 *         Application communication interface, method to send the data back to the main UI Thread.
 * 
 */
public interface AppCommunication
{
	/**
	 * 
	 * @param data
	 *        String value to send back to main UI Thread.
	 */
	void sendDataToUIThread( String data );
	/**
	 * 
	 * @param data
	 *        integer value to send back to main UI Thread.
	 */
	void sendDataToUIThread( int data );
	
	/**
	 * 
	 * @param data
	 *        byte[] value to send back to main UI Thread.
	 */
	void sendDataToUIThread( byte[] data );
	
	/**
	 * 
	 * @param data
	 *        String value to send back to main UI Thread.
	 *        
	 * @param type
	 *        integer values to specify the type of data to be sent.
	 */
	void sendDataToUIThread( String data, int type );
}
