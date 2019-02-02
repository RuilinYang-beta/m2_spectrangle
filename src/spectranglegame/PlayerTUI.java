package spectranglegame;

import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;

import java.net.Socket;

import players.*;

// This is a PlayerClient
public class PlayerTUI extends Thread{
	
	// =========== Signals: later will be changed to better cope protocol ===========
    private static final String FIELD = "Field?";
    private static final String FIELD_WRONG = "Wrong Field";
    private static final String TILE = "Tile?";
    private static final String ROTATION = "Rotation?";
    
    // =========== Instance Field ===========
    // ---------- old ones ---------- 
	private GameControl control;
	
	private Bag bag;
	private Tile chosenBaseTile = null;
	private Integer chosenFieldIdx = null;
	private Boolean chosenFieldFacingUp = null;

	
	// ---------- new ones ---------- 
	private String myName;
	private Socket csk;
	private int numOfPlayer;
	
	private Board board;
	private Player me;
	private List<Player> listPlayers; 
	
	
	// =========== Constructor ===========
	public PlayerTUI(GameControl gc, Board bd, List<Player> lp, Bag bg) {
		this.control = gc;
		this.board = bd;
		this.listPlayers = lp;
		this.bag = bg;
	}

	// only for test purpose
	public PlayerTUI(Board b) {
		this.board = b;
	}
	
	
	public PlayerTUI(Socket s, String nm, Integer numP) {
		// the specific info about this user
		this.myName = nm;
		this.csk = s;
		this.numOfPlayer = numP;
	
		// the local shadow of model
		this.board = new Board();
		this.listPlayers = new ArrayList<Player>(); // other player's name unknown yet
//		this.me = new HumanPlayer(nm);
	}
	
	// ========================= Handle Commands =========================
	// when server sends some signal, it will be handled by calling other functions 
	public void run() {
		System.out.println("You are in PlayerTUI.run()");
	}
	
	// ========================= Prompt User Input =========================
	// triggered when (S -> C: Turn <username>) && (username.equals(this.name)
//	public void promptChoice() {
	public String promptChoice(Player p) {
		System.out.println("Player " + p.getName() + ", it's your turn.");
		
		Display.showInfoToPlayer(control.getListPlayers(), control.getBoard());
		
//		Map<String, Set<Integer>> allMove = Sanitary.generateAllPossibleMoves(board, me);
		Map<String, Set<Integer>> allMove = Sanitary.generateAllPossibleMoves(control.getBoard(), p);
		
		ArrayList<Tile> nonNulls = p.getNonNullTiles();
		Display.showMultiTilesUp(nonNulls);
		
		if (allMove.size() == 0) {
			
			// it's first move
			if (control.getBoard().getEmptyFields().size() == 36) {
				
				int idx = p.chooseTileIdx(nonNulls.size());
				Tile theTile = nonNulls.get(idx);
				
				Integer theField = p.chooseFieldIdx(true);
				
				Tile theFinalTile = askRotation(p, theField, theTile);
				
				return "Move" + " " + theField + " " + theFinalTile.stringTile();
				
			}
			
			// user don't have a matching Tile && Bag is not empty
			if (control.getBoard().getEmptyFields().size() + getNumTilesAtPlayers() < 36) {
				// user have the choice to skip or swap
	    		Integer tileIdxToSwap = p.chooseSkipOrSwap(nonNulls.size());
	    		
	    		if (tileIdxToSwap == null) {
	    			// C -> S: Skip
	    			return "Skip";
	    		} else {
					Tile fromPlayer = nonNulls.get(tileIdxToSwap);
					// C -> S: Skip <encoding of fromPlayer>
					return "Skip " + fromPlayer.stringTile();
				}
	    		
			}
			
			// user don't have a matching Tile && Bag is empty
			if (control.getBoard().getEmptyFields().size() + getNumTilesAtPlayers() == 36) {
				p.toSkip();
				return "Skip";
			}
			
			else {
				
				System.out.println("You shouldn't fall here");
				return "";
			}
			
		}
		// not the first move && user has at least a matching tile
		else {
			Tile theTile = null;
			while (true) {
				int idx = p.chooseTileIdx(nonNulls.size());
				theTile = nonNulls.get(idx);
				
				if (Sanitary.optionsForBaseTile(allMove, theTile).size() > 0) { break; } 
				else { System.out.println("- I tell you what, this tile is by no rotation eligible. Choose another.");}
			}
			
			Integer theField = null;
			while (true) {
				theField = p.chooseFieldIdx(false);
				
				if (Sanitary.optionsForBaseTileAndField(allMove, theTile, theField).size() > 0) {break;}
				else { System.out.println("- Too young too simple, field not possible. Choose another.");}	
			}
			
			Tile theFinalTile = null;
			while (true) {
				theFinalTile = askRotation(p, theField, theTile);
				
				if ( (allMove.get(theFinalTile.stringTile()) != null) && (allMove.get(theFinalTile.stringTile()).contains(theField))) {
					break;
				} else {
					System.out.println("- Rotation wrong, use your brain. ");
				}
			}
			
			return "Move" + " " + theField + " " + theFinalTile.stringTile();
			
			
		}
		
	}
	
	
	
	
	// ========================= Keep the User Informed =========================
	
	
	// triggered when (S -> C: Turn <username>) && (!username.equals(this.name)
	public void othersTurn() {
		System.out.println("Player " + "" + "'s turn.");
	}
	
	
	// ========================= Other Helper =========================
	public int getNumTilesAtPlayers() {
		int sum = 0;
//		for (Player p : listPlayers) {
		for (Player p: control.getListPlayers()) {
			sum += p.getNonNullTiles().size();
		}
		
		
		return sum;
	}
	
	
	
