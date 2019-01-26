package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {

	private static final String USAGE = "usage: " + Server.class.getName() + " <ip> <port>";

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
			do {
				
			port = Integer.parseInt(args[1]);
			
			}while(port !=  1024);
		} catch (NumberFormatException e) {
			System.out.println(USAGE);
			System.out.println("ERROR: port " + args[2] + " is not an integer");
			System.exit(0);
		}

		// try to open a Socket server
		try {
			sersock = new ServerSocket(port);
			System.out.println("Server starting.");
			while (true) {
				sock = sersock.accept();
				System.out.println("Client connected");
				ClientHandler handler = new ClientHandler("one", sock);
				(new Thread(handler)).start();
				handler.handleTerminalInput();
				handler.shutDown();
			}
		} catch (IOException e) {
			System.out.println("ERROR: could not create a server socket on port " + port);
		}
	}

}
