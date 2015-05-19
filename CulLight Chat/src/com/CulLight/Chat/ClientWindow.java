package com.CulLight.Chat;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ClientWindow extends JFrame implements Runnable{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField txtMessage;
	public JTextPane history;
	StyledDocument doc;
	Style style;
	private DefaultCaret caret;
	
	private Thread run, listen;
	private boolean running;
	
	private Client client;
	private JMenuBar menuBar;
	private JMenu mnFile;
	private JMenuItem mntmOnlineUsers;
	private JMenuItem mntmExit;
	
	boolean bColor = false;
	
	private OnlineUsers users;
	
	public ClientWindow(String name, String address, int port) {
		client = new Client(name, address, port);
		Boolean connect = client.openConnection(address);
		if (!connect) {
			System.err.println("Connection failed");
			console("Connection failed", Color.BLACK);
		}
		//unconventional to call function from constructor, but 
		// createWindow is private, so one cant overwrite it.
		createWindow();
		console("Attempting a connection to " + address + ":" + port + ", user:" + name, Color.BLACK);
		String connection = "/c/" + name + "/e/";
		client.send(connection.getBytes());
		users = new OnlineUsers();
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
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmOnlineUsers = new JMenuItem("Online Users");
		mntmOnlineUsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				users.setVisible(true);
			}
		});
		mnFile.add(mntmOnlineUsers);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String disconnect = "/d/" + client.getID() + "/e/";
				send(disconnect, false);
				running = false;
				client.close();
			}
		});
		mnFile.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setTitle("CulLight Chat Client");
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		//sum of widths of each grid has to be equal to JPanel size
		gbl_contentPane.columnWidths = new int[]{28, 815, 30, 7}; //Sum 880
		gbl_contentPane.rowHeights = new int[]{25, 485, 40}; //Sum 550
		//set layout of the frame to the set layout above
		contentPane.setLayout(gbl_contentPane);
		
		history = new JTextPane();
        doc = history.getStyledDocument();
        style = history.addStyle("Chat Styole", null);
		// Put JTextArea history into ScrollPane
		JScrollPane scroll = new JScrollPane(history);
		//Scroll bar should also go to last entered line
		caret = (DefaultCaret) history.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		GridBagConstraints scrollConstraints = new GridBagConstraints();
		scrollConstraints.insets = new Insets(0, 0, 5, 5);
		scrollConstraints.fill = GridBagConstraints.BOTH;
		scrollConstraints.gridx = 0;
		scrollConstraints.gridy = 0;
		scrollConstraints.gridwidth = 3;
		scrollConstraints.gridheight = 2;
		scrollConstraints.weightx = 1;
		scrollConstraints.weighty = 1;
		scrollConstraints.insets = new Insets(0, 5, 0, 0);
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
		gbc_txtMessage.weightx = 1;
		gbc_txtMessage.weighty = 0;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		btnSend.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				send(txtMessage.getText(), true);
			}
		});
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		gbc_btnSend.weightx = 0;
		gbc_btnSend.weighty = 0;
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
						console("Successfully connected to server! ID: " + client.getID(), Color.BLACK);
					} else if (message.startsWith("/m/")) {
						// message = /m/blue/ Huhu ich bins/e/
						// info = blue/ Huhu ich bins
				    	String info = message.substring(3).split("/e/")[0];
				    	// colorStr = blue
				    	String colorStr = info.split("/")[0];
				    	Color color = getColor(colorStr);
				    	String text = info.split("/")[1];
						console(text, color);
						//ping to check if client is still there
					} else if (message.startsWith("/p/")) {
						String reply = "/p/" + client.getID() + "/e/";
						send(reply, false);
						// users list of all users
					} else if (message.startsWith("/u/")) {
						String[] u = message.split("/u/|/n/|/e/");
						// message = /u/Yan/n/Lucas/n/holger/e/
						// after split this is:
						//           "","Yan","Lucas","holger",""
						// the part before /u/ and after /e/ is too many
						// trim array
						users.update(Arrays.copyOfRange(u, 1, u.length - 1));						
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
			txtMessage.setText("");
		}
		client.send(message.getBytes());
	}
	
	public void console(String message, Color color) {

		StyleConstants.setForeground(style, color);
	    try {
			doc.insertString(doc.getLength(), message + "\n\r", style);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		history.setCaretPosition(history.getDocument().getLength());
	}
	
	private Color getColor(String colorStr) {
		Color color = null;
		if (colorStr.equals("blue")) color = Color.BLUE;
		else if (colorStr.equals("black")) color = Color.BLACK;
		else if (colorStr.equals("cyan")) color = Color.CYAN;
		else if (colorStr.equals("gray")) color = Color.GRAY;
		else if (colorStr.equals("green")) color = Color.GREEN;
		else if (colorStr.equals("magenta")) color = Color.MAGENTA;
		else if (colorStr.equals("orange")) color = Color.ORANGE;
		else if (colorStr.equals("red")) color = Color.RED;
		else if (colorStr.equals("pink")) color = Color.PINK;
		else if (colorStr.equals("yellow")) color = Color.YELLOW;

		return color;
	}

}
