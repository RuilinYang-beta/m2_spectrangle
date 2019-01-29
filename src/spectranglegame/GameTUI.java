package spectranglegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import players.*;

import javax.swing.text.html.HTMLDocument.HTMLReader.CharacterAction;
import java.util.Scanner;

public class GameTUI {
	
	// =========== Static Fields: only for test purpose ===========
	private static final List<Integer> bonuses =       Arrays.asList(1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 2, 4, 1, 4, 2, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 3, 1);
    private static List<Integer> values =        Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static List<Character> vertical =    Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static List<Character> left =        Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static List<Character> right =       Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

    private static final String FIELD = "Field?";
    private static final String FIELD_WRONG = "Wrong Field";
    private static final String TILE = "Tile?";
    private static final String ROTATION = "Rotation?";
    
    // =========== Instance Field ===========
	private GameControl control;
	private Board board;
	private Bag bag;
	private Tile chosenBaseTile = null;
	private Integer chosenFieldIdx = null;
	private Boolean chosenFieldFacingUp = null;

	private List<Player> listPlayers; 
	
	// =========== Constructor ===========
	public GameTUI(GameControl gc, Board bd, List<Player> lp, Bag bg) {
		this.control = gc;
		this.board = bd;
		this.listPlayers = lp;
		this.bag = bg;
	}

	// ========================= Ask User Input =========================
	/**
     * Helper function of askTileAndRotation, if not valid, keep asking until valid.
     * @param p Ask this player for input.
     * @return The Tile (cloned) of the player's choice. 
     */
//    public Tile askTile(Player p, boolean isFirstMove) {
	public Tile askTile(Player p) {
		chosenBaseTile = null;
		
		ArrayList<Tile> nonNulls = p.getNonNullTiles();
		// Later TUI should send this String representation to PlayerClient
		// For now TUI print this String at TUI's console.
		showMultiTilesUp(nonNulls);
    		
		if (canPlay(p)) {
			// make sure user will choose a legal tile
    		int nonNullTileIdx = p.chooseTileIdx(nonNulls.size());
    		
    		// C -> S: Move <index> <tile encoding> (half-way)
    		chosenBaseTile = nonNulls.get(nonNullTileIdx).deepCopy();
			
			return chosenBaseTile;
    	} 
    	// (user can't play) && (bag is not empty)
    	else if (!bagIsEmpty()) {
    		// user have the choice to skip or swap
    		Integer tileIdxToSwap = p.chooseSkipOrSwap(nonNulls.size());
    		
    		if (tileIdxToSwap == null) {
    			// C -> S: Skip
    		} else {
				Tile fromPlayer = nonNulls.get(tileIdxToSwap);
				Tile fromBag = control.swapRandomTileInBag(fromPlayer);
				swapTileAtPlayer(p, fromPlayer, fromBag);
				// C -> S: Skip <encoding of fromPlayer>
			}
    		return null;
    		
    	}
    	// (user can't play) && (bag is empty)
    	else {
    		// user can only skip
    		p.toSkip();
    		// C -> S: Skip
    		return null;
    	}
    }
    
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
    
