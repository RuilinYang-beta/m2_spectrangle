package networking;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class PlayerClient {

	private static final String USAGE = "usage: java week7.cmdline.Client <address> <port>";

	/** Starts a Client application. */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println(USAGE);
			System.exit(0);
		}

//		String name = args[0];
		InetAddress addr = null;
		int port = 0;
		Socket sock = null;
		//getting the name of the client
				
		
		// check args[0] - the IP-adress
		try {
			addr = InetAddress.getByName(args[0]);
		} catch (UnknownHostException e) {
			System.out.println(USAGE);
			System.out.println("ERROR: host " + args[0] + " unknown" + "\n");
			System.exit(0);
		}

		// parse args[1] - the port
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			System.out.println(USAGE);
			System.out.println("ERROR: port " + args[1] + " is not an integer" + "\n");
			System.exit(0);
		}

		// try to open a Socket to the server
		try {
			sock = new Socket(addr, port);
		} catch (IOException e) {
			System.out.println("ERROR: could not create a socket on " + addr + " and port " + port + "\n");
		}

		// create Peer object and start the two-way communication
		//int i = 0;
		Scanner in = new Scanner(System.in);
		String name;
		try {
			//while (i < 4) {
				name = in.nextLine();
				ClientHandler client = new ClientHandler(name,sock);
				Thread streamInputHandler = new Thread(client);
				streamInputHandler.start();
				client.handleTerminalInput();
			//	i++;
				client.shutDown();
		//	}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
