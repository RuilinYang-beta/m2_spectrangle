package players;

import java.util.Scanner;
import spectranglegame.*;

public class HumanPlayer extends Player{
	
	private String name;
	
	public HumanPlayer(String n) {
		super(n);
	}
	
	public String getName() {
		return this.name;
	}
	
	
	public int[] makeMove(Board b) {
		return null;
	}
	
	public int determineMove(Board b) {
		Scanner in = new Scanner(System.in);
		int num = in.nextInt();
		return num;
	}

}
