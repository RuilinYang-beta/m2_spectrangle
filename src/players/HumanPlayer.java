package players;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

import spectranglegame.*;

public class HumanPlayer extends Player{
	
//	private String name;
//	private Tile[] tilesAtHand;
	private int score;
	private Scanner in;
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
	public int chooseTileIdx(int numOfTile) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Integer i = null;
		String s = null;
		while (true) {
			System.out.print("> Player " + name + ", Chose a Tile from :[0, " + (numOfTile - 1) + "] (inclusive): ");
			try {
				s = br.readLine();
			} catch (IOException e) {
				System.out.println("IOException happens in chooseTileIdx br.readLine.");
			}
			
			try {
				i = Integer.parseInt(s);
				if ((0 <= i) && (i < numOfTile)) {
					break;
				} else {
					System.out.println("Input number out of range. Please try again.");
					i = null;
				}
			} catch (NumberFormatException e) {
				System.out.println("Try again. Please input a number.");
			}
		}
		
		return i;
	}
	
	
	public int chooseFieldIdx() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Integer i = null;
		String s = null;
		while (true) {
			System.out.print("> Player " + name + ", Please chose a field: ");
			try {
				s = br.readLine();
			} catch (IOException e) {
				System.out.println("IOException happens in br.readLine.");
			}
			
			try {
				i = Integer.parseInt(s);
				if ((0 <= i) && (i <= 35)) {
					break;
				} else {
					System.out.println("Index is not on board. Please try again.");
					i = null;
				}
			} catch (NumberFormatException e) {
				System.out.println("Try again. Please input a number.");
			}
		}
		
		return i;
	}

	
	
	public int chooseRotationIdx() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Integer i = null;
        String s = null;
        
		while (true) {
			System.out.print("> Player " + name + ", Chose a Rotation from :[0, 2] (inclusive): ");
            try {
                s = br.readLine();
            } catch (IOException e) {
                System.out.println("IOException happens in br.readLine.");
            }
            
            try {
                i = Integer.parseInt(s);
                if ((0 <= i) && (i <= 2)) {
                    break;
                } else {
                    System.out.println("Index is out of range. Please try again.");
                    i = null;
                }
            } catch (NumberFormatException e) {
                System.out.println("Try again. Please input a number.");
            }
		}

    	return i;
	}
	
	public String getName() {
		return this.name;
	}
	
	
	

//    private String readLine(String prompt) {
//        String value = "";
//        boolean intRead = false;
//        @SuppressWarnings("resource")
//        Scanner line = new Scanner(System.in);
//        do {
//            System.out.print(prompt);
//            try (Scanner scannerLine = new Scanner(line.nextLine());) {
//                if (scannerLine.hasNextLine()) {
//                    intRead = true;
//                    value = scannerLine.nextLine();
//                }
//            }
//        } while (!intRead);
//        return value;
//    }
//    
    public int chooseField() {
		System.out.print("Chose a field to place a tile: "); 
		Scanner in = null;
		// make sure this is a number && is a index on board; if not, ask again.
		Integer i = null;
		do {
			try {
				in = new Scanner(System.in);
				i = in.nextInt();
				if ((i < 0) || (36 <= i)) {
					System.out.print("Your input index does not exist on the board, please try again: ");
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

//		System.out.println("chosen Field Idx is: " + p.chooseFieldIdx());
//		System.out.println("chosen Tile Idx is: " + p.chooseTileIdx(3));
//		System.out.println("chosen Rotation Idx is: " + p.chooseRotationIdx());
		
		System.out.println(p.readLine("> Question 1:"));
		System.out.println(p.readLine("> Question 2:"));
		System.out.println(p.readLine("> Question 3:"));
		
		
	}
	
}
