package itt.t00154755.mouseapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

/**
 * @author Christopher Donovan
 * @author AccelerometerService.java, extends Service implements SensorEventListener, ClientCommsInterface
 * @version 2.0
 *          <p/>
 *          This service runs in the background for the life of the application. It contains the
 *          Accelerometer sensor which returns an event each time the sensor changes (i.e. phone
 *          tilted to left, right, up or down). The event contains an integer array with the values
 *          of the x, y, and z axis. The service sends the data back to the main UI thread through
 *          a handler this data is then passed on to the client which in turn forwards it to the server.
 * @since 10/02/2015
 */

public class AccelerometerService extends Service implements SensorEventListener, ClientCommsInterface {
    // sensor movement direction
    public static final int LEFT_DOWN = 4;
    public static final int RIGHT_UP = 3;
    public static final int LEFT_UP = 2;
    public static final int RIGHT_DOWN = 1;

    private static final String TAG = "Accelerometer Service";
    private static final boolean D = false;

    private static final double G_THRESHOLD = 0.6;
    private static final int TOAST = 1;
    private String accelerometerData;
    private Context context;
    private Handler appHandler;
    private SensorManager accelerometerManager;
    private Sensor accelerometerSensor;
    private boolean isRegistered = false;
    private long lastUpdate = 0;


    /**
     * Default AccelerometerService Constructor
     */
    public AccelerometerService() {
    }

    /**
     * AccelerometerService Constructor
     *
     * @param context    the global application interface
     * @param appHandler allow data transfer to the main UI
     */
    public AccelerometerService(Context context, Handler appHandler) {
        this.context = context;
        this.appHandler = appHandler;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // return start_not_sticky
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        // initiate the service
        super.onCreate();
        initAccelerometerService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        endAccelerometerService();
    }

    public void initAccelerometerService() {
        sendDataToUIThread("AccelerometerService started", TOAST);
        registerListener();
    }

    public void endAccelerometerService() {
        unregisterListener();
        stopSelf();
    }

    /**
     * @return the accelerometerData
     */
    public String getAccelerometerData() {
        return accelerometerData;
    }

    /**
     * @param accelerometerData the accelerometerData to set
     */
    public void setAccelerometerData(String accelerometerData) {
        this.accelerometerData = accelerometerData;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * This method receives receives an update of the current sensor event.
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (D) {
            Log.d(TAG, "+++ SENSOR CHANGE +++");
        }

        determinePhonePosition(event.values[0], event.values[1]);
    }

    /**
     * ClientCommsInterface method to transfer data form this class back to the MainClientActivity class.
     */
    @Override
    public void sendDataToUIThread(String data) {
        Message message = appHandler.obtainMessage(MainClientActivity.MESSAGE_DATA_ACCELEROMETER);
        Bundle bundle = new Bundle();
        bundle.putString(MainClientActivity.DATA, data);
        message.setData(bundle);
        appHandler.handleMessage(message);
    }


    /**
     * ClientCommsInterface method to transfer data form this class back to the MainClientActivity class.
     * <p/>
     * <ul>
     * <li>Type:
     * <li>TOAST: A short message displayed on the user screen.
     * <li>DATA: A reading taken from the Accelerometer.
     * </ul>
     */
    @Override
    public void sendDataToUIThread(String data, int type) {
        if (type == 1) {
            Message message = appHandler.obtainMessage(MainClientActivity.MESSAGE_TOAST_ACCELEROMETER);
            Bundle bundle = new Bundle();
            bundle.putString(MainClientActivity.TOAST, data);
            message.setData(bundle);
            appHandler.handleMessage(message);
        }
    }

    /**
     * This method is used by the calling class to determine if the service has being registered
     * already.
     *
     * @return isRegistered
     * returns the current value of isRegistered:
     * true if the sensor manager has been registered, otherwise false
     */
    public boolean isRegistered() {
        return isRegistered;
    }

    /**
     * ClientCommsInterface method to transfer data form this class back to the MainClientActivity class.
     */
    @Override
    public void sendDataToUIThread(int data) {
    }

    @Override
    public void sendDataToUIThread(byte[] data) {
    }

    @Override
    public void sendDataToOutputStream(String data) {
    }

