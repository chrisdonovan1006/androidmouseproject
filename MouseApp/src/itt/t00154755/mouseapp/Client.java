package itt.t00154755.mouseapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

/**
 * @author Christopher Donovan
 * @author Client.java
 * @version 2.0
 * @category The Client class runs in the background and is used to create the connection
 * to the server which must be running or a IOException will be thrown.
 * <p/>
 * The main activity calls the connectToServer(), this class then tries to establish a
 * connect visa the blue-tooth socket using a random UUID, if successful the socket
 * will be connected using the RFCOMM protocol designed for peer to peer blue-tooth connections.
 * <p/>
 * Android Developers Web-site
 * <p/>
 * {@link http://developer.android.com/guide/components/services.html}
 * {@link http://developer.android.com/guide/topics/sensors/sensors_overview.html}
 * Professional Android 2 Application Development
 * <p/>
 * Charter 14 - Blue-tooth, Networks, and WiFi {@link http:
 * //www.wrox.com/WileyCDA/WroxTitle/Professional-Android-2-Application-Development.productCd-0470565527.html}
 * <p/>
 * The New Boston Android Video Tutorials
 * <p/>
 * {@link http://thenewboston.org/list.php?cat=6}
 * @since 10/02/2015
 */

public class Client {

    // used for debugging
    private static final String TAG = "App Client";
    private static final boolean D = true;

    // used to keep track of the current state of the client
    public static final int WAITING = 0;
    public static final int CONNECTED = 1;
    // public static final int FAILED = -1;
    // private final int RUNNING = 2;

    // class fields
    private BluetoothAdapter btAdapter;
    private ClientCommsThread clientCommThread;
    private int state;
    private Handler appHandler;

    /**
     * @param appHandler the main UI threads handler
     */
    public Client(Handler appHandler) {
        if (D) {
            Log.d(TAG, "getting default adapter");
        }
        clientCommThread = null;
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // set the default state to waiting
        state = WAITING;
        // This is the main UI thread handler that will allow
        // the client to communicate with the main activity
        this.appHandler = appHandler;
    }

    // starts the connectToServer process
    public void start() {
        connectToSever();
    }


    /**
     * this method connects to the server it opens an RFCOMM serial port connection
     * over TCP this ensure that the data
     */
    private void connectToSever() {
        BluetoothDevice btDevice;
        BluetoothSocket btSocket = null;

        if (D) {
            Log.d(TAG, "getting local device");
        }
        // find the bluetooth address of the current device
        //String btAddress = "EC:A8:6B:6E:40:91";
        String btAddress = "00:15:83:3D:0A:57";
        btDevice = btAdapter.getRemoteDevice(btAddress);
        Log.i(TAG, "Remote Device Address: " + btAddress);

        // check to ensure that the bluetooth is turned on
        // must cancel the discovery process before trying to connect
        // as the discovery process is resource heavy
        btAdapter.cancelDiscovery();

        if (D) {
            Log.d(TAG, "connecting to server");
        }
        try {
            /*
			 * This method creates an RFComm connection with the server,
			 * because I am using the createRfcommSocketToServiceRecord and not
			 * the createInscureRfcommSocketToServiceRecord it means that the users
			 * phone must be bonded/paired with the server host machine. I have implemented
			 * it using this method as the user will be accessing the Robot Class on the server
			 * machine.
			 * 
			 * RFComm provides a secure SPP connection over TCP which offers a better
			 * connection than UDP. The UUID that i am using was generated here
			 * 
			 * @link http://www.famkruithof.net/uuid/uuidgen
			 * 
			 * more on RFComm here:
			 * 
			 * @link http://developer.android.com/reference/android/bluetooth/BluetoothDevice.html#createRfcommSocketToServiceRecord(java.util.UUID)
			 * 
			 * @link http://developer.bluetooth.org/TechnologyOverview/Pages/RFCOMM.aspx
			 * 
			 * @link http://en.wikipedia.org/wiki/Bluetooth_protocols#Radio_frequency_communication_.28RFCOMM.29
			 */
            btSocket = btDevice.createRfcommSocketToServiceRecord(UUID.fromString("5a17e500-ad3a-11e2-9e96-0800200c9a66"));
        } catch (IOException e) {
            if (D) {
                Log.e(TAG, "ERROR creating the RFCOMM connection");
            }
            e.printStackTrace();
        }

        if (D) {
            Log.d(TAG, "about to connect");
        }

        while (state == WAITING) {
            try {
                btSocket.connect();
            } catch (IOException e1) {
                if (D) {
                    Log.e(TAG, "ERROR: connecting to the bluetooth socket");
                }
                e1.printStackTrace();
            }

            if (btSocket.isConnected()) {
                if (D) {
                    Log.d(TAG, "Connected!");
                }
                sendConnectedToastToUIHandler();
                setState(CONNECTED);
            }
        }

        try {
            // forward the connected socket to the comms thread
            createClientCommThread(btSocket);
        } catch (IOException e) {
            if (D) {
                Log.e(TAG, "ERROR creating the client comms thread");
            }
            e.printStackTrace();
        }
    }

