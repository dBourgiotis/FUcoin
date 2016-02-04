package main;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import akka.actor.ActorSystem;
import user.UserControl;

public class MainFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;

	/**
	 * Create the frame.
	 */
	public MainFrame(ActorSystem system) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		JPanel panel_1 = new JPanel();
		panel.add(panel_1);

		JLabel lblUsername = new JLabel("Username:");
		panel_1.add(lblUsername);

		textField = new JTextField();
		panel_1.add(textField);
		textField.setColumns(10);

		JPanel panel_2 = new JPanel();
		panel.add(panel_2);

		JLabel lblBalance = new JLabel("Balance:");
		panel_2.add(lblBalance);

		JSpinner spinner = new JSpinner();
		panel_2.add(spinner);

		JButton btnCreate = new JButton("Create");
		btnCreate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String userName = textField.getText();
				try {
					spinner.commitEdit();
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int initBalance = (Integer) spinner.getValue();
				new UserControl(system, userName, initBalance);
			}
		});
		panel.add(btnCreate);
	}

}
