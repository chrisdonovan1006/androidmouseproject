package itt.t00154755.mouseapp;

import java.io.IOException;

import android.bluetooth.BluetoothSocket;

public class ClientCommsThread extends Thread 
{
	private static final String TAG = "Client Comms Thread";
	private BluetoothSocket socket;
	private String acceloData;
	int nullPacketsOut;

	public ClientCommsThread(BluetoothSocket socket, String acceloData) 
	{
		System.out.println(TAG);
		this.socket = socket;
		this.acceloData = acceloData;
	}

	@Override
	public void run() 
	{
		try 
		{
			while (true) 
			{
				try 
				{   
					if( acceloData == null )
					{
						nullPacketsOut++;
					}
					else
					{
						socket.getOutputStream().write(acceloData.getBytes());
					}
				}
				catch (IOException e) 
				{
					// print the error stack
					e.printStackTrace();
					e.getCause();
					System.exit(-1);
				}
			}
		} 
		catch (Exception e) 
		{
			// print the error stack
			e.printStackTrace();
			e.getCause();
			System.exit(-1);

		} 
		finally 
		{
			try 
			{
				if (socket != null) 
				{
					socket.close();
				}

			} 
			catch (IOException e) 
			{
				// print the error stack
				e.printStackTrace();
				e.getCause();
				System.exit(-1);
			}
			
			System.exit(-1);
		}

	}
}