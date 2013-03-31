package itt.t00154755.mouseapp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import android.bluetooth.BluetoothSocket;

public class ClientCommsThread extends Thread
{
	private static final String	TAG	= "Client Comms Thread";
	private BluetoothSocket		socket;
	private String				acceloData;
	private boolean				isRunning;
	private InputStream			dataIn;

	public ClientCommsThread ( BluetoothSocket socket, String acceloData )
	{
		System.out.println(TAG);
		this.socket = socket;
		this.acceloData = acceloData;
	}

	@Override
	public void run( )
	{
		String serverData = null;
		try
		{
			serverData = readInDataFromTheServer();
		} catch ( IOException e1 )
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		isRunning = true;
		while ( isRunning )
		{
			if ( serverData != null )
			{
				System.out.println("server is waiting");
				isRunning = false;
			} else
			{
				try
				{
					sendDataToTheServer();
				} catch ( IOException e )
				{
					//
					e.printStackTrace();
				}finally
				{
					closeTheSocket();
				}

			}
		}

	}

	/**
	 * @throws IOException
	 */
	private void sendDataToTheServer( ) throws IOException
	{
		OutputStream dataOut = socket.getOutputStream();
		PrintWriter writeOut = new PrintWriter(dataOut);
		writeOut.print(acceloData);
		writeOut.flush();
	}

	private String readInDataFromTheServer( ) throws IOException
	{
		dataIn = socket.getInputStream();
		BufferedReader buffIn = new BufferedReader(
				new InputStreamReader(dataIn));
		isRunning = true;
		String serverData = null;
		while ( isRunning )
		{
			if ( buffIn == null )
			{
				System.out.println("buff in client is empty");
				isRunning = false;
			} else
			{
				try
				{
					System.out.println("read line");
					serverData = buffIn.readLine();
					System.out.println("send to robot");

				} catch ( IOException e )
				{
					//
					e.printStackTrace();
				}
			}
		}
		return serverData;
	}

	public void closeTheSocket( )
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