    /**
     * Ask user to give a valid field, if not valid, keep asking until valid.
     * @param p Ask this player for input.
     * @return The index of the player's choice.
     */
    public Integer askField(Player p, Board b) {
    	chosenFieldIdx = null;
    	chosenFieldFacingUp = null;
    	boolean isLegalField = false;
    	
    	// Move to showInfoToPlayer
//		printBoardDynamic(board);
		
    	do {
    		// later this will be sent to p via socket, and p will parse it 
    		System.out.println(FIELD); 

    		int returnVal = p.chooseFieldIdx();
    		
    		System.out.println(returnVal);
    		
    		// TUI doing the sanitary check, if not sanitary, keep asking until user give legal fieldIdx.
    			// First Move: Not a BONUS Field; field is empty
    			// Normal Move: field is empty; has at least one matching boarder with tiles already on the board.
    		Character[] srd = board.getSurroundingInfo(returnVal);
    		// For non-joker Tile, make sure chosenTile can have at least 1 potential matching boarder, no matter rotation.
    		boolean cond0_nj0 = (srd[1] != null) ? ((chosenBaseTile.toString() + "W").indexOf(srd[1]) >= 0) : false; // yield true if (has a vertical neighbor tile) && (vertical boarder is possible to match)
    		boolean cond0_nj1 = (srd[2] != null) ? ((chosenBaseTile.toString() + "W").indexOf(srd[2]) >= 0) : false; // yield true if (has a left neighbor tile) && (left boarder is possible to match)
    		boolean cond0_nj2 = (srd[3] != null) ? ((chosenBaseTile.toString() + "W").indexOf(srd[3]) >= 0) : false; // yield true if (has a right neighbor tile) && (right boarder is possible to match)
    		// For joker Tile, make sure chosenField have at least 1 boarder Tile
    		boolean cond0_j = ((srd[1] != null) || (srd[2] != null) || (srd[3] != null));
    		
    		// cond0 only applies to Normal Move, 
    		boolean cond0 = (chosenBaseTile.isJoker()) ? (cond0_j) : (cond0_nj0 || cond0_nj1 || cond0_nj2);
    		// cond1 only applies to First Move.
    		boolean cond1 = !Board.isBonusField(returnVal); 
    		boolean cond2 = board.fieldIsEmpty(returnVal);
    		
    		isLegalField = (isFirstMove()) ? cond1 : (cond0 && cond2);
    		
    		if (isLegalField) {
    			chosenFieldIdx = returnVal;
    			chosenFieldFacingUp = Board.isFacingUp(chosenFieldIdx);
    		} else {
    			// later this will be sent to p via socket, and p will parse it 
    			// after parsing, will show a bunch of message to tell the rule to player
    			System.out.println(FIELD_WRONG);
    		}
    	} while (chosenFieldIdx == null);
    	
    	return chosenFieldIdx;
    }
    
