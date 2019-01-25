package players;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;

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
//		Tile t = new Tile();
		return null;
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
		System.out.println("Chose a field to place a tile: "); 
		Scanner in = new Scanner(System.in);
		// to do: make sure this is a number, if not, ask again.
		int num = in.nextInt();
		in.close();
		return num;
	}
	
	public Tile chooseTile() {
		return null;
	}
	
	public Tile chooseRotation(Tile[] allRots) {
		return null;
	}

	private void showTilesAtHand() {
		int numNonNullTile = getNumberOfNonNullTile();
		switch (numNonNullTile) { 
			case 0:
				System.out.println("");
				break;
			case 1:
				
		
		}
	}
	
	private int getNumberOfNonNullTile() {
		int result = 0;
		for (int i = 0; i < tilesAtHand.length; i++) {
			if (tilesAtHand[i] != null) {
				result += 1;
			}
		}
		return result;
	}
	
	private ArrayList<Tile> getNonNullTile() {
		ArrayList<Tile> result = new ArrayList<>();
		for (int i = 0; i < tilesAtHand.length; i++) {
			if (tilesAtHand[i] != null) {
				result.add(tilesAtHand[i]);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
		String value = "V";
		String left = "L";
		String right = "R";
		String vertical = "V";
		String template =
				// make sure each line is of the same total length
				// for GameTUI to display 3 tiles horizontally
                "    / \\    \n" +
                "   / " + value + " \\   \n" +
                "  / " + left + " " + right + " \\  \n" +
                " /   " + vertical + "   \\ \n" +
                " --------- \n"  ;
		
		System.out.println(template);
		
		String template2 =
				// make sure each line is of the same total length
				// for GameTUI to display 3 tiles horizontally
                "    / \\    " + "    / \\    \n" +
                "   / " + value + " \\   " +"   / " + value + " \\   \n" +
                "  / " + left + " " + right + " \\  " + "  / " + left + " " + right + " \\  \n" +
                " /   " + vertical + "   \\ " + " /   " + vertical + "   \\ \n" +
                " --------- " + " --------- \n"  ;
		System.out.println(template2);
		
		String template3 =
				// make sure each line is of the same total length
				// for GameTUI to display 3 tiles horizontally
                "    / \\    " + "    / \\    " + "    / \\    \n" +
                "   / " + value + " \\   " +"   / " + value + " \\   " + "   / " + value + " \\   \n" +
                "  / " + left + " " + right + " \\  " + "  / " + left + " " + right + " \\  " + "  / " + left + " " + right + " \\  \n" +
                " /   " + vertical + "   \\ " + " /   " + vertical + "   \\ " + " /   " + vertical + "   \\ \n" +
                " --------- " + " --------- " + " --------- \n"  ;
		System.out.println(template3);
		
		String template4 =
				// make sure each line is of the same total length
				// for GameTUI to display 3 tiles horizontally
                "    / \\    " + "    / \\    " + "    / \\    " + "    / \\    \n" +
                "   / " + value + " \\   " +"   / " + value + " \\   " + "   / " + value + " \\   " + "   / " + value + " \\   \n" +
                "  / " + left + " " + right + " \\  " + "  / " + left + " " + right + " \\  " + "  / " + left + " " + right + " \\  " + "  / " + left + " " + right + " \\  \n" +
                " /   " + vertical + "   \\ " + " /   " + vertical + "   \\ " + " /   " + vertical + "   \\ " + " /   " + vertical + "   \\ \n" +
                " --------- " + " --------- " + " --------- " + " --------- \n";
		System.out.println(template4);
		
	}
	
}
