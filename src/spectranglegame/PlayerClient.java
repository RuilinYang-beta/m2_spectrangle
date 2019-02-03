package spectranglegame;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.stream.Stream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import players.*;
import utils.*;

// This is a PlayerClient
public class PlayerClient extends Thread{
	
	// =========== Signals: later will be changed to better cope protocol ===========
    private static final String FIELD = "Field?";
    private static final String FIELD_WRONG = "Wrong Field";
    private static final List<Integer> BONUSES =       Arrays.asList(2, 10, 11, 13, 14, 20, 26, 30, 34);
    
    // =========== Instance Field ===========
    // ---------- old ones ---------- 
	private GameControl control;
	
	// ---------- new ones ---------- 
	private String myName;
	private int numOfPlayer;
	private Socket csk;
	private BufferedReader in;
	private BufferedWriter out;
	private BufferedReader sysin;
	
	private Board board;
	private Player me;
	private List<Player> listPlayers; 
	
	// =========== Constructor ===========
	public PlayerClient(GameControl gc, Board bd, List<Player> lp, Bag bg) {
		this.control = gc;
		this.board = bd;
		this.listPlayers = lp;
	}

	// only for test purpose
	public PlayerClient(Board b) {
		this.board = b;
	}
	
	
	public PlayerClient(Socket s, String nm, Integer numP, BufferedReader br) {
		// the specific info about this user
		this.myName = nm;
		this.numOfPlayer = numP;
		this.csk = s;
		try {
			this.in  = new BufferedReader(new InputStreamReader(csk.getInputStream()));
			this.out = new BufferedWriter(new OutputStreamWriter(csk.getOutputStream()));
		} catch(IOException e) {
			System.out.println("IOException in constructor of PlayerTUI.");
		}
	
		// the local shadow of model
		this.board = new Board();
		this.listPlayers = new ArrayList<Player>(); // other player's name unknown yet
		
		this.sysin = br;
	}
	
	// ========================= Networking =========================
	// when server sends some signal, it will be handled by calling other functions 
	public void run() {
		System.out.println("You are in PlayerClient.run()");
		
		while (true) {
			parseFromGameControl();
		}
		
	}
		
