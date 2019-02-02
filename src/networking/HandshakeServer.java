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

public class HandshakeServer extends Thread{

	private static final String EXIT = "exit";
	private static final String HELLO = "Hello";
	private static final String PLAY  = "Play";
	private static final String WAITING = "Waiting";
	
	private String playerName;
	private Integer numOfPlayers;
	
	private Socket sock;
	private BufferedReader in;
	private BufferedWriter out;
	
	public HandshakeServer(Socket sockArg) throws IOException {
		sock = sockArg;
		in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
	}
	
	/**
	 * Reads strings of the stream of the socket-connection and writes the
	 * characters to the default output.
	 */
	public void run() {
		parseFromClient();  // write name of this player 
		parseFromClient();  // write number of player of a game 
		
		updateAndNotify();  // update server's maps, 
							// notify already-waiting players of the same map
							// see the possibility of start a game
							// and then this thread is good to die
	}
	
	public void parseFromClient() {
		String s = null;
		
		try {
			s = in.readLine();
		} catch (IOException e) {
			System.out.println("IOException in parsing info from client.");
		}
		
        String[] info = null;
        if (s != null) {
        	info = s.split("\\s");
        } else {
        	System.out.println("Client sends null");
        }
        
        switch (info[0]) {
			case HELLO:
				this.playerName = info[1];
				sendToClient(HELLO, "", this.out);
				break;
			case PLAY:
				this.numOfPlayers = Integer.parseInt(info[1]); 
				break;
			default:
				System.out.println("You shouldn't reach here.");
				break;
		}
	}

	private void sendToClient(String cmd, String other, BufferedWriter out) {
		String msg = null;
		
		switch (cmd) {
			case HELLO:
				msg = HELLO + " false false false false";
				break;
			case WAITING:
				msg = WAITING + " " + other;
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
	
	public void updateAndNotify() {
		switch (numOfPlayers) {
			case 2:
				synchronized (Server.twoplayers) {
					Server.twoplayers.put(sock, playerName);
					notifyPlayers(Server.twoplayers, numOfPlayers);
					tryStartGameAndClearMap(Server.twoplayers, numOfPlayers);
				}
				break;
			case 3:
				synchronized (Server.threeplayers) {
					Server.threeplayers.put(sock, playerName);
					notifyPlayers(Server.threeplayers, numOfPlayers);
					tryStartGameAndClearMap(Server.threeplayers, numOfPlayers);
				}
				break;
			case 4:
				synchronized (Server.fourplayers) {
					Server.fourplayers.put(sock, playerName);
					notifyPlayers(Server.fourplayers, numOfPlayers);
					tryStartGameAndClearMap(Server.fourplayers, numOfPlayers);
				}
				break;
	
			default:
				break;
		}
	}
	
	private void notifyPlayers(Map<Socket, String> map, Integer wantedNum) {
		for (Map.Entry<Socket, String> entry: map.entrySet()) {
			Socket s = entry.getKey();
			BufferedWriter outt = null;
			
			try {
				outt = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			} catch (IOException e) {
				System.out.println("IOException in notifyPlayers.");
			}
			
			Integer waiting = wantedNum - map.size();
			sendToClient(WAITING, "" + waiting, outt);
		}
		
		
	}
	
	private void tryStartGameAndClearMap(Map<Socket, String> map, Integer wantedNum) {
		if (map.size() == wantedNum) {
			Map<Socket, String> mapCopy = new HashMap<>();
			mapCopy.putAll(map);
			
			Thread g = new GameControl(mapCopy);
			g.start();
			
			map.clear();
		}
	}
	

}
