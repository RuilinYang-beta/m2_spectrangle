package networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;
import java.util.Scanner;
import spectranglegame.*;

public class HandshakeClient extends Thread {

	private static final String USAGE = "Client <address> <port>";
	private static final String HELLO = "Hello";
	private static final String PLAY  = "Play";
	private static final String WAITING = "Waiting";
	private static BufferedReader in = null;
	private static BufferedWriter out = null;
	/** Starts a Client application. */
	public static void main(String[] args) {
		Scanner inn = new Scanner(System.in);
		// ------------------------- Connect to Server -------------------------
		InetAddress addr = null;
		Integer port = 0;
		Socket sock = null;

		while ( (in == null) || (out == null) ) {
			addr = promptIPAddr(inn); 	//  the IP-adress
			port = promptPort(inn);		// the port
			sock = tryConnection(addr, port);   // try connection
			if (sock != null) {
				initInOut(sock);
			}
		}
		
		System.out.println("Your connection is established!");

		// ------------------------- Prompt User Info -------------------------
		String name = promptName(inn);
		sendToServer(HELLO, name);
		parseFromServer();

		Integer numOfOppo = promptNumOppo(inn);
		sendToServer(PLAY, "" + (numOfOppo + 1));
//		parseFromServer();
		
		while (true) {
			String numWaiting = parseFromServer();
			if (numWaiting.equals("0")){
				System.out.println("Player " + name + ", Game is goint to start, be prepared...");
				Thread t =  new PlayerTUI(sock, name, numOfOppo+1);
				t.start();
				break;
			}
		}
		
		inn.close();
	}

	public static InetAddress promptIPAddr(Scanner in) {
		String s = null;
		
		while (true) {
			try {
				System.out.println("Please enter the ip address of server: ");
				s = in.nextLine();
				return InetAddress.getByName(s);
			} catch (UnknownHostException e) {
				System.out.println("ERROR: host " + s + " unknown, please try again.");
			}
		}

	}

	public static Integer promptPort(Scanner in) {
		String s = null;
		Integer p = null;
		
		while (true) {
			try {
				System.out.println("Please enter the port on the server: ");
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

	public static Socket tryConnection(InetAddress addr, Integer port) {
		Socket sock = null;
		
		while (true) {
			try {
				sock = new Socket(addr, port);
				return sock;
			} catch (IOException e) {
				System.out.println("ERROR: could not create a socket on " + addr + " and port " + port + "\n. Try again.");
				return null;
			}
		}
	}
	
	public static void initInOut(Socket sock) {
		try {
			out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		} catch (IOException e) {
			System.out.println("IOException in sock.getOutputStream / sock.getInputStream.");
		}
	}
	
	public static String promptName(Scanner in) {
		String s;
		
		while (true) {
			System.out.print("> Please enter your user name: ");
			s = in.nextLine();
			
			if (Pattern.matches("\\w+", s))  { return s; }
			else { System.out.println("Name can only have letters, digitis, and underscore. Try again."); } 	
		}
	}
	
	public static Integer promptNumOppo(Scanner in) {
		Integer numOfOppo = null;
		
		while (true) {
			System.out.print("You want to play with how many opponents? (Yourself NOT included) \n> Enter 1, 2, or 3: ");
			String num = in.nextLine();
			
			try {
				numOfOppo = Integer.parseInt(num); 
				if (numOfOppo == 1 || numOfOppo == 2 || numOfOppo == 3) {
					return numOfOppo;
				} else {
					System.out.println("Illegal number, please enter 1, 2 or 3. Try again. "); 
				}
			} 
			catch (NumberFormatException e) { 
				System.out.println("Illegal format, please enter 1, 2 or 3. Try again. "); 
			}
			
		}
	}
	
	public static void sendToServer(String cmd, String other) {
		String msg = null;
		
		switch (cmd) {
			case HELLO:
				msg = HELLO + " " + other + " false false false false";
				break;
			case PLAY:
				msg = PLAY + " " + other;
				break;
			default:
				System.out.println("Unknow command.");
				break;
		}
		
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.out.println("ERROR: unable to communicate to server");
            e.printStackTrace();
		}
	}
	
	public static String parseFromServer() {
		String s = null;
		try {
			s = in.readLine();
		} catch (IOException e) {
			System.out.println("IOException in parsing info from server.");
		}
		
        String[] info = null;
        if (s != null) {
        	info = s.split("\\s");
        } else {
        	System.out.println("Server sends null");
        }
        
        switch (info[0]) {
			case HELLO:
				System.out.println("Your name has been registered.");
				System.out.println("- Chat extension enabled: 		 " 	 + info[1]);
				System.out.println("- Challenge extension enabled:   "   + info[2]);
				System.out.println("- Leaderboard extension enabled: "   + info[3]);
				System.out.println("- Security extension enabled:    " 	 + info[4]);
				return "";
			case WAITING:
				System.out.println("Waiting for " + info[1] + " player(s)....");
				return info[1];
		}
        return "";
        
	}

}
