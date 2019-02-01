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
//		if (args.length != 2) {
//			System.out.println(USAGE);
//			System.exit(0);
//		}

		InetAddress addr = null;
		int port = 0;
		Socket sock = null;
		String ip = "";
				
		Scanner inn = new Scanner(System.in);
		//  the IP-adress
		try {
			System.out.println("Please introduce the ip address: ");
			ip = inn.nextLine();
			addr = InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			System.out.println(USAGE);
			System.out.println("ERROR: host " + ip + " unknown" + "\n");
			System.exit(0);
		}
		
		// the port
		while (port != 1024) {
			try {
				System.out.println("Please introduce the port: ");
				ip = inn.nextLine();
				port = Integer.parseInt(ip);
			} catch (NumberFormatException e) {
				System.out.println(USAGE);
				System.out.println("ERROR: port " + ip + " is not an integer" + "\n");
				System.exit(0);
			}
		}
	//	inn.close();
		
		// try to open a Socket to the server
		try {
			sock = new Socket(addr, port);
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			System.out.println("ERROR: could not create a socket on " + addr + " and port " + port + "\n");
		}

	//	Scanner input = new Scanner(System.in);
		if (sock != null && in != null && out != null) {
			try {
				PlayerClient client = new PlayerClient();
				client.start();
				while (true) {
					String s = inn.nextLine();
					out.write(s + "\n");
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}catch(NoSuchElementException e) {
				e.printStackTrace();
			}
		}
		inn.close();
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
