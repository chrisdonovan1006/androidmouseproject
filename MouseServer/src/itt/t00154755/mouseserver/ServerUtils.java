package itt.t00154755.mouseserver;

/**
 * This is the utilities class for the server.
 *
 * @author Christopher Donovan
 * @version 4.06
 * @since 26/04/2013
 */
public abstract class ServerUtils {

    /**
     * This method will print the error message thrown by
     * the exception to the screen, and then exit the server
     * application.
     *
     * @param TAG
     * @param e
     */
    public void
    printOutExceptionDetails(String TAG, Exception e) {
        // print the error stack
        e.printStackTrace();
        e.getCause();
        System.out.println(TAG + " ...Shutting down the server " + e);
        System.exit(-1);
    }
}
