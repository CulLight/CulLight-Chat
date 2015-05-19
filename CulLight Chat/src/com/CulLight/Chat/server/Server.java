package com.CulLight.Chat.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

//code to send and receive data
public class Server implements Runnable{
	
	private List<ServerClient> clients = new ArrayList<ServerClient>();
	// clientResponse contains ID of client
	private List<Integer> clientResponse  = new ArrayList<Integer>();
	private DatagramSocket socket;
	private int port;
	private boolean running = false;
	private boolean raw, rawNoPing = false;
	private Thread run, manage, send, receive;
	
	private final int MAX_ATTEMPS = 5;
	
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
		//console read in
		Scanner scanner = new Scanner(System.in);
		while (running) {
			//nextLine() will block till it is executed
			String text = scanner.nextLine(); //enter will terminate line
			if (!text.startsWith("/")) {
				printHelp();
				continue; // start while loop again and do not continue following code
			}
			// text begins with / it is a command
			text = text.substring(1); //get rid of slash (/)
			if (text.equals("raw")) {
				raw = !raw;
				if (raw) System.out.println("Raw mode activated!");
				else System.out.println("Raw mode disabled!");
			} else if (text.equals("rawp")) {
				rawNoPing = !rawNoPing;
				if (raw) System.out.println("Raw (no ping) mode activated!");
				else System.out.println("Raw (no ping) mode disabled!");
			} else if (text.equals("clients")) {
				System.out.println("Clients:");
				System.out.println("=========");
				for (int i=0; i < clients.size(); i++) {
					ServerClient c = clients.get(i);
					System.out.println(c.name + " (" + c.getID() + "): " + c.address.toString() + ":" + c.getPort());
				}
				System.out.println("=========");
			} else if (text.startsWith("m")) {
				// get rid of inital m
				text = text.substring(1);
				sendToAll("/m/black/Server:" + text);
			} else if (text.startsWith("kick")) {
				// /kick Lucas	
				String name = text.split(" ")[1];
				boolean num = false;
				int id = -1;
				try { //check if name is number (id) or name (username)
					id = Integer.parseInt(name);
					num = true;
				} catch (NumberFormatException e) {
					num = false;
				}
				if (num) { // name is an ID
					ServerClient kickedClient = null;
					for (int i = 0; i < clients.size(); i++) {
						if (clients.get(i).getID() == id) {
							kickedClient = clients.get(i);
							break;
						}
					}
					if (kickedClient != null) {
						send("/m/black/You got kicked!", kickedClient.address, kickedClient.getPort()); 
						disconnect(kickedClient.getID(), true);
					}
					else System.out.println("Client " + id + " does not exist! Check ID!");
				} else { // name is a actual name not an id
					ServerClient kickedClient = null;
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (name.equals(c.name)) {
							kickedClient = c;
							break;
						}
					}
					if (kickedClient != null) {
						send("/m/black/You got kicked!", kickedClient.address, kickedClient.getPort()); 
						disconnect(kickedClient.getID(), true);
					}
					else System.out.println("Client " + name + " does not exist! Check name!");
				}
				System.out.println("# Clients: " + clients.size());
			} else if (text.equals("help")) {
				printHelp();
			} else if (text.equals("quit")) {
				quit();
			} else {
				System.out.println("Unknown command.");
				printHelp();
			}
		}
		scanner.close();
	}
	
	private void printHelp() {
		System.out.println();
		System.out.println("List of all available commands:");
		System.out.println("==============================");
		System.out.println("/raw - enables raw mode.");
		System.out.println("/rawp - enables raw mode (no ping message).");
		System.out.println("/clients - show all connected clients.");
		System.out.println("/m [message] - send message to all clients.");
		System.out.println("/kick [username or ID] - kicks a user.");
		System.out.println("/help - print help.");
		System.out.println("/quit - shuts down server.");
		System.out.println();
	}
	
	private void quit() {
		for (int i = 0; i < clients.size(); i++) {
			disconnect(clients.get(i).getID(), true);
		}
		running = false;
		socket.close();
	}
	
	private void manageClients() {
		//make sure clients are still there and disconnect them if not so
		manage = new Thread("Manage") {
			public void run() {
				while (running) {
					//Managing: Send ping 
					sendToAll("/p/server");
					sendStatus();
					//the following should not run as fast as possible.
					//also dont want to use up all memory
					//Thread.sleep is awful for timing, ok here
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//one dont want to make following for loop
					//cos ocassionally one can run in thread concurrucie issues
					//for (ServerClients c : clients)
					for (int i = 0; i < clients.size(); i++) {
						ServerClient c = clients.get(i);
						if (!clientResponse.contains(c.getID())) {
							// clients has not yet responded
							if (c.attempt >=  MAX_ATTEMPS) {
								disconnect(c.getID(), false);
							} else {
								c.attempt++;
							}
						} else {
							// why new Integer
							//without it will use remove(int index) so it will remove
							//the client with index c.getID() instead of object in list
							//that has id == c.getID()
							clientResponse.remove(new Integer(c.getID()));
							c.attempt = 0;
						}

					}
				}
			}
		};
		manage.start();
	}
	

	private void sendStatus() {
		if (clients.size() <= 0) return;
		String users = "/u/";
		for (int i = 0; i < clients.size() - 1; i++) {
			// /u/Yan/n/Lucas/n/holger
			users += clients.get(i).name + "/n/";
		}
		users += clients.get(clients.size() - 1).name + "/e/";
		sendToAll(users);
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
						//wait here till we receive sth
						socket.receive(packet);
					} catch (SocketException e){ //catch socket exception when closing
					} catch (IOException e) {
						e.printStackTrace();
					}
					process(packet);
				}
			}
		};
		receive.start();		
	}
	
	
	// connection prefix: /c/
	private void process(DatagramPacket packet) {
		String string  = new String(packet.getData());
		if (raw) System.out.println(string);
		if (rawNoPing) {
			if (!string.startsWith("/p/")) System.out.println(string);
		}
		if (string.startsWith("/c/")) {
			//Guaranteed universal unique number
			//UUID id = UUID.randomUUID();
			int id = UniqueIdentifier.getIdentifier();	
			String color = UniqueColor.getUniqueColor();
			String name = string.split("/c/|/e/")[1];
			String text = name + "(" + id + ")"  + " @ " +  packet.getAddress().toString() + ":" + packet.getPort() + " connected.";
			System.out.println(text);
			//System.out.println(name + "(" + id + ") connected!");			
			clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id, color));
			String ID = "/c/" + id;
			//send message such that client knows he is connected
			send(ID, packet.getAddress(), packet.getPort());
		} else if (string.startsWith("/m/")) {
			// check if addressee is client or got kicked
			boolean isClient = false;
			//get color code of client that send message
			String color = "";
			for (int i = 0; i < clients.size(); i++) {
				ServerClient c = clients.get(i);
				if ( packet.getPort() == c.getPort() && packet.getAddress().getHostAddress().equals(c.getAddress().getHostAddress())) {
					isClient = true;
					color = c.getColor();
					break;
				}
			}
			if (isClient) {
		    	String message = string.substring(0, 3) + color +  "/" + string.substring(3,string.length());
				sendToAll(message);
			}
			else {
				send("/m/black/Stop spaming!/e/", packet.getAddress(), packet.getPort());	
				String text = string.substring(3);
				text = text.split("/e/")[0];
				System.out.println(text);
			}
		} else if (string.startsWith("/d/")) {
			String id = string.split("/d/|/e/")[1];
			disconnect(Integer.parseInt(id), true);
		} else if (string.startsWith("/p/")) {
			clientResponse.add(Integer.parseInt(string.split("/p/|/e/")[1]));
		} else {
			System.out.println(string);
		}
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
		if (message.startsWith("/m/")) {
			String colorText = message.substring(3).split("/e/")[0];
	    	String text = colorText.split("/")[1];
			System.out.println(text);
		}
		for (int i = 0; i < clients.size(); i++) {
			ServerClient client = clients.get(i);
			send(message, client.address, client.port);
		}
	}
	
	
	
	private void disconnect(int id, boolean status) {
		//status: if they are closing, or if they lost internet connect, or their PC shut down 
		//true: regular disconnect
		//false: time out
		ServerClient c = null;
		for (int i = 0; i < clients.size(); i++) {
			if (clients.get(i).getID() == id) {
				c = clients.get(i);
				clients.remove(i);
				break;
			}
		}
		//if client got kicked and try to disconnect, he does not exist any more
		if (c == null) return;
		String message = "";
		if (status) {
			message = "Client " + c.name + " (" + c.getID() + ") @ " + c.address.toString() + ":" + c.port + " disconnected.";
		} else {
			message = "Client " + c.name + " (" + c.getID() + " )" + c.address.toString() + ":" + c.port + " timed out.";
		}
		System.out.println(message);
	}
}
