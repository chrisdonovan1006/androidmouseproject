package itt.t00154755.mouseserver;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class ServerView
{

	private JFrame frame;
	private RunServerListener runServerListener;
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
					ServerView window = new ServerView();
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
	public ServerView()
	{
		initialize();
	}


	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize()
	{

		runServerListener = new RunServerListener();
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
			public void actionPerformed( ActionEvent arg0 )
			{
				// TODO Auto-generated method stub

			}

		});
		final JButton btnStartServer = new JButton("Start Server");
		btnStartServer.setBounds(264, 24, 160, 23);
		btnStartServer.addActionListener(new ActionListener()
		{

			public void actionPerformed( ActionEvent e )
			{
				runServerListener.startServer();
				serverText.setText("server is running");
			}
		});
		frame.getContentPane().setLayout(null);
		frame.getContentPane().add(btnStartServer);
		frame.getContentPane().add(btnStopServer);

		frame.getContentPane().add(serverText);
		serverText.setColumns(10);

	}
}
