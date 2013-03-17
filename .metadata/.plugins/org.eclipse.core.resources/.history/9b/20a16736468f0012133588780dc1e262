package itt.t00154755.mouseserver;

import java.io.DataInputStream;
import java.io.IOException;
import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class AppServer {
	private final LocalDevice pcDevice;
	private final String connString = "btspp://localhost:"
			+ "0000000000000000000000000000ABCD;name=BtExample;"
			+ "authenticate=false;encrypt=false;master=false";

	public static void main(String[] args) throws IOException,
			InterruptedException {
		new AppServer().initServer();
	}

	public AppServer() throws IOException {
		pcDevice = LocalDevice.getLocalDevice();
	}

	private void initServer() throws IOException {
		StreamConnectionNotifier connectionNotifier = (StreamConnectionNotifier) Connector
				.open(connString);
		System.out.println("accepting on " + pcDevice.getBluetoothAddress());
		StreamConnection streamConnection = connectionNotifier.acceptAndOpen();
		DataInputStream is = streamConnection.openDataInputStream();

		byte[] bytes = new byte[1024];
		int r;
		while ((r = is.read(bytes)) > 0) {
			System.out.println(new String(bytes, 0, r));
		}

	}
}
