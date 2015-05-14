package com.CulLight.Chat.server;

//launch new instance of Server.class and allow it to run
// can run multiple instances of Server.class
public class ServerMain {
		
	private int port;
	private Server server;
	
	public ServerMain(int port) {
		this.port = port;
		server = new Server(port);
	}
	
	public static void main(String[] args) {
		int port;
		if (args.length != 1) {
			System.out.println("Usage: java -jar Chat.jar [port]");
			return;
		}
		port = Integer.parseInt(args[0]);
		new ServerMain(port);
	}
}
