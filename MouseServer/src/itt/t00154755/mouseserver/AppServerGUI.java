package itt.t00154755.mouseserver;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;


/**
 * Built using the Swing Design Eclipse Plug-in
 * 
 * AppServerGUI allows the user to interact with the server, 
 * also give a visual output as to whether the server is running or not.
 * 
 * @author Christopher Donovan
 * @since 26/04/2013
 * @version 4.06
 */
public class AppServerGUI
{

	private JFrame frame;
	private AppRunServer appRunServer;
	private JTextField serverText;


	/**
	 * Launch the application.
	 */
	public static void main( String[] args )
	{
		EventQueue.invokeLater(new Runnable()
		{

			public void run()
			{
				try
				{
					AppServerGUI window = new AppServerGUI();
					window.frame.setVisible(true);
				}
				catch ( Exception e )
				{
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the application.
	 */
	public AppServerGUI()
	{
		initialize();
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{

		appRunServer = new AppRunServer();
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		serverText = new JTextField();
		serverText.setBounds(0, 0, 254, 261);

		final JButton btnStopServer = new JButton("Stop Server");
		btnStopServer.setBounds(264, 192, 160, 23);

		btnStopServer.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed( ActionEvent e )
			{
				// terminate the program
				System.out.println("Closing the server...");
				System.exit(0);
			}

		});
		final JButton btnStartServer = new JButton("Start Server");
		btnStartServer.setBounds(264, 24, 160, 23);
		btnStartServer.addActionListener(new ActionListener()
		{

			public void actionPerformed( ActionEvent e )
			{
				appRunServer.startServer();
				serverText.setText("Server is Running");
			}
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnStartServer);
		frame.getContentPane().add(btnStopServer);

		frame.getContentPane().add(serverText);
		serverText.setColumns(10);

	}
}