    /**
     * Helper function of askTileAndRotation(Player p).
     * Generate 3 different rotation from the base Tile, ask user to choose one rotation.
     * @param p The player to ask
     * @param t The tile with rotation 0, as the base Tile
     * @return
     */
    public Tile askRotation(Player p, Tile baseT) {
    	Tile chosenRotation = null;
    	
    	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ Below are printing all possible Rotation  ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    	// ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ Later TUI should send this String representation to PlayerClient ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓
    	// Generate 3 different rotation from the base Tile,
    	ArrayList<Tile> allRotation = new ArrayList<>();

    	if (chosenFieldFacingUp) {
    		// fill 3 rotation facing up
    		allRotation.addAll( Arrays.asList(baseT, 
    										  baseT.rotateTileTwice(), 
    										  baseT.rotateTileFourTimes()
    										  )
    				);

    		showMultiTilesUp(allRotation);
    		
    	} else if (!chosenFieldFacingUp) {
    		// fill 3 rotation facing down
    		allRotation.addAll( Arrays.asList(baseT.rotateTileOnce(), 
    										  baseT.rotateTileOnce().rotateTileTwice(), 
    										  baseT.rotateTileOnce().rotateTileFourTimes()
    										  )
    				);
    		
    		showThreeTilesDown(allRotation);
    		
    	} else {
    		System.out.println("Error! chosen field direction unknow!"); // impossible to happen.
    	}
    	// ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑ Above are printing all possible Rotation ↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑
    	
    	// TUI doing the sanitary check, if not sanitary, keep asking until user give legal fieldIdx.
    	while (true) {
    		chosenRotation = allRotation.get(p.chooseRotationIdx());
    		
    		if (isFirstMove()) {
    			// First Move don't care about neighbors
    			break;
    		}
    		
    		// Normal Move Case 1: chosenBaseTile is Joker : direct pass
    		if (chosenBaseTile.isJoker()) {
    			// Normal Move: askField has make sure chosenField has at least one neighbor
    			// Then Joker don't care boarder color.
    			break;
    		}
    		
    		Character[] srd = board.getSurroundingInfo(chosenFieldIdx);
    			
			// Normal Move Case 2: chosenBaseTile is not Joker, has No Joker neighbor : (cond1 || cond2 || cond3)
        	boolean cond1 = (srd[1] != null) ? (chosenRotation.getVertical() == srd[1] ) : false; // yield true if (has a vertical neighbor tile) && (vertical boarder MATCHES)
    		boolean cond2 = (srd[2] != null) ? (chosenRotation.getLeft() == srd[2])      : false; // yield true if (has a left neighbor tile) && (left boarder MATCHES)
    		boolean cond3 = (srd[3] != null) ? (chosenRotation.getRight() == srd[3])     : false; // yield true if (has a right neighbor tile) && (right boarder MATCHES)
    		if (cond1 || cond2 || cond3) {
    			break;
    		}
    		
    		// Normal Move Case 3: chosenBaseTile is not Joker, HAS Joker neighbor
    		boolean cond4 = (srd[1] != null) ? (srd[1] == 'W') : false;
    		boolean cond5 = (srd[2] != null) ? (srd[2] == 'W') : false;
    		boolean cond6 = (srd[3] != null) ? (srd[3] == 'W') : false;
    		
        	if ( cond4 || cond5 || cond6 ) {
        		break;
        	}
    		
    	};
    	
    	return chosenRotation;
    }
 
    
    // ========================= Query Model Data =========================
	/**
	 * Return true if Player p has a tile that can be placed on board.
	 */
	public boolean canPlay(Player p) {
		// if no Tile on board, and Player has Tile, it's first move, always can play.
		if (isFirstMove()) {
			return true;
		}
		
		// if player doesn't have non null Tile, canPlay is false
		if (p.getNonNullTiles().size() == 0) {
			return false;
		}
		
		// all boarders open to be matched
		ArrayList<Character> openToMatch = new ArrayList<>();
		
		for (Integer idx : Board.hasVN) {
			// if there's a Tile on it, and it's vertical boarder color is null
			// then this Tile's vertical color is open to be matched
			if ((!board.fieldIsEmpty(idx)) && (board.getVerticalBoarderColor(idx) == null)) {
				openToMatch.add(board.getTile(idx).getVertical());
			}
		}
		
		for (Integer idx : Board.hasLN) {
			// if there's a Tile on it, and it's left boarder color is null
			// then this Tile's left color is open to be matched
			if ((!board.fieldIsEmpty(idx)) && (board.getLeftBoarderColor(idx) == null)) {
				openToMatch.add(board.getTile(idx).getLeft());
			}
		}
		
		for (Integer idx : Board.hasRN) {
			// if there's a Tile on it, and it's right boarder color is null
			// then this Tile's right color is open to be matched
			if ((!board.fieldIsEmpty(idx)) && (board.getRightBoarderColor(idx) == null)) {
				openToMatch.add(board.getTile(idx).getRight());
			}
		}
		
		ArrayList<Character> atHand = new ArrayList<>();
		for (Tile t : p.getNonNullTiles()) {
			atHand.add(t.getVertical());
			atHand.add(t.getLeft());
			atHand.add(t.getRight());
		}
		
		// only leave the intersection of atHand and openToMath in atHand 
		atHand.retainAll(openToMatch); 
		
		return atHand.size() > 0;
	}

	public boolean canPlay2(Player p) {
		// if no Tile on board, and Player has Tile, it's first move, always can play.
		if (isFirstMove()) {
			return true;
		}
		
		// if player doesn't have non null Tile, canPlay is false
		if (p.getNonNullTiles().size() == 0) {
			return false;
		}
		
		// when there's already some Tile on board, and Player has Tile in hand
		ArrayList<Integer> emptyWNT = board.getEmptyFieldsWithNeighborTile();
		// for every empty field with at least a neighboring Tile
		for (Integer idx : emptyWNT) {
			Character[] srd = board.getSurroundingInfo(idx);
			
			// for every tile in user's hand
			for (Tile t : p.getNonNullTiles()) {
				// for every tile in rotation 0, 2, 4, return true as long as you find a match
				if (srd[0] == 'U') {
					ArrayList<Tile> upRotations = new ArrayList<Tile>(
							Arrays.asList(t, t.rotateTileTwice(), t.rotateTileFourTimes()));
					for (Tile up : upRotations) {
						boolean match = boarderMatchs(srd, up);
						if (match) {
							return true;
						}
					}	
					
				}
				// for every tile in rotation 1, 3, 5, return true as long as you find a match
				else {
					ArrayList<Tile> downRotations = new ArrayList<Tile>(
							Arrays.asList(t.rotateTileOnce(), t.rotateTileOnce().rotateTileTwice(), t.rotateTileOnce().rotateTileFourTimes()));
					for (Tile down : downRotations) {
						boolean match = boarderMatchs(srd, down);
						if (match) {
							return true;
						}
					}	
				}
				
				
			}	
		}
		return false;
	}
	
