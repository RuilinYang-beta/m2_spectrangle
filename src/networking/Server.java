package networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server implements Observer {

	private static final String USAGE = "usage: " + Server.class.getName() + " <ip> ";
	private static Thread[] clients;
	public static Map<Socket,String> twoplayers = new HashMap<>();;
	public static Map<Socket,String> threeplayers = new HashMap<>();
	public static Map<Socket,String> fourplayers = new HashMap<>();
	
	@Override
	public void update(Observable o, Object arg) {
//		System.out.println("smth");
//		if(arg.equals(2)) {
//			twoplayers.add((ClientHandler) o);
//			System.out.println("Waiting " + (2 - twoplayers.size() % 2));
//		}else {
//			if(arg.equals(3)) {
//				threeplayers.add((ClientHandler) o);
//				System.out.println("Waiting " + (3 - threeplayers.size() % 3));
//			}else {
//				fourplayers.add((ClientHandler) o);
//				System.out.println("Waiting " + (2 - fourplayers.size() % 4));
//			}
//		}
//	}
//		if(twoplayers.size() == 2) {
//			System.out.println("Waiting 0");
//		}else {
//			System.out.println("Waiting " + (2 - twoplayers.size() % 2));
//		}
//		
//		if(threeplayers.size() == 3) {
//			System.out.println("Waiting 0");
//		}else {
//			System.out.println("Waiting " + (3 - threeplayers.size() % 3));
//		}
//		
//		if(fourplayers.size() == 4) {
//			System.out.println("Waiting 0");
//		}else {
//			System.out.println("Waiting " + (2 - fourplayers.size() % 4));
		}

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
//				BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
//				BufferedWriter out = new BufferedWriter(new OutputStreamWriter());
//				String j = in.readLine();
//				out.write(j + "\n");
//				handler.handleTerminalInput();
//				handler.shutDown();
			} catch (IOException e) {
				System.out.println("ERROR: could not create a server socket on port " + port);
			}
		}
	}
	 
	
	}


