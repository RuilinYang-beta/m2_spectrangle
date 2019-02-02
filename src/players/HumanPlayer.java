package players;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Observable;

import spectranglegame.*;

public class HumanPlayer extends Player {
	
//	private String name;
//	private Tile[] tilesAtHand;
	private int score;
	private Scanner in;
	// later move this to PlayerClient
	private static final List<Integer> BONUSES =       Arrays.asList(2, 10, 11, 13, 14, 20, 26, 30, 34);
	
	// ======================== Constructor ========================
	public HumanPlayer(String n, Tile[] tiles) {
		super(n,tiles);
	}
	
	public HumanPlayer(String n) {
		super(n);
	}
	
	
	// ======================== Making Choices ========================
//	public int chooseTileIdx(int numOfTile, boolean isFirstMove) {
	public int chooseTileIdx(int numOfTile) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Integer i = null;
		String s = null;
		while (true) {
			
			System.out.println("> Player " + name + ", Chose a Tile from :[0, " + (numOfTile - 1) + "] (inclusive): ");
			
			try {
				s = br.readLine();
			} catch (IOException e) {
				System.out.println("IOException happens in chooseTileIdx br.readLine.");
			}
			
			try {
				i = Integer.parseInt(s);
				
				// User choose a legal tile index 
				if ((0 <= i) && (i < numOfTile)) {
					break;
				} 
				else {
					System.out.println("Input number out of range. Please try again.");
					i = null;					
				}
				
			} catch (NumberFormatException e) {
				System.out.println("Try again. Please input a number.");
			}
		}
		
		return i;
	}
	
	public Integer chooseSkipOrSwap(int numOfTile) {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		Integer i = null;
		String s = null;
		
		while (true) {
			
			System.out.println("> Player " + name + ", you have no Tile that matches the board. ");
			System.out.println("- Enter 0 to skip the turn, or ");
			System.out.println("- Enter 1 to swap a Tile with bag and pass the turn.");
			
			try {
				s = br.readLine();
			} catch (IOException e) {
				System.out.println("IOException happens in chooseTileIdx br.readLine.");
			}
			
			try {
				i = Integer.parseInt(s);
				
				if ( i == 0) {
					System.out.println("You've chosen to skip the turn.");
//					break;
					return null;
				} else if ( i == 1) {
					Integer tileIdxToSwap = chooseTileIdxToSwap(numOfTile);
//					break;
					return tileIdxToSwap;
					
				}
				else {
					System.out.println("Try again. Please input 0 or 1.");
					i = null;			
				}
				
			} catch (NumberFormatException e) {
				System.out.println("Try again. Please input 0 or 1.");
			}
		}
	}
	
	public void toSkip() {
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		
		System.out.println("> Player " + name + ", you have no Tile that matches the board, ");
		System.out.println("> and there's no Tile in bag to swap. ");
		System.out.println("> Enter anything to skip your turn");
		
		try {
			String s = br.readLine();
		} catch (IOException e) {
			System.out.println("IOException happens in chooseTileIdx br.readLine.");
		}
	}
	
	public int chooseTileIdxToSwap(int numOfTile) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Integer i = null;
        String s = null;
        
        while (true) {
            
            System.out.println("> Player " + name + ", Chose a Tile from :[0, " + (numOfTile - 1) + "] (inclusive) to swap with bag: ");
            
            try {
                s = br.readLine();
            } catch (IOException e) {
                System.out.println("IOException happens in chooseTileIdx br.readLine.");
            }
            
            try {
                i = Integer.parseInt(s);
                
                // User choose a legal tile index 
                if ((0 <= i) && (i < numOfTile)) {
                    break;
                } 
                else {
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
	
	public int chooseFieldIdx(boolean isFirstMove) {
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
					if (!isFirstMove) {
						break;
					} else if (!BONUSES.contains(i)) {
						break;
					} else {
						System.out.println("First Move cannot be on a bonus Field. Please try again.");
					}
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
	
	// ======================== Quries ========================
	
	public String getName() {
		return this.name;
	}
	
	public ArrayList<Tile> getNonNullTiles(){
    	// get non-null Tile and store them in a ArrayList (such that there's no null in between)
		ArrayList<Tile> nonNullTiles = new ArrayList<>();
		
		for (int i = 0; i < tilesAtHand.length; i++) {
			if (tilesAtHand[i] != null) {
				nonNullTiles.add(tilesAtHand[i]);
			}
		}
		return nonNullTiles;
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
