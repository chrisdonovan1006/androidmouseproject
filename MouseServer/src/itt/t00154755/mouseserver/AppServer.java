package itt.t00154755.mouseserver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.bluetooth.LocalDevice;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class AppServer extends Thread{
	private final LocalDevice pcDevice;
	private final String connString = "btspp://localhost:"
			+ "0000000000000000000000000000ABCD;name=BtExample;"
			+ "authenticate=false;encrypt=false;master=false";

	public AppServer() throws IOException {
		pcDevice = LocalDevice.getLocalDevice();
	}

	public void run() {
		StreamConnectionNotifier connectionNotifier = null;
		try {
			connectionNotifier = (StreamConnectionNotifier) Connector
					.open(connString);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("accepting on " + pcDevice.getBluetoothAddress());
		StreamConnection streamConnection = null;
		try {
			streamConnection = connectionNotifier.acceptAndOpen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		InputStream dataIn = null;
		try {
			dataIn = new DataInputStream(streamConnection.openInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ServerCommsThread sct = new ServerCommsThread(dataIn);
		sct.start();
		
	}
}
