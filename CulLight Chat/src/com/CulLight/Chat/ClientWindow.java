package com.CulLight.Chat;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;

public class ClientWindow extends JFrame implements Runnable{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtMessage;
	private JTextArea history;
	private DefaultCaret caret;
	
	private Thread run, listen;
	private boolean running;
	
	private Client client;
	
	public ClientWindow(String name, String address, int port) {
		client = new Client(name, address, port);
		Boolean connect = client.openConnection(address);
		if (!connect) {
			System.err.println("Connection failed");
			console("Connection failed");
		}
		//unconventional to call function from constructor, but 
		// createWindow is private, so one cant overwrite it.
		createWindow();
		console("Attempting a connection to " + address + ":" + port + ", user:" + name);
		String connection = "/c/" + name + "/e/";
		client.send(connection.getBytes());
		running = true;
		run = new Thread(this, "Running");
		run.start();
	}
	
	
	private void createWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(880,550);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setTitle("CulLight Chat Client");
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		//sum of widths of each grid has to be equal to JPanel size
		gbl_contentPane.columnWidths = new int[]{28, 815, 30, 7}; //Sum 880
		gbl_contentPane.rowHeights = new int[]{35,475, 40}; //Sum 550
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0};
		gbl_contentPane.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		//set layout of the frame to the set layout above
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextArea();
		history.setEditable(false);
		// Put JTextArea history into ScrollPane
		JScrollPane scroll = new JScrollPane(history);
		//Scroll bar should also go to last entered line
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		// which grid it is in
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		// grid cells it takes up
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.insets = new Insets(20, 0, 0, 0);
		contentPane.add(scroll, scrollConstraints);
		
		txtMessage = new JTextField();
		txtMessage.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					send(txtMessage.getText(), true);
				}
			}
		});
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 0;
		gbc_txtMessage.gridy = 2;
		gbc_txtMessage.gridwidth = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				send(txtMessage.getText(), true);
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				String disconnect = "/d/" + client.getID() + "/e/";
				send(disconnect, false);
				running = false;
				client.close();
			}
		});
		
		setVisible(true);
		
		// User should be able to type in message right away
		txtMessage.requestFocusInWindow();
		//
	}
	
	public void run() {
		listen();
		
	}
	
	// need thread to not wait for receive forever
	public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (running) {
					String message = client.receive();
					if (message.startsWith("/c/")) {
						//message should look like "/c/8297/e/"
						//client.setID(Integer.parseInt(message.substring(3, message.length()-1)));
						String id = message.split("/c/|/e/")[1];
						client.setID(Integer.parseInt(id));
						console("Successfully connected to server! ID: " + client.getID());
					} else if (message.startsWith("/m/")) {
						String text = message.split("/m/|/e/")[1];
						console(text);
					}
				}
			}
		};
		listen.start();
	}	
	
	private void send(String message, boolean text){
		if (message.equals("")) return;
		if (text) {
			message = client.getName() + ": " + message;
			//prefix /m/ to indicate it is a message
			message = "/m/" + message + "/e/";
		}
		client.send(message.getBytes());
		txtMessage.setText("");
	}
	
	public void console(String message) {
		history.append(message + "\n\r");
		history.setCaretPosition(history.getDocument().getLength());
	}

}