	/**
	 * Given the surrounding infos of an empty field with at least one neighboring tile, and a specific Tile, 
	 * can the Tile be placed on the field?
	 * @param srds The surrounding information of the field.
	 * @param t The Tile you want to place on the empty field.
	 * @return true If the tile match the surroundings.
	 */
	/*
	 * @requires (srd[1] != null) || (srd[2] != null) || (srd[3] != null);
	 * @requires ((srd[0] == 'U') && (t.isFacingUp())) || ((srd[0] == 'D') && (!t.isFacingUp()));
	 */
	public boolean boarderMatchs(Character[] srd, Tile t) {
		// (has a vertical neighbor) && (vertical color mismatch)
		if ((srd[1] != null) && (t.getVertical() != srd[1])) {
			return false;
		}
		
		// (has a left neighbor) && (left color mismatch)
		if ((srd[2] != null) && (t.getLeft() != srd[2])) {
			return false;
		}
		
		// (has a right neighbor) && (right color mismatch)
		if ((srd[3] != null) && (t.getRight() != srd[3])) {
			return false;
		}
		
		return true;
	}
	
	public boolean bagIsEmpty() {
		for (Tile t : bag.getTiles()) {
			if (t != null) {
				return false;
			}
		}
		return true;
	}
    
	public boolean isFirstMove() {
		return board.getEmptyFields().size() == 36;
	}
	
	// ========================= Display Tiles Related =========================
    // Later this whole bunch of string will be sent to PlayerClient via socket
    // For now TUI print to TUI's console.
    public void showInfoToPlayer(Player sendToThisPlayer) {
    	// show each Player's Tile 
    	for (Player p : listPlayers) {
    		System.out.println("Player " + p.getName() + "'s Tiles: ");
    		ArrayList<Tile> nonNulls = p.getNonNullTiles();
    		// Later TUI should send this String representation to PlayerClient
    		// For now TUI print this String at TUI's console.
    		showMultiTilesUp(nonNulls);
    	}
    	
    	// show board
    	printBoardDynamic(board);
    }
    
    private void showMultiTilesUp(ArrayList<Tile> nonNullTiles) {
    	// use different showing function, based on different number of non-null tile
		int numNonNullTile = nonNullTiles.size();
		switch (numNonNullTile) { 
			case 0:
				System.out.println("Player has 4 null in Tile[].");
				break;
			case 1:
				showOneTileUp(nonNullTiles);
				break;
			case 2: 
				showTwoTilesUp(nonNullTiles);
				break;
			case 3:
				showThreeTilesUp(nonNullTiles);
				break;
			case 4:
				showFourTilesUp(nonNullTiles);
				break;
			default: 
				System.out.println("Something wrong, you shouldn't reach here.");
		}
		
		
    }
       
	private void showOneTileUp(List<Tile> tiles) {
		Tile t0 = tiles.get(0);
		
		if (t0.getRotation() % 2 != 0) {
			System.out.println("Warning! Tile facing down passed to showOneTileUp!");
		}
		
		String val0 = "" + t0.getValue();
		String lef0 = "" + t0.getLeft();
		String rig0 = "" + t0.getRight();
		String ver0 = "" + t0.getVertical();
		
		String template =
                "    / \\    \n" +
                "   / " + val0 + " \\   \n" +
                "  / " + lef0 + " " + rig0 + " \\  \n" +
                " /   " + ver0 + "   \\ \n" +
                " --------- \n"  ;
		
		System.out.println(template);
	}
	
