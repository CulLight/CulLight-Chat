package com.CulLight.Chat.server;

//code to send and receive data
public class Server {
	
	
	private int port;
	
	//constructor needs no address, because we are the address
	// dont need a name because we are a server not a client
	//but we can have any port, so we need a port
	public Server(int port) {
		this.port = port;
	}

}
