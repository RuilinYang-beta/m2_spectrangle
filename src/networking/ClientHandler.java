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
import players.HumanPlayer;
/**
 * Client handler for a client of the game
 */

public class ClientHandler extends Observable implements Runnable{

	public static final String EXIT = "exit";
	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	protected Thread[] clients;
	protected GameControl gamecontrol;
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
//						this.addObserver(server);
						if (nr > 1 && nr < 5) {
//							setChanged();
//							notifyObservers(nr);
							if (nr == 2) {
								Server.twoplayers.put(sock,name);
								System.out.println("Waiting " + (2 - Server.twoplayers.size()));
							}
							while (Server.twoplayers.size() % 2 != 0) {
								;
							}
//							 start the game
						} else {
							if (nr == 3) {
								Server.threeplayers.put(sock,name);
								System.out.println("Waiting " + (3 - (Server.threeplayers.size())));
								while (Server.threeplayers.size() % 3 != 0) {
									;
								}
								// play the game on a new thread
							} else {
								if (nr == 4) {
									Server.fourplayers.put(sock,name);
									System.out.println("Waiting " + (4 - (Server.fourplayers.size())));
									while (Server.fourplayers.size() % 4 != 0) {
										;
									}
									// play the game on a new thread
								} else {
									System.out.println("The game can be player only in 2, 3 or 4 players! Introduce another number: ");
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
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
			// System.exit(0);
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