    /**
     * @return the state
     * the current state of the connection process
     */
    public synchronized int getState() {
        return state;
    }

    /**
     * @param stateIn the state to set
     */
    public synchronized void setState(int stateIn) {
        if (D) {
            Log.i(TAG, "state : " + state + " => to: " + stateIn);
        }
        state = stateIn;
    }

    /**
     * This method creates a new Thread based on the Inner
     * class ClientCommThread which takes in the blue-tooth socket
     * as a parameter.
     *
     * @param btSocket the currently open blue-tooth socket
     * @throws IOException if the socket is not open
     */
    public synchronized void createClientCommThread(BluetoothSocket btSocket) throws IOException {
        // close any running threads
        if (clientCommThread != null) {
            clientCommThread.cancel();
        }

        // start a new thread
        clientCommThread = new ClientCommsThread(btSocket);
        clientCommThread.start();
    }

    /**
     * Example taken from the BluetoothChat.java program
     *
     * @param acceloData a byte[] representation of the accelerometer data reading
     */
    public synchronized void write(byte[] acceloData) {
        // method referenced
        if (D) {
            Log.i(TAG, "Pass the data to the thread");
        }
        // Create temporary object
        ClientCommsThread cct;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (state != CONNECTED) {
                return;
            }

            cct = clientCommThread;
        }
        // Perform the write unsynchronized
        cct.sendDataToOutputStream(acceloData);
    }

    /**
     * @param options a byte[] representation of the accelerometer data reading
     */
    public synchronized void write(int options) {
        // method referenced
        if (D) {
            Log.i(TAG, "Pass the data to the thread");
        }
        // Create temporary object
        ClientCommsThread cct;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
            if (state != CONNECTED) {
                return;
            }

            cct = clientCommThread;
        }
        // Perform the write unsynchronized
        cct.sendDataToUIThread(options);
    }

    /**
     * This method will cancel any running clientCommThreads.
     */
    public void closeClient() {
        if (clientCommThread != null) {
            clientCommThread.cancel();
            clientCommThread = null;
        }
        setState(WAITING);
    }

    /**
     * sends a toast back to the UI Handler once the connection is made
     */
    private void sendConnectedToastToUIHandler() {
        Message message = appHandler.obtainMessage(MainClientActivity.MESSAGE_TOAST_CLIENT);
        Bundle bundle = new Bundle();
        bundle.putString(MainClientActivity.TOAST, "The device is now connected to the server");
        message.setData(bundle);
        appHandler.handleMessage(message);
    }

    /**
     * @param name
     *        the friendly name of the connected blue-tooth device
     */
	/*
	 * private void sendDeviceNameToUIHandler( String name )
	 * {
	 * // message back to UI
	 * Message message = appHandler.obtainMessage(MainClientActivity.MESSAGE_DEVICE_NAME);
	 * Bundle bundle = new Bundle();
	 * bundle.putString(MainClientActivity.DEVICE_NAME, name);
	 * message.setData(bundle);
	 * appHandler.handleMessage(message);
	 * }
	 */

    /**
     * @author Christopher Donovan
     *         <p/>
     *         This the private inner class that extends Thread
     *         each time this class is called a new thread is created.
     *         <p/>
     *         This class creates an Output Stream using the btSocket
     *         and fires the data to the server.
     */
    private class ClientCommsThread extends Thread implements ClientCommsInterface {
        private static final String TAG = "Client Comms Thread";
        private BluetoothSocket btSocket = null;
        private final OutputStream outStream;

        private static final String EXITSERVERCODE = "0";

        /*
         * Class constructor takes a BluetoothSocket as parameter.
         */
        public ClientCommsThread(BluetoothSocket btSocket) {
            this.btSocket = btSocket;
            OutputStream tmpOut = null;
            if (D) {
                Log.i(TAG, "+++ SET UP THE CLIENT COMM THREAD +++");
            }
            try {
                tmpOut = btSocket.getOutputStream();
            } catch (IOException e) {
                if (D) {
                    Log.e(TAG, "ERROR trying to open the output stream");
                }
            }
            outStream = tmpOut;
        }

        /**
         * close the btSocket and cancel the thread
         */
        public synchronized void cancel() {
            sendDataToOutputStream(EXITSERVERCODE);
            try {
                if (btSocket != null) {
                    btSocket.close();
                    btSocket = null;
                }

                if (clientCommThread != null) {
                    clientCommThread = null;
                }
            } catch (IOException e) {
                if (D)
                    Log.e(TAG, "Cancelling the Client Comm Thread");
                if (D)
                    Log.e(TAG, "ERROR: closing the socket");
                e.printStackTrace();
            }
        }

        /**
         * ClientCommsInterface method to transfer data form this class back to the MainClientActivity class.
         *
         * @param data String representation of the pop menu options
         */
        @Override
        public void sendDataToOutputStream(String data) {
            DataOutputStream dataOutStream = new DataOutputStream(outStream);
            try {
                dataOutStream.writeBytes(data);
            } catch (IOException e) {
                if (D) {
                    Log.e(TAG, "ERROR: while writing the string to the server");
                }
                e.printStackTrace();
                cancel();
                try {
                    outStream.close();
                    cancel();
                } catch (IOException e1) {
                    if (D) {
                        Log.e(TAG, "ERROR: trying to close the string dataoutstream");
                    }
                }
            } catch (Exception e2) {
                if (D) {
                    Log.e(TAG, "ERROR: trying to write to the server");
                }
                cancel();
            }
        }

        /**
         * ClientCommsInterface method to transfer data form this class back to the MainClientActivity class.
         *
         * @param data int representation of the pop menu options
         */
        @Override
        public void sendDataToOutputStream(int data) {
            DataOutputStream dataOutStream = new DataOutputStream(outStream);
            try {
                dataOutStream.writeInt(data);
            } catch (IOException e) {
                if (D) {
                    Log.e(TAG, "ERROR: while writing int to the server");
                }
                e.printStackTrace();
                cancel();
                try {
                    outStream.close();
                    cancel();
                } catch (IOException e1) {
                    if (D) {
                        Log.e(TAG, "ERROR: trying to close the int dataoutstream");
                    }
                }
            } catch (Exception e2) {
                if (D) {
                    Log.e(TAG, "ERROR: trying to write to the server int");
                }
                cancel();
            }
        }

        /**
         * ClientCommsInterface Interface method transfers data to the calling class.
         *
         * @param acceloData the byte[] representation of the accelerometer data
         */
        @Override
        public void sendDataToOutputStream(byte[] data) {
            try {
                outStream.write(data);
            } catch (IOException e) {
                if (D) {
                    Log.e(TAG, "ERROR: writing to the server!");
                }
                cancel();
                try {
                    outStream.close();
                    cancel();
                } catch (IOException e1) {
                    if (D)
                        Log.e(TAG, "ERROR: closing outstream!");
                }
            } catch (Exception e2) {
                if (D) {
                    Log.e(TAG, "ERROR: writing to the server!");
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cancel();
            }
        }

        // ++++++++Inherited Methods Currently Not Implemented++++++++
        @Override
        public void sendDataToUIThread(String data, int type) {
        }

        @Override
        public void run() {
        }

        @Override
        public void sendDataToUIThread(byte[] data) {
        }

        @Override
        public void sendDataToUIThread(int data) {
        }

        @Override
        public void sendDataToUIThread(String data) {
        }
    }
}// end of the class