    @Override
    public void sendDataToOutputStream(int data) {
    }

    @Override
    public void sendDataToOutputStream(byte[] data) {
    }

    /*
     * In order for the service to listen for changes to the sensor data, the sensor must first be
     * registered to the service.  The sensors are accessed through the final static type using
     * the sensor name.
     */
    private void registerListener() {
        // register the listener
        if (D) {
            Log.d(TAG, "+++ REGISTER-LISTENER FOR SENSOR +++");
        }

        accelerometerManager = getAccelerometerManager();
        // check to make sure that the SensorList is not empty
        if (accelerometerManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0) {
            // set service as registered
            isRegistered = true;
            // get the Accelerometer Sensor
            accelerometerSensor = accelerometerManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            // register the listener to the Sensor
            accelerometerManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else
            Log.e(TAG, "Sensor could not be registered!!!");
    }

    /**
     * This method returns the Sensor manager object to the service, the context
     * is the main class that has been passed into the constructor of the service.
     * As the service runs in the background it needs to refer to the main class context
     * this allows the service to runs in the background and access the current position
     * of the phone through the accelerometer sensor.
     *
     * @return the accelerometerManager
     */
    private SensorManager getAccelerometerManager() {
        // get the system service using the main class reference, return the manager object
        return (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /*
     * Method that will unregister the Listener from
     * the Accelerometer sensor.
     */
    private void unregisterListener() {
        if (D) {
            Log.d(TAG, "+++ UNREGISTER-LISTENER FOR SENSOR +++");
        }
        if (isRegistered) {
            accelerometerManager.unregisterListener(this, accelerometerSensor);
        }
    }

    /*
     * Using the filters applied the next task is to determine the
     * current position of the sensors values.
     *
     * @param xFloatAxis
     *
     * @param yFloatAxis
     */
    private void determinePhonePosition(float xFloatAxis, float yFloatAxis) {
        if (xFloatAxis < G_THRESHOLD && yFloatAxis > G_THRESHOLD) {
            if (D) {
                Log.d(TAG, "move mouse: " + RIGHT_UP);
            }
            setAccelerometerData(" " + RIGHT_UP + " " + Math.abs((int) xFloatAxis) + " " + Math.abs((int) yFloatAxis) + " ");
        } else if (xFloatAxis < G_THRESHOLD && yFloatAxis > G_THRESHOLD) {
            if (D) {
                Log.d(TAG, "move mouse: " + LEFT_UP);
            }
            setAccelerometerData(" " + LEFT_UP + " " + Math.abs((int) xFloatAxis) + " " + Math.abs((int) yFloatAxis) + " ");
        } else if (xFloatAxis < G_THRESHOLD && yFloatAxis < G_THRESHOLD) {
            if (D) {
                Log.d(TAG, "move mouse: " + RIGHT_DOWN);
            }
            setAccelerometerData(" " + RIGHT_DOWN + " " + Math.abs((int) xFloatAxis) + " " + Math.abs((int) yFloatAxis) + " ");
        } else if (xFloatAxis > G_THRESHOLD && yFloatAxis > G_THRESHOLD) {
            if (D) {
                Log.d(TAG, "move mouse: " + LEFT_DOWN);
            }
            setAccelerometerData(" " + LEFT_DOWN + " " + Math.abs((int) xFloatAxis) + " " + Math.abs((int) yFloatAxis) + " ");

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                lastUpdate = curTime;
                sendCurrentReadingsToUI(getAccelerometerData());
            }
        }
    }

    /*
     * Create a new Thread that begin passing the data to the main UI
     * Thread.
     */
    private void sendCurrentReadingsToUI(String data) {
        SendDataThread sendDataThread = new SendDataThread(data);
        // create a new thread and start sending the data to the main UI thread
        Thread sendData = new Thread(sendDataThread); // Thread created
        sendData.start(); // Thread started
    }

    /*
     * private inner class that implements the Runnable interface.
     */
    private class SendDataThread implements Runnable {

        String data;

        public SendDataThread(String data) {
            this.data = data;
        }

        // send the data
        public void run() {
            if (data == null)
                return;
            sendDataToUIThread(data);
        }
    }
}// end class
