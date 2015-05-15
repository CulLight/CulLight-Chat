package com.CulLight.Chat;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class Login extends JFrame {
	private static final long serialVersionUID = 1L;
	
	//global variables
	private JPanel contentPane;
	private JTextField txtName;
	private JTextField txtAddress;
	private JTextField txtPort;
	private JLabel lblAddress;
	private JLabel lblPort;
	private JLabel lblAddressDesc;
	private JLabel lblPortDesc;

	public Login() {
		//get native windows look of GUI, somehow doesnt work
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

			
		setResizable(false);
		setTitle("Login");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(300, 380);
		//start window in center
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		//absolute Layout, because not resizable GUI
		contentPane.setLayout(null);
		
		txtName = new JTextField();
		txtName.setBounds(64, 50, 165, 28);
		contentPane.add(txtName);
		txtName.setColumns(10);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(129, 34, 46, 16);
		contentPane.add(lblName);
		
		txtAddress = new JTextField();
		txtAddress.setBounds(64, 117, 165, 28);
		contentPane.add(txtAddress);
		txtAddress.setColumns(10);
		
		lblAddress = new JLabel("IP Address:");
		lblAddress.setBounds(119, 100, 71, 16);
		contentPane.add(lblAddress);
		
		lblPort = new JLabel("Port:");
		lblPort.setBounds(135, 181, 40, 16);
		contentPane.add(lblPort);
		
		txtPort = new JTextField();
		txtPort.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String name = txtName.getText();
					String address = txtAddress.getText();
					int port = Integer.parseInt(txtPort.getText());
					login(name, address, port);
				}
			}
		});
		txtPort.setColumns(10);
		txtPort.setBounds(64, 198, 165, 28);
		contentPane.add(txtPort);
		
		lblAddressDesc = new JLabel("(eg. 192.169.0.2)");
		lblAddressDesc.setBounds(103, 146, 99, 16);
		contentPane.add(lblAddressDesc);
		
		lblPortDesc = new JLabel("(eg. 8192)");
		lblPortDesc.setBounds(121, 230, 69, 16);
		contentPane.add(lblPortDesc);
		
		JButton btnLogin = new JButton("Login");
		//anonymous inner type (addActionListener wants a class of type ActionListner that has function actionPerformed
		//instead of having a new class, one can do this anonymous, with its actionPerfomed function here
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = txtName.getText();
				String address = txtAddress.getText();
				int port = Integer.parseInt(txtPort.getText());
				login(name, address, port);
			}

		});
		btnLogin.setBounds(102, 294, 89, 23);
		contentPane.add(btnLogin);
	}

	
	
	private void login(String name, String address, int port) {
		// close the login window
		dispose();
		new ClientWindow(name, address, port);
	}
	
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login frame = new Login();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
