package com.CulLight.Chat.server;

import java.net.DatagramSocket;
import java.net.SocketException;

//code to send and receive data
public class Server implements Runnable{
	
	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private Thread run, manage, send, receive;
	
	//constructor needs no address, because we are the address
	// dont need a name because we are a server not a client
	//but we can have any port, so we need a port
	public Server(int port) {
		this.port = port;
		try {
			//use UTP and there server is not really much different from the client
			socket = new DatagramSocket(port);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		run = new Thread(this,"Server");
	}

	// this methods only runs once and then it is gone
	public void run() {
		running = true;
		manageClients();
		receive();
		
	}
	
	private void manageClients() {
		//make sure clients are still there and disconnect them if not so
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					//Managing
				}
			}
		};
		manage.start();
	}

	private void receive() {
		//need keep track of recipient of packets
		receive = new Thread("Receive") {
			public void run() {
				while (running) {
					// Receiving
				}
			}
		};
		receive.start();
		
	}
}
