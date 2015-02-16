package itt.t00154755.mouseserver;

/**
 * @author Christopher Donovan
 * @author RunServer.java
 * @version 4.06
 * @since 26/04/2013
 */
public class RunServer {

    /**
     * Constructor:
     * used by the ServerGUI GUI to start the server.
     */
    public void startServer() {
        // create a new thread that calls the runnable object.
        Thread runningServer = new Thread(new Server());
        // start the Thread
        runningServer.start();
    }

}// end of main method
