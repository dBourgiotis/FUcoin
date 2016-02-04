package user;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import user.UserControl.UserStaticContext;

public class UserFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final UserControl userControl;
	private JLabel balanceField;
	private JTextField newUserNameField;
	private JTextField newAddressField;
	private JList neighboursList;
	private JTextField transactionUserNameField;
	private JTextField transactionAmountField;


	/**
	 * Create the frame.
	 */
	public UserFrame(UserStaticContext context) {
		this.userControl = context.getUserControl();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 830, 387);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

		JPanel static_info = new JPanel();
		static_info
				.setBorder(new TitledBorder(null, "Static Info", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(static_info);
		static_info.setLayout(new BoxLayout(static_info, BoxLayout.Y_AXIS));

		JPanel panel = new JPanel();
		static_info.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblUsername = new JLabel("Username:");
		panel.add(lblUsername);

		JLabel userNameField = new JLabel("" + context.getUserName());
		panel.add(userNameField);

		JPanel panel_1 = new JPanel();
		static_info.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblBalance = new JLabel("Balance:");
		panel_1.add(lblBalance);

		balanceField = new JLabel("" + context.getBalance());
		panel_1.add(balanceField);

		JPanel panel_2 = new JPanel();
		static_info.add(panel_2);
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblAddress = new JLabel("Address:");
		panel_2.add(lblAddress);

		JLabel addressField = new JLabel(context.getAddress().path().toString());
		panel_2.add(addressField);

		JPanel addressBook_panel = new JPanel();
		addressBook_panel
				.setBorder(new TitledBorder(null, "Address Book", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(addressBook_panel);
		addressBook_panel.setLayout(new BoxLayout(addressBook_panel, BoxLayout.Y_AXIS));

		JPanel panel_4 = new JPanel();
		addressBook_panel.add(panel_4);

		JPanel panel_5 = new JPanel();
		panel_4.add(panel_5);

		JLabel lblUsername_1 = new JLabel("Username:");
		panel_5.add(lblUsername_1);

		newUserNameField = new JTextField();
		panel_5.add(newUserNameField);
		newUserNameField.setColumns(10);

		JPanel panel_6 = new JPanel();
		panel_4.add(panel_6);

		JLabel lblAddress_1 = new JLabel("Address (optional):");
		panel_6.add(lblAddress_1);

		newAddressField = new JTextField();
		panel_6.add(newAddressField);
		newAddressField.setColumns(10);

		JButton btnAdd = new JButton("Add");
		panel_4.add(btnAdd);
		btnAdd.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String userName = newUserNameField.getText();
				String address = newAddressField.getText();
				userControl.addNewNeighbour(userName, address);
			}
		});

		JPanel panel_3 = new JPanel();
		addressBook_panel.add(panel_3);
		panel_3.setLayout(new BoxLayout(panel_3, BoxLayout.Y_AXIS));

		JLabel lblNewLabel = new JLabel("Neighbours");
		panel_3.add(lblNewLabel);

		neighboursList = new JList<String>();
		panel_3.add(neighboursList);

		JPanel transaction_panel = new JPanel();
		transaction_panel
				.setBorder(new TitledBorder(null, "Transaction", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPane.add(transaction_panel);

		JPanel panel_7 = new JPanel();
		transaction_panel.add(panel_7);

		JLabel lblUsername_2 = new JLabel("Username:");
		panel_7.add(lblUsername_2);

		transactionUserNameField = new JTextField();
		panel_7.add(transactionUserNameField);
		transactionUserNameField.setColumns(10);

		JPanel panel_8 = new JPanel();
		transaction_panel.add(panel_8);

		JLabel lblAmount = new JLabel("Amount:");
		panel_8.add(lblAmount);

		transactionAmountField = new JTextField();
		panel_8.add(transactionAmountField);
		transactionAmountField.setColumns(10);

		JButton btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int amount = Integer.parseInt(transactionAmountField.getText());
				String userName = transactionUserNameField.getText();

				userControl.makeTransaction(userName, amount);
			}
		});
		transaction_panel.add(btnSend);
	}

	public void updateNeighbours(Collection<String> neighbours) {
		neighboursList.setListData(neighbours.toArray());
	}

	public void updateBalance(int balance) {
		balanceField.setText(balance + "");
	}

}
