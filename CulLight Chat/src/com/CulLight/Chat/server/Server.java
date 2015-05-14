package com.CulLight.Chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

//code to send and receive data
public class Server implements Runnable{
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
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
			return;
		}
		run = new Thread(this,"Server");
		run.start();
	}

	// this methods only runs once and then it is gone
	public void run() {
		running = true;
		System.out.println("Server started on port " +  port);
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
					byte[] data = new byte[1024];
					DatagramPacket packet = new DatagramPacket(data, data.length);
					try {
						socket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
					clients.add(new ServerClient("Lucas", packet.getAddress(), packet.getPort(), 50));
					System.out.println(clients.get(0).address.toString() + ":" + clients.get(0).port);
				}
			}
		};
		receive.start();
		
	}
	
	// connection prefix: /c/
	private void process(DatagramPacket packet) {
		String string  = new String(packet.getData());
		if (string.startsWith("/c/")) {
			clients.add(new ServerClient(string.substring(3, string.length()), packet.getAddress(), packet.getPort(), 50) );
			System.out.println(string.substring(3, string.length()));
		} else {
			System.out.println(string);
		}
	}
}
