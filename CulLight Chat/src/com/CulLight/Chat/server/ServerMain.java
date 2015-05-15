package com.CulLight.Chat.server;

import java.net.InetAddress;
import java.net.UnknownHostException;

//launch new instance of Server.class and allow it to run
// can run multiple instances of Server.class
public class ServerMain {
		
//	private int port;
//	private Server server;
//	
//	public ServerMain(int port) {
//		this.port = port;
//		server = new Server(port);
//	}
	
	public static void main(String[] args) {
		int port;
		if (args.length != 1) {
			System.out.println("Usage: java -jar Chat.jar [port]");
			return;
		}
		port = Integer.parseInt(args[0]);
//		new ServerMain(port);
		new Server(port);
		
		InetAddress address = null;
		try {
			address = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String myIp = "Your forward to ip is " + address.getHostAddress();
		System.out.println(myIp); 
		System.out.println("TO DO for internet chat:");
		System.out.println("--- Look up the IP of your router (google, what is my IP)");
		System.out.println("--- Look up your PvP4 ip (cmd, ipconfig)");
		System.out.println("--- Forward your port to your ip (router, config)");
		System.out.println();
		System.out.println();


	}
}