	private void showTwoTilesUp(List<Tile> tiles) {
		Tile t0 = tiles.get(0);
		Tile t1 = tiles.get(1);
		
		if ((t0.getRotation() % 2 != 0) || (t1.getRotation() % 2 != 0)){
			System.out.println("Warning! Tile(s) facing down passed to showTwoTilesUp!");
		}
		
		String val0 = "" + t0.getValue();		String val1 = "" + t1.getValue();
		String lef0 = "" + t0.getLeft();		String lef1 = "" + t1.getLeft();
		String rig0 = "" + t0.getRight();		String rig1 = "" + t1.getRight();
		String ver0 = "" + t0.getVertical();	String ver1 = "" + t1.getVertical();
		
		String template2 =
                "    / \\    " + "    / \\    \n" +
                "   / " + val0 + " \\   " +"   / " + val1 + " \\   \n" +
                "  / " + lef0 + " " + rig0 + " \\  " + "  / " + lef1 + " " + rig1 + " \\  \n" +
                " /   " + ver0 + "   \\ " + " /   " + ver1 + "   \\ \n" +
                " --------- " + " --------- \n"  ;
		System.out.println(template2);
		
	}
	
	private void showThreeTilesUp(List<Tile> tiles) {
		Tile t0 = tiles.get(0);
		Tile t1 = tiles.get(1);
		Tile t2 = tiles.get(2);
		
		if ((t0.getRotation() % 2 != 0) || (t1.getRotation() % 2 != 0) || (t2.getRotation() % 2 != 0)) {
			System.out.println("Warning! Tile(s) facing down passed to showThreeTilesUp!");
		}
		
		String val0 = "" + t0.getValue();		String val1 = "" + t1.getValue();
		String lef0 = "" + t0.getLeft();		String lef1 = "" + t1.getLeft();
		String rig0 = "" + t0.getRight();		String rig1 = "" + t1.getRight();
		String ver0 = "" + t0.getVertical();	String ver1 = "" + t1.getVertical();
		
		String val2 = "" + t2.getValue();
		String lef2 = "" + t2.getLeft();
		String rig2 = "" + t2.getRight();
		String ver2 = "" + t2.getVertical();
		
		String template3 =
				// make sure each line is of the same total length
				// for GameTUI to display 3 tiles horizontally
                "    / \\    " + "    / \\    " + "    / \\    \n" +
                "   / " + val0 + " \\   " +"   / " + val1 + " \\   " + "   / " + val2 + " \\   \n" +
                "  / " + lef0 + " " + rig0 + " \\  " + "  / " + lef1 + " " + rig1 + " \\  " + "  / " + lef2 + " " + rig2 + " \\  \n" +
                " /   " + ver0 + "   \\ " + " /   " + ver1 + "   \\ " + " /   " + ver2 + "   \\ \n" +
                " --------- " + " --------- " + " --------- \n"  ;
		System.out.println(template3);
		
	}
	
	private void showFourTilesUp(List<Tile> tiles) {
		Tile t0 = tiles.get(0);
		Tile t1 = tiles.get(1);
		Tile t2 = tiles.get(2);
		Tile t3 = tiles.get(3);
		
		if ((t0.getRotation() % 2 != 0) || (t1.getRotation() % 2 != 0) || (t2.getRotation() % 2 != 0) || (t3.getRotation() % 2 != 0)){
			System.out.println("Warning! Tile(s) facing down passed to showFourTilesUp!");
		}
		
		String val0 = "" + t0.getValue();		String val1 = "" + t1.getValue();
		String lef0 = "" + t0.getLeft();		String lef1 = "" + t1.getLeft();
		String rig0 = "" + t0.getRight();		String rig1 = "" + t1.getRight();
		String ver0 = "" + t0.getVertical();	String ver1 = "" + t1.getVertical();
		
		String val2 = "" + t2.getValue();		String val3 = "" + t3.getValue();
		String lef2 = "" + t2.getLeft();		String lef3 = "" + t3.getLeft();
		String rig2 = "" + t2.getRight();		String rig3 = "" + t3.getRight();
		String ver2 = "" + t2.getVertical();	String ver3 = "" + t3.getVertical();
		
		String template4 =
                "    / \\    " + "    / \\    " + "    / \\    " + "    / \\    \n" +
                "   / " + val0 + " \\   " +"   / " + val1 + " \\   " + "   / " + val2 + " \\   " + "   / " + val3 + " \\   \n" +
                "  / " + lef0 + " " + rig0 + " \\  " + "  / " + lef1 + " " + rig1 + " \\  " + "  / " + lef2 + " " + rig2 + " \\  " + "  / " + lef3 + " " + rig3 + " \\  \n" +
                " /   " + ver0 + "   \\ " + " /   " + ver1 + "   \\ " + " /   " + ver2 + "   \\ " + " /   " + ver3 + "   \\ \n" +
                " --------- " + " --------- " + " --------- " + " --------- \n";
		System.out.println(template4);
	}
	
