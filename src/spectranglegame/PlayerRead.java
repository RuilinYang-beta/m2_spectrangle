package spectranglegame;

import java.io.BufferedReader;
import java.io.IOException;

import utils.Commands;

public class PlayerRead extends Thread {
	
	private PlayerClient pc = null;
	private BufferedReader in = null;
	
	public PlayerRead(PlayerClient client, BufferedReader read) {
		this.pc = client;
		this.in = read;
	}
	
	public void run() {
		while (true) {
			parseFromServer();
		}
	}
	
	public void parseFromServer() {
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
			case Commands.ORDER:
				System.out.println("You receive this from GameControl: " + s);
				break;
			case Commands.GIVE:
				break;
			case Commands.TURN:
				System.out.println("You receive this from GameControl: " + s);
				break;
			case Commands.MOVE:
				break;
			case Commands.SKIP:
				break;
			case Commands.END:
				break;
			case Commands.ERROR:
				break;
		}
	}

}
