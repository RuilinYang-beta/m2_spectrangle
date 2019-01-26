package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Server extends Thread{

	private static final String USAGE = "usage: " + Server.class.getName() + " <ip> ";
	private List<Socket> clients;
	
	/** Starts a Server-application. */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println(USAGE);
			System.exit(0);
		}
		String name = args[0];
		int port = 0;
		Socket sock = null;
		ServerSocket sersock = null;

		// parse args[1] - the port
		try {
			port = Integer.parseInt(args[1]);
			if(port != 1024) {
				System.out.print("The port should be 1024. Introduce it here: ");
				do {
					Scanner in = new Scanner(System.in);
					port = in.nextInt();
					in.close();
				}while(port != 1024);
			}
		} catch (NumberFormatException e) {
			System.out.println(USAGE);
			System.out.println("ERROR: port " + args[2] + " is not an integer");
			System.exit(0);
		}

		// try to open a Socket server
		try {
			System.out.println("Server starting. \n  What is your name?");
			while (true) {
				sersock = new ServerSocket(port);
				sock = sersock.accept();
				System.out.println("Client connected");
				Scanner in = new Scanner(System.in);
				String clientname = in.nextLine();
				ClientHandler handler = new ClientHandler(clientname, sock);
				(new Thread(handler)).start();
				handler.handleTerminalInput();
				handler.shutDown();
			}
		} catch (IOException e) {
			System.out.println("ERROR: could not create a server socket on port " + port);
		}
	}

}
