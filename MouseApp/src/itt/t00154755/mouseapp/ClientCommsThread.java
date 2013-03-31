package itt.t00154755.mouseapp;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.bluetooth.BluetoothSocket;

public class ClientCommsThread extends Thread
{
	private static final String	TAG	= "Client Comms Thread";
	private BluetoothSocket		socket;
	private String				acceloData;
	private int					nullPacketsOut;

	public ClientCommsThread ( BluetoothSocket socket, String acceloData )
	{
		System.out.println(TAG);
		this.socket = socket;
		this.acceloData = acceloData;
	}

	@Override
	public void run( )
	{
		try
		{
			while ( true )
			{
				try
				{
					if ( acceloData == null )
					{
						nullPacketsOut++;
						System.out.println("null packects recieved on the client side: " + nullPacketsOut);
						if (nullPacketsOut > 20000)
						{
							System.out.println("too many null packets");
							System.exit(-1);
						}
					} else
					{
						OutputStream dataOut = socket.getOutputStream();
						PrintWriter writeOut = new PrintWriter(dataOut);
						writeOut.print(acceloData);
						writeOut.flush();
					}
				} catch ( IOException e )
				{
					closeTheSocket();
					// print the error stack
					e.printStackTrace();
					e.getCause();
					System.exit(-1);
				}
			}
		} catch ( Exception e )
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);

		} finally
		{
			closeTheSocket();
		}
	}
	
	public void closeTheSocket()
	{
		if ( socket != null )
		{
			try
			{
				socket.close();
			} catch ( IOException e )
			{
				// print the error stack
				e.printStackTrace();
				e.getCause();
				System.exit(-1);
			}
		}
	}
}
