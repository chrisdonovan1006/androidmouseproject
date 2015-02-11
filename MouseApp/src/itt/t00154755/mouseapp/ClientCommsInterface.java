/**
 * 
 */
package itt.t00154755.mouseapp;

/**
 * @author Christopher Donovan
 * @author ClientCommsInterface.java
 * @since 10/02/2015
 * @version 2.0
 * 
 *         Application communication interface, method to send the data back to the main UI Thread.
 * 
 */
public interface ClientCommsInterface
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
	
	/**
	 * 
	 * @param data
	 *        String value to send back to output stream.
	 */
	void sendDataToOutputStream( String data );
	/**
	 * 
	 * @param data
	 *        integer value to send back to output stream.
	 */
	void sendDataToOutputStream( int data );
	
	/**
	 * 
	 * @param data
	 *        byte[] value to send back to output stream.
	 */
	void sendDataToOutputStream( byte[] data );
}
