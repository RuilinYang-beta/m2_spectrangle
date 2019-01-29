package networking;

import java.io.BufferedReader;
import java.util.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import spectranglegame.GameControl;

/**
 * Client handler for a client of the game
 */

public class ClientHandler implements Runnable {

	public static final String EXIT = "exit";
	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	protected Thread[] clients;
	protected GameControl gamecontrol;
	protected List<Socket> twoplayers;
	protected List<Socket> threeplayers;
	protected List<Socket> fourplayers;
	/*
	 * @requires (clients != null) && (sockArg != null);
	 */
	/**
	 * Constructor. creates a ClientHandler object based in the given parameters.
	 * 
	 * @param clients the thread array for every client that connects to the server
	 * @param sockArg Socket of the Client Handler-process
	 */
	public ClientHandler(Socket sockArg, Thread[] clients) throws IOException {
		sock = sockArg;
		this.clients = clients;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
		twoplayers = new ArrayList<Socket>();
		threeplayers = new ArrayList<Socket>();
		fourplayers = new ArrayList<Socket>();
	}

	/**
	 * Reads strings of the stream of the socket-connection and writes the
	 * characters to the default output.
	 */
	public void run() {
		boolean first = true;
		try {
			while (true) {
				String s = in.readLine();
				if (/* s == null || */ s.isEmpty() || s.equals("exit")) {
					System.out.println("Client " + getName() + " disconnectd");
					shutDown();
					break;
				}
				String[] a = s.split(" ");
				if (first) {
					if (a[0].equals("hello") || a[0].equals("Hello")) {
						name = a[1];
						System.out.println("Hello " + name + " false false false false");
						first = false;
					}
				} else {
					System.out.println("Client " + getName() + ": " + s);
					if (a[0].equals("Play") || a[0].equals("play")) {
						int nr = Integer.parseInt(a[1]);
						if (nr == 2) {
							twoplayers.add(this.sock);
							while(twoplayers.size() != 2) {
								System.out.println("Waiting " + (twoplayers.size() % 2));
							}
							//start the game
							System.out.println("Waiting 0");
							twoplayers = new ArrayList<Socket>();
						} else {
							if (nr == 3) {
								threeplayers.add(this.sock);
								while(threeplayers.size() != 3) {
									System.out.println("Waiting" + (threeplayers.size() % 3));
								}
								// play the game on a new thread
								System.out.println("Waiting 0");
								threeplayers = new ArrayList<Socket>();
							} else {
								if (nr == 4) {
									fourplayers.add(this.sock);
									while(fourplayers.size() != 4) {
										System.out.println("Waiting " + (fourplayers.size() % 4));
									}
									//play the game on a new thread
									System.out.println("Waiting 0");
									fourplayers = new ArrayList<Socket>();
								} else {
									System.out.println("The game can be player only in 2, 3 or 4 players!");
								}
							}
						}
					}

				}
			}
		} catch (IOException e) {
			// System.out.println("C" + "\n");
		}
	}

	/**
	 * Reads a string from the console and sends this string over the
	 * socket-connection to the Peer process. On Peer.EXIT the method ends
	 */
	public void handleTerminalInput() throws IOException {
		try {
			while (true) {
				Scanner inn = new Scanner(System.in);
				String s = inn.nextLine();
				if (s.equals(EXIT)) {
					out.write(EXIT + "\n");
					out.flush();
					shutDown();
				} else {
					out.write(getName() + ":" + s + "\n");
					out.flush();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Closes the connection, the sockets will be terminated
	 */
	public void shutDown() {
		try {
			in.close();
			sock.close();
			out.close();
		//	System.exit(0);
		} catch (IOException e) {

		}
	}

	/** returns name of the peer object */
	public String getName() {
		return name;
	}

	/** read a line from the default input */
	static public String readString(String tekst) {
		System.out.print(tekst);
		String antw = null;
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			antw = in.readLine();
		} catch (IOException e) {
		}

		return (antw == null) ? "" : antw;
	}
}
