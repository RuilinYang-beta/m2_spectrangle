package networking;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * Client handler for a client of the game
 */

public class ClientHandler implements Runnable {

	public static final String EXIT = "exit";
	protected String name;
	protected Socket sock;
	protected BufferedReader in;
	protected BufferedWriter out;
	protected ClientHandler[] clients;

	/*
	 * @requires (nameArg != null) && (sockArg != null);
	 */
	/**
	 * Constructor. creates a ClientHandler object based in the given parameters.
	 * 
	 * @param nameArg name of the Client Handler-process
	 * @param sockArg Socket of the Client Handler-process
	 */
	public ClientHandler(String nameArg, Socket sockArg, ClientHandler[] clients) throws IOException {
		name = nameArg;
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
		String s;
		try {
			while (true) {
				s = in.readLine();
				if (s == null || s.isEmpty() || s.equals("exit")) {
					shutDown();
					break;
				} else {
					// out.print(s);
					System.out.println(s);
				}
			}
		} catch (IOException e) {
			// System.out.println("Stream is closed" + "\n");
		}
	}

	/**
	 * Reads a string from the console and sends this string over the
	 * socket-connection to the Peer process. On Peer.EXIT the method ends
	 */
	public void handleTerminalInput() throws IOException {
		String s;
		try {
			while (true) {
				Scanner inn = new Scanner(System.in);
				s = inn.nextLine();
				if (s.equals("exit")) {
					out.write(EXIT + "\n");
					out.flush();
					System.out.println("Stream closed");
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
			System.exit(0);
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
