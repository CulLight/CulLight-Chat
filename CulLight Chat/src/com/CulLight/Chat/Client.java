package com.CulLight.Chat;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;

import javax.swing.JTextArea;

import java.awt.GridBagConstraints;
import javax.swing.JButton;
import java.awt.Insets;
import javax.swing.JTextField;

public class Client extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;

	private String name, address;
	private int port;
	private JTextField txtMessage;
	
	public Client(String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
		//unconventional to call function from constructor, but 
		// createWindow is private, so one cant overwrite it.
		createWindow();

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
		
		JTextArea txtrHistory = new JTextArea();
		txtrHistory.setEditable(false);
		GridBagConstraints gbc_txtrHistory = new GridBagConstraints();
		gbc_txtrHistory.insets = new Insets(0, 0, 5, 5);
		gbc_txtrHistory.fill = GridBagConstraints.BOTH;
		// which grid it is in
		gbc_txtrHistory.gridx = 1;
		gbc_txtrHistory.gridy = 1;
		// grid cells it takes up
		gbc_txtrHistory.gridwidth = 2;
		gbc_txtrHistory.insets = new Insets(20, 0, 0, 0);
		contentPane.add(txtrHistory, gbc_txtrHistory);
		
		txtMessage = new JTextField();
		GridBagConstraints gbc_txtMessage = new GridBagConstraints();
		gbc_txtMessage.insets = new Insets(0, 0, 0, 5);
		gbc_txtMessage.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtMessage.gridx = 1;
		gbc_txtMessage.gridy = 2;
		contentPane.add(txtMessage, gbc_txtMessage);
		txtMessage.setColumns(10);
		
		JButton btnSend = new JButton("Send");
		GridBagConstraints gbc_btnSend = new GridBagConstraints();
		gbc_btnSend.insets = new Insets(0, 0, 0, 5);
		gbc_btnSend.gridx = 2;
		gbc_btnSend.gridy = 2;
		contentPane.add(btnSend, gbc_btnSend);
		
		setVisible(true);
	}

}
