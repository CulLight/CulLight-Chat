package com.CulLight.Chat.server;

import java.net.InetAddress;

//store information about client that is connected to server
//class is public, read and write permission
public class ServerClient {
	
	public String name;
	public InetAddress address;
	public int port;
	//need in addition to IP address a ID, if two people from same house connect
	private final int ID;
	//if client times out, we need to contact him "attempt" times 
	//before be sure he is disconnected and then kick him out.
	public int attempt = 0;
	
	public ServerClient(String name, InetAddress address, int port, final int ID) {
		this.name = name;
		this.address = address;
		this.port = port;
		this.ID = ID;	
	}
	
	//class is public, so one has read and write permission, but ID should not be modified.
	//However ID is final, so not really necessary.
	public int getID() {
		return ID;
	}

}
