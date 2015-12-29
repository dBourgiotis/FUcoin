package main;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import akka.actor.ActorSystem;

public class MainFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Main main;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame frame = new MainFrame();
					frame.pack();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel panel = new JPanel();
		panel.setBorder(
				new TitledBorder(null, "Create a new User", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel.setPreferredSize(new Dimension(300, 100));
		panel.setMinimumSize(new Dimension(500, 10));
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut_2);

		JLabel lblNewLabel_1 = new JLabel("Name:");
		panel_1.add(lblNewLabel_1);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panel_1.add(horizontalStrut_1);

		JTextField lblNewLabel = new JTextField("");
		lblNewLabel.setPreferredSize(new Dimension(150, 20));
		lblNewLabel.setMinimumSize(new Dimension(250, 20));
		lblNewLabel.setSize(new Dimension(50, 50));
		panel_1.add(lblNewLabel);

		Component verticalStrut = Box.createVerticalStrut(20);
		panel.add(verticalStrut);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JButton btnCreateNewUser = new JButton("Create");
		panel_2.add(btnCreateNewUser);
		btnCreateNewUser.setAlignmentX(Component.RIGHT_ALIGNMENT);
		btnCreateNewUser.setMinimumSize(new Dimension(250, 23));

		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel_2.add(horizontalStrut);
		btnCreateNewUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String username = lblNewLabel.getText();
				main.createNewUserFrame(username);
			}
		});
		main = new Main();
	}

	public class Main {

		private ActorSystem actorSystem;

		public Main() {
			actorSystem = ActorSystem.create("FUCoin");
		}

		public void createNewUserFrame(String username) {
			UserFrame userFrame = new UserFrame(actorSystem, username);
			userFrame.setVisible(true);
		}

	}

}
