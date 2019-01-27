package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class Server{

	private static final String USAGE = "usage: " + Server.class.getName() + " <ip> ";
	private static Thread[] clients;
	
	/** Starts a Server-application. */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println(USAGE);
			System.exit(0);
		}
		String name = args[0];
		int port = 0;
		Socket sock = null;

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
			System.out.println("Server starting. \nWhat is your name?");
			clients = new Thread[10];
			int i = 0;
			while (true) {
				try(ServerSocket sersock = new ServerSocket(port)) {
				sock = sersock.accept();
				System.out.println("Client " + i +  " connected");
				ClientHandler handler = new ClientHandler(sock, clients);
				clients[i] = (new Thread(handler));
				clients[i].start();
				i++;
//				handler.handleTerminalInput();
//				handler.shutDown();
			} catch (IOException e) {
				System.out.println("ERROR: could not create a server socket on port " + port);
			}
		}
	}

}
