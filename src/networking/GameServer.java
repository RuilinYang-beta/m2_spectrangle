package networking;

import java.net.Socket;
import java.util.*;

import players.Player;
import spectranglegame.GameControl;

public class GameServer extends GameControl{
	
	private static final String USAGE = "usage: " + Server.class.getName() + " <ip> "; 
	
	public GameServer(List<Player> lp, boolean shuffle) {
		super(lp,shuffle);
	}
	
	
	
	
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
	
		
	}

}
