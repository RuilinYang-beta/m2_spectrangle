package players;

import java.util.Scanner;
import spectranglegame.*;

public class HumanPlayer extends Player{
	
	private String name;
	private Tile[] tilesAtHand;
	private int score;
	
	
	public HumanPlayer(String n, Tile[] tiles) {
		super(n,tiles);
	}
	
	public String getName() {
		return this.name;
	}
	
	
	public int[] makeMove(Board b) {
		return null;
	}
	
	
	public String determineMove(Board b) {
		String prompt = "Player " + getName() + "what is your move?";
		String choice = readLine(prompt);
		
	}
	
    private String readLine(String prompt) {
        String value = "";
        boolean intRead = false;
        @SuppressWarnings("resource")
        Scanner line = new Scanner(System.in);
        do {
            System.out.print(prompt);
            try (Scanner scannerLine = new Scanner(line.nextLine());) {
                if (scannerLine.hasNextLine()) {
                    intRead = true;
                    value = scannerLine.nextLine();
                }
            }
        } while (!intRead);
        return value;
    }
    
	public int chooseField(Board b) {
		Scanner in = new Scanner(System.in);
		int i = in.nextInt();
    	in.close();
    	if(Board.isLegalIdx(i)) {
    		return i;
    	}else {
    		return -1;
    	}
	}

	
	public Tile chooseTile() {
		Scanner in = new Scanner(System.in);
    	int i = in.nextInt();
    	Tile[] tiles = tilesAtHand;
    	in.close();
    	if(i >= 0 && i < 5) {
    		return tiles[i];
    	}else {
    		return null;
    	}
	}
	public Tile chooseRotation(Tile t) {
		return null;
	}

	
	
}