	private void showThreeTilesDown(List<Tile> tiles) {
		Tile t0 = tiles.get(0);
		Tile t1 = tiles.get(1);
		Tile t2 = tiles.get(2);
		
		if ((t0.getRotation() % 2 == 0) || (t1.getRotation() % 2 == 0) || (t2.getRotation() % 2 == 0)){
			System.out.println("Warning! Tile(s) facing up passed to showThreeTilesDown!");
		}
		
		String val0 = "" + t0.getValue();		String val1 = "" + t1.getValue();
		String lef0 = "" + t0.getLeft();		String lef1 = "" + t1.getLeft();
		String rig0 = "" + t0.getRight();		String rig1 = "" + t1.getRight();
		String ver0 = "" + t0.getVertical();	String ver1 = "" + t1.getVertical();
		
		String val2 = "" + t2.getValue();
		String lef2 = "" + t2.getLeft();
		String rig2 = "" + t2.getRight();
		String ver2 = "" + t2.getVertical();
		String template = 
				// make sure each line is of the same total length
				// for GameTUI to display 3 tiles horizontally
			     " --------- " + " --------- " + " --------- \n" +
			     " \\   " + ver0 + "   / " + " \\   " + ver1 + "   / " + " \\   " + ver2 + "   / \n" +
                 "  \\ " + lef0 + " " + rig0 + " /  " + "  \\ " + lef1 + " " + rig1 + " /  " + "  \\ " + lef2 + " " + rig2 + " /  \n" +
                 "   \\ " + val0 + " /   " + "   \\ " + val1 + " /   " + "   \\ " + val2 + " /   \n" +
			     "    \\ /    " + "    \\ /    " + "    \\ /    \n" ; 
		System.out.print(template);
	}

    // ========================= Print the board =========================
    public void printBoardDynamic(Board b) {
    	System.out.println(getBoardString(b.getValuesOnBoard(), b.getVerticalOnBoard(), b.getLeftOnBoard(), b.getRightOnBoard()));
    }

    public void printBoardStatic() {
    	System.out.println(getBoardString(values, vertical, left, right));
    }
    