	// ========================= Old Ones =========================
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
		Display.showMultiTilesUp(nonNulls);
    		
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
    public Tile askRotation(Player p, int idx, Tile baseT) {
    	Tile chosenRotation = null;
    	boolean isFacingUp = Board.isFacingUp(idx);

    	ArrayList<Tile> allRotation = new ArrayList<>();

    	if (isFacingUp) {
    		// fill 3 rotation facing up
    		allRotation.addAll( Arrays.asList(baseT, 
    										  baseT.rotateTileTwice(), 
    										  baseT.rotateTileFourTimes()
    										  )
    				);

    		Display.showMultiTilesUp(allRotation);
    		
    	} else {
    		// fill 3 rotation facing down
    		allRotation.addAll( Arrays.asList(baseT.rotateTileOnce(), 
    										  baseT.rotateTileOnce().rotateTileTwice(), 
    										  baseT.rotateTileOnce().rotateTileFourTimes()
    										  )
    				);
    		
    		Display.showThreeTilesDown(allRotation);
    		
    	} 
    	
    	chosenRotation = allRotation.get(p.chooseRotationIdx());
    	
    	// TUI doing the sanitary check, if not sanitary, keep asking until user give legal fieldIdx.
//    	while (true) {
//    		chosenRotation = allRotation.get(p.chooseRotationIdx());
//    		
//    		if (isFirstMove()) {
//    			// First Move don't care about neighbors
//    			break;
//    		}
//    		
//    		// Normal Move Case 1: chosenBaseTile is Joker : direct pass
//    		if (chosenBaseTile.isJoker()) {
//    			// Normal Move: askField has make sure chosenField has at least one neighbor
//    			// Then Joker don't care boarder color.
//    			break;
//    		}
//    		
//    		Character[] srd = board.getSurroundingInfo(chosenFieldIdx);
//    			
//			// Normal Move Case 2: chosenBaseTile is not Joker, has No Joker neighbor : (cond1 || cond2 || cond3)
//        	boolean cond1 = (srd[1] != null) ? (chosenRotation.getVertical() == srd[1] ) : false; // yield true if (has a vertical neighbor tile) && (vertical boarder MATCHES)
//    		boolean cond2 = (srd[2] != null) ? (chosenRotation.getLeft() == srd[2])      : false; // yield true if (has a left neighbor tile) && (left boarder MATCHES)
//    		boolean cond3 = (srd[3] != null) ? (chosenRotation.getRight() == srd[3])     : false; // yield true if (has a right neighbor tile) && (right boarder MATCHES)
//    		if (cond1 || cond2 || cond3) {
//    			break;
//    		}
//    		
//    		// Normal Move Case 3: chosenBaseTile is not Joker, HAS Joker neighbor
//    		boolean cond4 = (srd[1] != null) ? (srd[1] == 'W') : false;
//    		boolean cond5 = (srd[2] != null) ? (srd[2] == 'W') : false;
//    		boolean cond6 = (srd[3] != null) ? (srd[3] == 'W') : false;
//    		
//        	if ( cond4 || cond5 || cond6 ) {
//        		break;
//        	}
//    		
//    	};
    	
    	return chosenRotation;
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

    public boolean canPlay(Player p) {
		// if no Tile on board, and Player has Tile, it's first move, always can play.
		if (isFirstMove()) {
			return true;
		}
		
		ArrayList<Tile> tilesAtHand = p.getNonNullTiles();
		
		// if player doesn't have non null Tile, canPlay is false
		if (tilesAtHand.size() == 0) {
			return false;
		}
		
		// if player has a Joker, can play
		for (Tile t : tilesAtHand) {
			if (t.isJoker()) {
				return true;
			}
		}
		
		// when there's already some Tile on board, and Player has Tile in hand but don't have Joker
		ArrayList<Integer> emptyWNT = board.getEmptyFieldsWithNeighborTile();
		// for every empty field with at least a neighboring Tile
		for (Integer idx : emptyWNT) {
			Character[] srd = board.getSurroundingInfo(idx);
			
			// for every tile in user's hand
			for (Tile t : tilesAtHand) {
				if (boarderPossibleToMatch(srd, t)) {
					return true;
				}	
			}	
		}
		return false;
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