	public void parseFromGameControl() {
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
				keepInformed(Commands.ORDER, info);
				break;
			case Commands.GIVE:
				updateLocalTile(Commands.GIVE, info[1], info[2]);
				break;
			case Commands.TURN:
				if (info[1].equals(myName)) {
					String toGameControl = promptChoice();
					if (!toGameControl.equals("")) { 
						sendToGameControl(toGameControl); 
						}
				} else {
					keepInformed(Commands.TURN, info);
				}
				break;
			case Commands.MOVE:
				updateLocalTile(Commands.MOVE, info[1], info[3]);
				updateLocalBoard(Commands.MOVE, info[2], info[3]);
				break;
			case Commands.SKIP:
				if (info.length == 2 && (!info[1].equals(myName)) ) {
					keepInformed(Commands.SKIP, info);
				}
				if (info.length == 4) {
					updateLocalTile(Commands.SKIP, info[1], info[2]);
				}
				break;
			case Commands.END:
				System.out.println("You should see this line at each client!");
				break;
			case Commands.ERROR:
				break;
		}
	}
	
	public void sendToGameControl(String whole) {
		try {
			out.write(whole);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.out.println("ERROR: unable to communicate to server");
            e.printStackTrace();
		}
	}

	
	// ========================= Prompt User Input =========================	
	public String promptChoice() {
		System.out.println("Player " + myName + ", it's your turn.");
		Display.showInfoToPlayer(listPlayers, board);
		
		ArrayList<Tile> nonNulls = me.getNonNullTiles();
        Display.showMultiTilesUp(nonNulls);
        
        Map<String, Set<Integer>> allMove = Sanitary.generateAllPossibleMoves(board, me);
        
        if (allMove.size() == 0) {
            
            // it's first move
            if (board.getEmptyFields().size() == 36) {
                return getLegalMoveString(true, nonNulls, allMove);
            }
            
            // user don't have a matching Tile && Bag is not empty
            if (board.getEmptyFields().size() + getNumTilesAtPlayers() < 36) {
                Integer tileIdxToSwap = chooseSkipOrSwap(nonNulls.size());
                
                if (tileIdxToSwap == null) { // user want to skip
                	return Commands.SKIP;
                } else {					 // user want to swap
                	return Commands.SKIP + " " + nonNulls.get(tileIdxToSwap).stringTile();
                }              
            }
            
            // user don't have a matching Tile && Bag is empty
            if (control.getBoard().getEmptyFields().size() + getNumTilesAtPlayers() == 36) {
                toSkip();
                return Commands.SKIP;
            }
            
            else { return "";}
        }
        // not the first move && user has at least a matching tile
        else {
            return getLegalMoveString(false, nonNulls, allMove);
        }
	}
	
	
	// ========================= Update Local Model ========================
	public void updateLocalTile(String cmd, String playerName, String tileEncoding) {
		Player p = getPlayerByName(playerName);
		
		// fill the first null spot of player's tile array with tile
		if (cmd.equals(Commands.GIVE)) {
			if (p == null) {
				p = new HumanPlayer(playerName);
				this.listPlayers.add(p);
			}
			
			// btw, init me
			if ((this.me == null) && (getPlayerByName(myName) != null)) {
				this.me = getPlayerByName(myName);
			}
			
			p.takeTheTile(new Tile(tileEncoding));
		}
		
		// server confirm a move / skip, should remove the tile from player
		if (cmd.equals(Commands.MOVE) || cmd.equals(Commands.SKIP)){
			
			for (int i = 0; i < 4; i++) {
				Tile t = p.getTiles()[i];
				// you don't want to call toString on a null object.
				if (t != null) {
					if (t.toString().equals(tileEncoding.substring(1))) {
						p.getTiles()[i] = null;
					}
				}

			}
		}

	}
	
	public void updateLocalBoard(String cmd, String idx, String tileEncoding) {
		Integer theField = Integer.parseInt(idx);
		this.board.setTile(theField, new Tile(tileEncoding));
	}
	
	// ========================= Console IO Related =========================
	// ------------- When user is able to make a move -------------
	public String getLegalMoveString(boolean isFirstMove, 
									ArrayList<Tile> nonNull, 
									Map<String, Set<Integer>> allMove) {
		if (isFirstMove) {
			int idx = chooseTileIdx(nonNull.size());
	        Tile theBaseTile = nonNull.get(idx);
	        Integer theField = chooseFieldIdx(true);
	        
	        ArrayList<Tile> allRota = generateAllRotations(theField, theBaseTile);
	        Display.showAllRotation(allRota, theField);
	        Integer theRota = chooseRotationIdx();
	        Tile theFinalTile = allRota.get(theRota);
	        
	        System.out.println("You made the first first move.");
	        
	        return Commands.MOVE + " " + theField + " " + theFinalTile.stringTile();
		
		} else {
			 Tile theBaseTile = null;
	            while (true) {
	                int idx = chooseTileIdx(nonNull.size());
	                theBaseTile = nonNull.get(idx);
	                
	                if (Sanitary.optionsForBaseTile(allMove, theBaseTile).size() > 0) { break; } 
	                else { System.out.println("- I tell you what, this tile is by no rotation eligible. Choose another.");}
	            }
	            
	            Integer theField = null;
	            while (true) {
	                theField = chooseFieldIdx(false);
	                
	                if (Sanitary.optionsForBaseTileAndField(allMove, theBaseTile, theField).size() > 0) {break;}
	                else { System.out.println("- Too young too simple, field not possible. Choose another.");}  
	            }
	            
	            Tile theFinalTile = null;
	            while (true) {
	                ArrayList<Tile> allRota = generateAllRotations(theField, theBaseTile);
	                Display.showAllRotation(allRota, theField);
	                Integer theRota = chooseRotationIdx();
	                theFinalTile = allRota.get(theRota);
	                
	                if ( (allMove.get(theFinalTile.stringTile()) != null) && (allMove.get(theFinalTile.stringTile()).contains(theField))) { break; } 
	                else {  System.out.println("- Rotation wrong, use your brain. "); }
	            }
	            
	            return "Move" + " " + theField + " " + theFinalTile.stringTile();
		}
	}
	
	public int chooseTileIdx(int numOfTile) {
        Integer i = null;
        String s = null;
        
        while (true) {
            
            System.out.println("> Player " + myName + ", Chose a Tile from :[0, " + (numOfTile - 1) + "] (inclusive): ");
            
            try {
                s = sysin.readLine();
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
	
	public int chooseFieldIdx(boolean isFirstMove) {
		Integer i = null;
		String s = null;
		while (true) {
			System.out.print("> Player " + myName + ", Please chose a field: ");
			try {
				s = sysin.readLine();
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
        Integer i = null;
        String s = null;
        
		while (true) {
			System.out.print("> Player " + myName + ", Chose a Rotation from :[0, 2] (inclusive): ");
            try {
                s = sysin.readLine();
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
	
	// ------------- When user is NOT able to make a move -------------
	public Integer chooseSkipOrSwap(int numOfTile) {
		Integer i = null;
		String s = null;
		
		while (true) {
			
			System.out.println("> Player " + myName + ", you have no Tile that matches the board. ");
			System.out.println("- Enter 0 to skip the turn, or ");
			System.out.println("- Enter 1 to swap a Tile with bag and pass the turn.");
			
			try {
				s = sysin.readLine();
			} catch (IOException e) {
				System.out.println("IOException happens in chooseTileIdx br.readLine.");
			}
			
			try {
				i = Integer.parseInt(s);
				
				if ( i == 0) {
					System.out.println("You've chosen to skip the turn.");
					return null;
				} else if ( i == 1) {
					Integer tileIdxToSwap = chooseTileIdxToSwap(numOfTile);
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
	
	public Integer chooseTileIdxToSwap(int numOfTile) {
        Integer i = null;
        String s = null;
        
        while (true) {
            
            System.out.println("> Player " + myName + ", Chose a Tile from :[0, " + (numOfTile - 1) + "] (inclusive) to swap with bag: ");
            
            try {
                s = sysin.readLine();
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
	
	public void toSkip() {
		
		System.out.println("> Player " + myName + ", you have no Tile that matches the board, ");
		System.out.println("> and there's no Tile in bag to swap. ");
		System.out.println("> Enter anything to skip your turn");
		
		try {
			String s = sysin.readLine();
		} catch (IOException e) {
			System.out.println("IOException happens in chooseTileIdx br.readLine.");
		}
	}
	
	// ========================= Keep the User Informed =========================
	public void keepInformed(String cmd, String[] splited) {
		switch(cmd) {
			case Commands.ORDER:
				String order = "";
				for (int i = 1; i < splited.length; i++) {
					order += splited[i];
					order += " -> ";
				}
				order = order.substring(0, order.length()-4);
				System.out.print("Play order is determined as : ");
				System.out.println(order);
				break;
			case Commands.TURN:
				System.out.println("Waiting for Player " + splited[1] + "'s move...");
				break;
			case Commands.SKIP:
				if (splited.length == 2) {
					System.out.println("Player " + splited[1] + " has skipped his/her turn...");
					break;
				}
				
				
		}
	}
	
	
	// ========================= Other Helper =========================
	public int getNumTilesAtPlayers() {
		int sum = 0;
		for (Player p : listPlayers) {
			sum += p.getNonNullTiles().size();
		}
		return sum;
	}
	
	public ArrayList<Tile> generateAllRotations(Integer idx, Tile baseT) {
		boolean isFacingUp = Board.isFacingUp(idx);
    	ArrayList<Tile> allRotation = new ArrayList<>();

    	if (isFacingUp) {
    		// fill 3 rotation facing up
    		allRotation.addAll( Arrays.asList(baseT, 
    										  baseT.rotateTileTwice(), 
    										  baseT.rotateTileFourTimes()
    										  )
    				);
    	} else {
    		// fill 3 rotation facing down
    		allRotation.addAll( Arrays.asList(baseT.rotateTileOnce(), 
    										  baseT.rotateTileOnce().rotateTileTwice(), 
    										  baseT.rotateTileOnce().rotateTileFourTimes()
    										  )
    				);
    	}
    	
    	return allRotation;
	}
	
	// ========================= Old Ones =========================

    public Integer askTileToSwap(Player p, List<Tile> nonNullAtPlayer) {
    	
    	int firstNonNullInBag = control.getFirstNonNullIdx();
    	
    	if (firstNonNullInBag < 36) {
    		int toSwapIdx = p.chooseTileIdxToSwap(nonNullAtPlayer.size());
    		return toSwapIdx;
    	} else {
    		// Later this msg will be send to player via socket
    		System.out.println("Bag is empty, no tile to swap. Your turn ends.");
    		return null;
    	}
    	
    }
  
    public void swapTileAtPlayer(Player p, Tile toToss, Tile toEquip) {
		for (int i = 0; i < p.getTiles().length; i++) {
			if ( (p.getTiles()[i] != null) && 
					(p.getTiles()[i].toString().equals(toToss.toString())) ) {
				p.getTiles()[i] = toEquip;
				break;
			}
		}
		// Later this msg will be sent to Player p via socket
		// For now print msg to TUI's console.
		System.out.println("Your tile is swapped!");
    	
    }
    
 
    
    // ========================= Query Model Data =========================
	public Player getPlayerByName(String name) {
		for (Player p : listPlayers) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		return null;
	}

	
	/**
	 * @param srd
	 * @param base
	 * @return
	 */
	/*
	 * @requires (srd[1] != null) || (srd[2] != null) || (srd[3] != null);
	 * @requires ((srd[0] == 'U') && (t.isFacingUp())) || ((srd[0] == 'D') && (!t.isFacingUp()));
	 * @requires t.getRotation() == 0;
	 */
	public boolean boarderPossibleToMatch(Character[] srd, Tile base) {
		
		if (srd[0] == 'U') {
			ArrayList<Tile> upRotations = new ArrayList<Tile>(
					Arrays.asList(base, base.rotateTileTwice(), base.rotateTileFourTimes()));
			for (Tile up : upRotations) {
				boolean match = boarderMatchs(srd, up);
				if (match) {
					return true;
				}
			}
		}
		else {
			ArrayList<Tile> downRotations = new ArrayList<Tile>(
					Arrays.asList(base.rotateTileOnce(), base.rotateTileOnce().rotateTileTwice(), base.rotateTileOnce().rotateTileFourTimes()));
			for (Tile down : downRotations) {
				boolean match = boarderMatchs(srd, down);
				if (match) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Given the surrounding infos of an empty field with at least one neighboring tile, and a specific Tile, 
	 * can the Tile be placed on the field?
	 * @param srds The surrounding information of the field.
	 * @param t The Tile you want to place on the empty field, should be a baseTile (with rotation 0)
	 * @return true If the tile match the surroundings.
	 */
	/*
	 * @requires (srd[1] != null) || (srd[2] != null) || (srd[3] != null);
	 * @requires ((srd[0] == 'U') && (t.isFacingUp())) || ((srd[0] == 'D') && (!t.isFacingUp()));
	 * @requires t.getRotation() == 0;
	 */
	public boolean boarderMatchs(Character[] srd, Tile t) {
		// Joker can be placed anywhere as long as there's a neighboring Tile
		if (t.isJoker()) {
			return true;
		}
		
		// (has a vertical neighbor) && (vertical neighborTile is not Joker) && (vertical color mismatch)
		if ((srd[1] != null) && (srd[1] != 'W') && (t.getVertical() != srd[1])) {
			return false;
		}
		
		// (has a left neighbor) && (vertical neighborTile is not Joker) && (left color mismatch)
		if ((srd[2] != null) && (srd[2] != 'W') && (t.getLeft() != srd[2])) {
			return false;
		}
		
		// (has a right neighbor) && (vertical neighborTile is not Joker) && (right color mismatch)
		if ((srd[3] != null) && (srd[3] != 'W') && (t.getRight() != srd[3])) {
			return false;
		}
		
		return true;
	}
	

    // ========================= Main =========================
    public static void main(String[] args) {
//    	Tile[] tiles = {new Tile(3, "RGB")};
//    	List<Player> pl = Arrays.asList(new HumanPlayer("player", tiles));
//    	
//		GameTUI temp = new GameTUI(new GameControl(pl, true), 
//								   new Board(), pl);
//		
//		// test of showTile
////		temp.showThreeTilesUp(Arrays.asList(new Tile(3, "RGB"), new Tile(1, "HEY"), new Tile(2, "OOP")));
////		temp.showThreeTilesDown(Arrays.asList(new Tile(3, "RGB"), new Tile(1, "HEY"), new Tile(2, "OOP")));
//		
//		// test of askField, askTileAndRotation:
//		Tile[] tiles1 = {new Tile(3, "RGB"), null, null, null};
//		Tile[] tiles2 = {new Tile(3, "RGB"), null, new Tile(1, "HEY"), null};
//		Tile[] tiles3 = {new Tile(3, "RGB"), new Tile(1, "HEY"), null, new Tile(2, "OOP")};
//		Tile[] tiles4 = {new Tile(3, "RGB"), new Tile(1, "HEY"), new Tile(2, "OOP"), new Tile(4, "WHO")};
//		
//		HumanPlayer p = new HumanPlayer("player", tiles2);
//		Tile t0 = temp.askTile(p, true);
//		int i = temp.askField(p, new Board(), true);
//		Tile t1 = temp.askRotation(p, t0, true);
//		
		
		
	}

}
