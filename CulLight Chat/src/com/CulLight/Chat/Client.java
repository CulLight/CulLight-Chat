package com.CulLight.Chat;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Client {
	// only send message will ever really communicate with server. "OpenConnection" does not

	//socket = power outlet --> need to connect to it, to connect to the network
	//two main protocolls: 1) TCP: guarantees delivery of packet and sequential package, can sent package to any (not connected) IP address
	//                     resends package that has not arrived --> bad for gaming because one can get delayed
	//	   				   2) UTP: needs to establish connection with address first before sending package
	// use UTP
	private DatagramSocket socket;
	private InetAddress ip;
	private String name, address;
	private int port;
	
	private Thread send;
	
	private int ID = -1;
	
	public Client(String name, String address, int port) {
		this.name = name;
		this.address = address;
		this.port = port;
	}
	
	public String getAddress() {
		return address;
	}
	
	public int getPort() {
		return port;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean openConnection(String address) {
		// need to connect address (ip) which is a string into a string (inetAddress) object
			try {
				// the following line wont work because server already occupies that port
				// socket = new DatagramSocket(port);
				// now we will bind to any port that is available
				// however this way server needs to know the port we are sending from
				// UDP protocoll: we dont have to connect to serverto begin with
				socket = new DatagramSocket();
			} catch (SocketException e) {
				e.printStackTrace();
				return false;
			}
			try {
				ip = InetAddress.getByName(address);
			} catch (UnknownHostException e) {
				e.printStackTrace();
				return false;
			}
			return true;
	}
	
	public void send(final byte[] data) {  //need final because run is anonymous inner class
		send = new Thread("Send") {
			// anaonymous class prevents us from making new class that implemtents Runnable
			public void run(){
				DatagramPacket packet = new DatagramPacket(data,data.length, ip, port);
				try {
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		};
		send.start();
	}
	
	public String receive() {
		//dont want to send package with size larger than kb
		byte[] data = new byte[1024];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		
		//put data into packet
		try {
			// receive data that is send to socket, which knows the port
			//socket will sit until it receives sth --> will freeze application
			// need for threads
			socket.receive(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String message = new String(packet.getData());
		return message;
	}

	public void close() {
		new Thread() {
			public void run(){
				//dont terminate when it is used
				synchronized (socket) {
					socket.close();
				}
			}
		}.start();
	}

	public void setID(int id) {
		this.ID = id;
	}
	
	public int getID() {
		return ID;
	}

}
