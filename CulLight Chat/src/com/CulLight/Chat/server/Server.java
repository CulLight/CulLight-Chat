package com.CulLight.Chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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
				}
			}
		};
		receive.start();		
	}
	
	private void send(String message, InetAddress address, int port) {
		//signal end of message
		message += "/e/";
		send(message.getBytes(), address, port);
	}
	
	//In contrast to send message of client which automatically sends to server
	//it is connected to, the send method of the server needs an address, where to send the message to.
	// final parameter because we send them over to an inner class
	private void send(final byte[] data, final InetAddress address, final int port) {
		send = new Thread("Send") {
			public void run() {
				DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
				try {
					//socket is like postoffice, and packet like a letter. letter has address
					//where to go to on it
					socket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();		
				}
			}
		};
		send.run();
	}
	
	private void sendToAll(String message) {
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message, client.address, client.port);
		}
	}
	
	// connection prefix: /c/
	private void process(DatagramPacket packet) {
		String string  = new String(packet.getData());
		if (string.startsWith("/c/")) {
			//Guaranteed universal unique number
			//UUID id = UUID.randomUUID();
			int id = UniqueIdentifier.getIdentifier();	
			String name = string.split("/c/|/e/")[1];
			System.out.println(name + "(" + id + ") connected!");
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
			String ID = "/c/" + id;
			send(ID, packet.getAddress(), packet.getPort());
		} else if (string.startsWith("/m/")) {
			sendToAll(string);
		}else if (string.startsWith("/d/")) {
			String id = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id), true);
		} else {
			System.out.println(string);
		}
	}
	
	
	private void disconnect(int id, boolean status) {
		//status: if they are closing, or if they lost internet connect, or their PC shut down 
		ServerClient c = null;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == id) {
				c = clients.get(i);
				clients.remove(i);
				break;
			}
		}
		String message = "";
		if (status) {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " disconnected.";
		} else {
			message = "Client " + c.name + " (" + c.getID() + " )" + c.address.toString() + ":" + c.port + " timed out.";
		}
		System.out.println(message);
	}
}
