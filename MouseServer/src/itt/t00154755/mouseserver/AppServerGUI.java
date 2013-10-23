//package name
package itt.t00154755.mouseserver;

// imports used
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
 * also give a visual output as to whether or not the server is running.
 * 
 * 
 * @author Christopher Donovan
 * @since 26/04/2013
 * @version 4.06
 */
public class AppServerGUI
{
	// Global variables
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
					// try to open the window and display it to the user
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
	 * 
	 * Constructor to create an instance of the display 
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
		// instance of the runner class 
		// provides encapsulation
		appRunServer = new AppRunServer();
		
		// frame to be displayed
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// text field added to the screen
		serverText = new JTextField();
		serverText.setBounds(0, 0, 254, 261);
		
		// stop button added to the field
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
		
		// start button added to the field
		final JButton btnStartServer = new JButton("Start Server");
		btnStartServer.setBounds(264, 24, 160, 23);
		btnStartServer.addActionListener(new ActionListener()
		{
			public void actionPerformed( ActionEvent e )
			{
				// start the application
				appRunServer.startServer();
				serverText.setText("Server is Running");
			}
		});
		
		// set up the frame by added the button, and text field
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnStartServer);
		frame.getContentPane().add(btnStopServer);

		frame.getContentPane().add(serverText);
		serverText.setColumns(10);
	}
} // end of the GUI application
