package players;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

import spectranglegame.*;

public class HumanPlayer extends Player{
	
	private String name;
	private Tile[] tilesAtHand;
	private int score;
	// later move this to PlayerClient
	private static final List<Integer> BONUSES =       Arrays.asList(1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 2, 4, 1, 4, 2, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 3, 1);
	
	// ======================== Constructor ========================
	public HumanPlayer(String n, Tile[] tiles) {
		super(n,tiles);
	}
	
	public HumanPlayer(String n) {
		super(n);
	}
	
	
	// ======================== Making Choices ========================
    public int chooseFieldIdx() {
    	Integer choice = null;
    	
    	while (true) {
			System.out.print("> Please chose a field: ");
			Scanner in = new Scanner(System.in);
			
			if (in.hasNextLine()) {
				try {
					choice = in.nextInt();
					if ((0 <= choice) && (choice <= 35)) {
						break;
					} else {
						System.out.println("Index is not on board. Please try again.");
						choice = null;
					}
				} catch (InputMismatchException e) {
					System.out.println("InputMismatch. Please try again.");
				}
			}
		}
		System.out.println("Player is going to return " + choice + " to TUI");
		return choice;	
	}

	public int chooseTileIdx(int numOfTile) {
		System.out.print("Chose a Tile from :[0, " + (numOfTile - 1) + "] (inclusive): "); 
		Scanner in = null;
		// make sure this is a number && is a index on board; if not, ask again.
		Integer i = null;
		do {
			try {
				in = new Scanner(System.in);
				i = in.nextInt();
				if ((i < 0) || (numOfTile <= i)) {
					System.out.print("Index out of bound, please try again: ");
					i = null;
				}
			} catch(InputMismatchException e) {
				System.out.print("Your input is not a number, please try again: ");
				i = null;
			}
		} while (i == null);
		
		System.out.println("Player is going to return " + i + " to TUI.");
    	return i;
	}

	public int chooseRotationIdx() {
		System.out.print("Chose a Rotation from :[0, 2] (inclusive): "); 
		Scanner in = null;
		// make sure this is a number && is a index on board; if not, ask again.
		Integer i = null;
		do {
			try {
				in = new Scanner(System.in);
				i = in.nextInt();
				if ((i < 0) || (3 <= i)) {
					System.out.print("Index out of bound, please try again: ");
					i = null;
				}
			} catch(InputMismatchException e) {
				System.out.print("Your input is not a number, please try again: ");
				i = null;
			}
		} while (i == null);
		
    	in.close();
    	return i;
	}
	
	public String getName() {
		return this.name;
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
    


	
	public static void main(String[] args) {
		HumanPlayer p = new HumanPlayer("joke");
//		p.chooseFieldIdx();
//		p.chooseTileIdx(3);
//		p.chooseRotationIdx();
		
		
	}
	
}
