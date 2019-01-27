package networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class PlayerClient extends Thread {

	private static final String USAGE = "Client <address> <port>";
	protected static BufferedReader in;
	protected static BufferedWriter out;
	
	/** Starts a Client application. */
	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println(USAGE);
			System.exit(0);
		}

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
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			System.out.println("ERROR: could not create a socket on " + addr + " and port " + port + "\n");
		}

		Scanner input = new Scanner(System.in);
		if (sock != null && in != null && out != null) {
			try {
				PlayerClient client = new PlayerClient();
				client.start();
				while (true) {
					String s = input.nextLine();
					out.write(s + "\n");
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	@Override
	public void run() {
		while(true) {
			try {
				String s = in.readLine();
				if(s == null || s.equals("exit")) {
					System.exit(0);
					break;
				}
				System.out.println(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