    /* if a field is empty         =>      its index showed, its bonus (if not 1) showed
     * if a field has a tile on it =>      its index hidden, its bonus (if not 1) showed,
     *  								   its value showed.
     */
    public String getBoardString(List<Integer> values, List<Character> vertical, List<Character> left, List<Character> right){
        // All lists should have exactly 36 items.
        if(!Stream.of(values, vertical, left, right).parallel().map(List::size).allMatch(n -> n == 36)){
            throw new IllegalArgumentException("Input lists should all have 36 items, one for each field on the board.");
        }
        String template = "\n" +
                "                               ^\n" +
                "                              / \\\n" +
                "                             / {f0b} \\\n" +
                "                            /{f00}{f0v} {f01}\\\n" +
                "                           /   {f02}   \\\n" +
                "                          /---------\\\n" +
                "                         / \\   {f22}   / \\\n" +
                "                        / {f1b} \\{f20}{f2v} {f21}/ {f3b} \\\n" +
                "                       /{f10}{f1v} {f11}\\ {f2b} /{f30}{f3v} {f31}\\\n" +
                "                      /   {f12}   \\ /   {f32}   \\\n" +
                "                     /---------X---------\\\n" +
                "                    / \\   {f52}   / \\   {f72}   / \\\n" +
                "                   / {f4b} \\{f50}{f5v} {f51}/ {f6b} \\{f70}{f7v} {f71}/ {f8b} \\\n" +
                "                  /{f40}{f4v} {f41}\\ {f5b} /{f60}{f6v} {f61}\\ {f7b} /{f80}{f8v} {f81}\\\n" +
                "                 /   {f42}   \\ /   {f62}   \\ /   {f82}   \\\n" +
                "                /---------X---------X---------\\\n" +
                "               / \\   {f102}   / \\   {f122}   / \\   {f142}   / \\\n" +
                "              / {f9b} \\{f100}{f10v} {f101}/ {f11b} \\{f120}{f12v} {f121}/ {f13b} \\{f140}{f14v} {f141}/ {f15b} \\\n" +
                "             /{f90}{f9v} {f91}\\ {f10b} /{f110}{f11v} {f111}\\ {f12b} /{f130}{f13v} {f131}\\ {f14b} /{f150}{f15v} {f151}\\\n" +
                "            /   {f92}   \\ /   {f112}   \\ /   {f132}   \\ /   {f152}   \\\n" +
                "           /---------X---------X---------X---------\\\n" +
                "          / \\   {f172}   / \\   {f192}   / \\   {f212}   / \\   {f232}   / \\\n" +
                "         / {f16b} \\{f170}{f17v} {f171}/ {f18b} \\{f190}{f19v} {f191}/ {f20b} \\{f210}{f21v} {f211}/ {f22b} \\{f230}{f23v} {f231}/ {f24b} \\\n" +
                "        /{f160}{f16v} {f161}\\ {f17b} /{f180}{f18v} {f181}\\ {f19b} /{f200}{f20v} {f201}\\ {f21b} /{f220}{f22v} {f221}\\ {f23b} /{f240}{f24v} {f241}\\\n" +
                "       /   {f162}   \\ /   {f182}   \\ /   {f202}   \\ /   {f222}   \\ /   {f242}   \\\n" +
                "      /---------X---------X---------X---------X---------\\\n" +
                "     / \\   {f262}   / \\   {f282}   / \\   {f302}   / \\   {f322}   / \\   {f342}   / \\\n" +
                "    / {f25b} \\{f260}{f26v} {f261}/ {f27b} \\{f280}{f28v} {f281}/ {f29b} \\{f300}{f30v} {f301}/ {f31b} \\{f320}{f32v} {f321}/ {f33b} \\{f340}{f34v} {f341}/ {f35b} \\\n" +
                "   /{f250}{f25v} {f251}\\ {f26b} /{f270}{f27v} {f271}\\ {f28b} /{f290}{f29v} {f291}\\ {f30b} /{f310}{f31v} {f311}\\ {f32b} /{f330}{f33v} {f331}\\ {f34b} /{f350}{f35v} {f351}\\\n" +
                "  /   {f252}   \\ /   {f272}   \\ /   {f292}   \\ /   {f312}   \\ /   {f332}   \\ /   {f352}   \\\n" +
                " /-----------------------------------------------------------\\\n";

        // Fill in bonus values
        template = listToMap(bonuses).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "b}", elem.getValue() != 1 ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        // Fill in values
        template = listToMap(values).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "v}", elem.getValue() != null ? String.format("%2d", elem.getValue()) : String.format("%2d", elem.getKey())), (s, s2) -> s);

        // Fill in left colors
        template = listToMap(left).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "0}", elem.getValue() != null ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        // Fill in right colors
        template = listToMap(right).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "1}", elem.getValue() != null ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        // Fill in vertical colors
        template = listToMap(vertical).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "2}", elem.getValue() != null ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        return template;
    }

    private <K> Map<Integer, K> listToMap(List<K> inputList){
        Map<Integer, K> indexed_values = new HashMap<>();
        for(int i = 0; i < values.size(); i++){ indexed_values.put(i, inputList.get(i)); }
        return indexed_values;
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
