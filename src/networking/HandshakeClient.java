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
import utils.*;

public class HandshakeClient extends Thread {

	private static final String USAGE = "Client <address> <port>";
	private static BufferedReader in = null;
	private static BufferedWriter out = null;
	/** Starts a Client application. */
	public static void main(String[] args) {
//		Scanner inn = new Scanner(System.in);
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		// ------------------------- Connect to Server -------------------------
		InetAddress addr = null;
		Integer port = 0;
		Socket sock = null;

		while ( (in == null) || (out == null) ) {
			addr = promptIPAddr(r); 	//  the IP-adress
			port = promptPort(r);		// the port
			sock = tryConnection(addr, port);   // try connection
			if (sock != null) {
				initInOut(sock);
			}
		}
		
		System.out.println("Your connection is established!");

		// ------------------------- Prompt User Info -------------------------
		String name = promptName(r);
		sendToServer(Commands.HELLO, name);
		parseFromServer();

		Integer numOfOppo = promptNumOppo(r);
		sendToServer(Commands.PLAY, "" + (numOfOppo + 1));
		
		while (true) {
			String numWaiting = parseFromServer();
			if (numWaiting.equals("0")){
				System.out.println("Player " + name + ", Game is goint to start, be prepared...");
				Thread t =  new PlayerClient(sock, name, numOfOppo+1, r);
				t.start();
				break;
			}
		}
	}

	public static InetAddress promptIPAddr(BufferedReader br) {
		String s = null;
		
		while (true) {
			try {
				System.out.println("Please enter the ip address of server: ");
				s = br.readLine();
				InetAddress res = InetAddress.getByName(s);
				return res;
			} catch (UnknownHostException e) {
				System.out.println("ERROR: host " + s + " unknown, please try again.");
			} catch (IOException e) {
                System.out.println("IOException happens in chooseTileIdx br.readLine.");
            }
		}

	}

	public static Integer promptPort(BufferedReader br) {
		String s = null;
		Integer p = null;
		
		while (true) {
			try {
				System.out.println("Please enter the port on the server: ");
				s = br.readLine();
				p = Integer.parseInt(s);
				if (p != 1024) {
					System.out.println("Hint: the correct port for your group is 1024. Try again.");
				} else {
					return p;
				}
			} catch (NumberFormatException e) {
				System.out.println("Hint: the correct port for your group is 1024. Try again.");
			} catch (IOException e) {
                System.out.println("IOException happens in chooseTileIdx br.readLine.");
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
	
	public static String promptName(BufferedReader br) {
		String s;
		
		while (true) {
			System.out.print("> Please enter your user name: ");
			try {
                s = br.readLine();
                if (Pattern.matches("\\w+", s))  { return s; }
                else { System.out.println("Name can only have letters, digitis, and underscore. Try again."); } 
            } catch (IOException e) {
                System.out.println("IOException happens in chooseTileIdx br.readLine.");
            }
			
				
		}
	}
	
	public static Integer promptNumOppo(BufferedReader br) {
		Integer numOfOppo = null;
		String num = null;
		
		while (true) {
			System.out.print("You want to play with how many opponents? (Yourself NOT included) \n> Enter 1, 2, or 3: ");

			try {
                num = br.readLine();
            } catch (IOException e) {
                System.out.println("IOException happens in chooseTileIdx br.readLine.");
            }
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
			case Commands.HELLO:
				msg = Commands.HELLO + " " + other + " false false false false";
				break;
			case Commands.PLAY:
				msg = Commands.PLAY + " " + other;
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
			case Commands.HELLO:
				System.out.println("Your name has been registered.");
				System.out.println("- Chat extension enabled: 		 " 	 + info[1]);
				System.out.println("- Challenge extension enabled:   "   + info[2]);
				System.out.println("- Leaderboard extension enabled: "   + info[3]);
				System.out.println("- Security extension enabled:    " 	 + info[4]);
				return "";
			case Commands.WAITING:
				System.out.println("Waiting for " + info[1] + " player(s)....");
				return info[1];
		}
        return "";
        
	}

}
