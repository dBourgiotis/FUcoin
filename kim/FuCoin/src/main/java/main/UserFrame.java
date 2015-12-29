package main;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import Actors.UserActor;
import Actors.UserActor.HelloMessage;
import Actors.UserActor.JoinRequest;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;

public class UserFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final User user;
	private JComboBox<String> textFieldRecipient;
	private JTextField textFieldMessage;
	private JTextField txtFieldIncomingMessage;
	private JTextField textFieldNewNeighbour;

	/**
	 * Create the frame.
	 */
	public UserFrame(ActorSystem actorSystem, String userName) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		JPanel description = new JPanel();
		FlowLayout flowLayout = (FlowLayout) description.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(description);

		Component horizontalStrut = Box.createHorizontalStrut(115);
		description.add(horizontalStrut);

		JLabel lblUserNameLabel = new JLabel("Name:");
		description.add(lblUserNameLabel);

		JTextField lblUserName = new JTextField("");
		lblUserName.setEditable(false);
		description.add(lblUserName);

		lblUserName.setText(userName);

		JPanel incomingMessage = new JPanel();
		contentPane.add(incomingMessage);
		incomingMessage.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		Component horizontalStrut_1 = Box.createHorizontalStrut(39);
		incomingMessage.add(horizontalStrut_1);

		JLabel lblIncomingMessage = new JLabel("Incoming Message:");
		incomingMessage.add(lblIncomingMessage);

		txtFieldIncomingMessage = new JTextField("");
		txtFieldIncomingMessage.setPreferredSize(new Dimension(150, 20));
		txtFieldIncomingMessage.setSize(new Dimension(150, 0));
		txtFieldIncomingMessage.setEditable(false);
		incomingMessage.add(txtFieldIncomingMessage);

		JPanel SendMessage = new JPanel();
		SendMessage
				.setBorder(new TitledBorder(null, "Send Message", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(SendMessage);
		SendMessage.setLayout(new BoxLayout(SendMessage, BoxLayout.Y_AXIS));

		JPanel panel_3 = new JPanel();
		SendMessage.add(panel_3);
		panel_3.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		horizontalStrut_4.setPreferredSize(new Dimension(40, 0));
		panel_3.add(horizontalStrut_4);

		JLabel lblRecipient = new JLabel("Recipient:");
		lblRecipient.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel_3.add(lblRecipient);

		textFieldRecipient = new JComboBox<String>();
		textFieldRecipient.setAlignmentX(Component.RIGHT_ALIGNMENT);
		panel_3.add(textFieldRecipient);

		JPanel panel_4 = new JPanel();
		SendMessage.add(panel_4);
		panel_4.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		horizontalStrut_3.setPreferredSize(new Dimension(40, 0));
		panel_4.add(horizontalStrut_3);

		JLabel lblMessage = new JLabel("Message:");
		lblMessage.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel_4.add(lblMessage);

		textFieldMessage = new JTextField();
		textFieldMessage.setPreferredSize(new Dimension(150, 20));
		panel_4.add(textFieldMessage);
		textFieldMessage.setColumns(10);

		JPanel panel = new JPanel();
		SendMessage.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 5));

		JButton btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String recipientUserName = (String) textFieldRecipient.getSelectedItem();
				String message = textFieldMessage.getText();
				user.sendMessage(recipientUserName, message);
			}
		});
		panel.add(btnSend);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		horizontalStrut_2.setPreferredSize(new Dimension(60, 0));
		panel.add(horizontalStrut_2);

		JPanel addNewNeighbour = new JPanel();
		addNewNeighbour.setBorder(
new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Add new Neighbour",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		contentPane.add(addNewNeighbour);
		addNewNeighbour.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));

		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		addNewNeighbour.add(horizontalStrut_5);

		JLabel lblName = new JLabel("Name:");
		addNewNeighbour.add(lblName);

		textFieldNewNeighbour = new JTextField();
		textFieldNewNeighbour.setPreferredSize(new Dimension(100, 20));
		addNewNeighbour.add(textFieldNewNeighbour);
		textFieldNewNeighbour.setColumns(10);

		JButton btnAddNeighbour = new JButton("Add Neighbour");
		btnAddNeighbour.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String newNeighbour = textFieldNewNeighbour.getText();
				user.sendJoinRequest(newNeighbour);
			}
		});

		Component horizontalStrut_6 = Box.createHorizontalStrut(20);
		addNewNeighbour.add(horizontalStrut_6);
		addNewNeighbour.add(btnAddNeighbour);
		user = new User(actorSystem, userName, this);

	}

	public class User {

		private final ActorSystem actorSystem;
		private final ActorRef userActor;
		private final String userName;
		private final UserFrame userFrame;

		public User(ActorSystem actorSystem, String userName, UserFrame userFrame) {
			this.actorSystem = actorSystem;
			this.userName = userName;
			this.userFrame = userFrame;
			userActor = actorSystem.actorOf(UserActor.props(this), userName);

		}

		public String getUserName() {
			return userName;
		}

		public void sendMessage(String recipientUserName, String message) {
			ActorSelection recipient = actorSystem.actorSelection("/user/" + recipientUserName);
			HelloMessage helloMessage = new HelloMessage(message);
			recipient.tell(helloMessage, userActor);
		}
		
		public void getMessage(String message, String sender) {
			userFrame.txtFieldIncomingMessage.setText(message);
		}

		public void sendJoinRequest(String userName) {
			ActorSelection recipient = actorSystem.actorSelection("/user/" + userName);
			JoinRequest request = new JoinRequest();
			recipient.tell(request, userActor);
		}

		public void addNeighbour(String neighbour) {
			textFieldRecipient.addItem(neighbour);
		}


	}

}
