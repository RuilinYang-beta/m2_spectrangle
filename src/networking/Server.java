package networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import spectranglegame.*;

public class Server {

	private static final String USAGE = "usage:  <port> ";
	public static Map<Socket,String> twoplayers = new HashMap<>();;
	public static Map<Socket,String> threeplayers = new HashMap<>();
	public static Map<Socket,String> fourplayers = new HashMap<>();
	

	/** Starts a Server-application. */
	public static void main(String[] args) {
		Scanner inn = new Scanner(System.in);
		
		Integer port = promptPort(inn);
		ServerSocket sersock = null;
		Socket sock = null;
		Integer i = 1;
		
		System.out.println("Server is listening to port 1024. Waiting for connection...");
		
		try {
			sersock = new ServerSocket(port);
			while (true) {
				sock = sersock.accept();
				System.out.println("In total, " + i + " client(s) has connected");
				Thread handshakeS = new HandshakeServer(sock);
				handshakeS.start();
				i++;
			}
		} catch(IOException e) {
			System.out.println("IOException in opening serversocket.");
		}
	}	
	
	public static Integer promptPort(Scanner in) {
		String s = null;
		Integer p = null;
		
		while (true) {
			try {
				System.out.println("Please input which port to listen to: ");
				s = in.nextLine();
				p = Integer.parseInt(s);
				if (p != 1024) {
					System.out.println("Hint: the correct port for your group is 1024. Try again.");
				} else {
					return p;
				}
			} catch (NumberFormatException e) {
				System.out.println("Hint: the correct port for your group is 1024. Try again.");
			}
		}
	}
	
}


