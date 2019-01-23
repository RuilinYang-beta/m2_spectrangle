package spectranglegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.*;

import players.HumanPlayer;
import players.Player;

public class GameControl {
	
	// ======================== Fields ========================
	private Board board;
	private GameTUI tui;
	
	private Bag bag;
	private List<Tile> tilesCopy;  // for safety, don't want to change a collection when looping through it
	private int firstNonNullIdx;   // for safety, auxiliary to tilesCopy
	
	private final int numPlayers;
	private List<Player> listPlayers; // to have a order in players
	private Integer firstPlayerIdx;   // not determined until dealTiles
	private Map<Player, Tile[]> mapPlayers; // to store player and its tiles
	
	
	// ======================== Constructor ========================
	public GameControl(List<Player> lp, boolean shuffle) {
		this.board = new Board();
		this.tui = new GameTUI(this);
		
		this.bag = new Bag(shuffle);
		// a clone of bag.getTiles()
		this.tilesCopy = new ArrayList<>(bag.getTiles());
		firstNonNullIdx = 0;
		
		this.numPlayers = lp.size();
		this.listPlayers = lp;
		firstPlayerIdx = null;
		this.mapPlayers = new HashMap<>();
	}
	
	// ============ Preparation: deal the initial tiles ============
	/**
	 * Deal tiles so that each user has 4 Tiles, stored in mapPlayers.
	 * Also return the index of the player to make the first move.
	 * @return The index of player that should take the first move.
	 *         Return null if this index is still undetermined.
	 *         (which is very unlikely for shuffled bag)
	 */
	public Integer dealTiles() {
		// initiate each player with an empty array
		// that can hold at most 4 Tiles object
		for (Player p : this.listPlayers) {
			Tile[] tiles = new Tile[4];
			mapPlayers.put(p, tiles);
		}
		
		// for each of the 4 slot in Tile[4]
		for (int j = 0; j < 4; j++) {
			// give each user one tile and remove the tile from the tilesCopy
			for (int i = 0; i < numPlayers; i++) {
				mapPlayers.get(listPlayers.get(i))[j] = this.bag.getTiles().get(j * numPlayers + i);
				this.tilesCopy.set(j * numPlayers + i, null); 
				firstNonNullIdx++;
			} 
		
			// if the player to make the first move is undetermined
			// try to determine
			if (firstPlayerIdx == null) {
				// get a list of total values at hand of each player
				// and collect the results in an List
				List<Integer> valAtHand = listPlayers.
											stream().
											map(p -> getValuesAtHand(p)).
											collect(Collectors.toList());
				
				// if there's a unique max value, 
				// the user to make the first move is determined
				if (hasUniqueMax(valAtHand)) {
					firstPlayerIdx = valAtHand.indexOf(Collections.max(valAtHand));
				}
			}
		}	
		return firstPlayerIdx;
	}
	
	/**
	 * Helper function of public int dealCards().
	 * @param p A Player object in listPlayers.
	 * @return An integer indicating the total value of the tiles 
	 *         at the hand of this specific player.
	 */
	private Integer getValuesAtHand(Player p) {
		Integer sum = 0;
		for (Tile t : mapPlayers.get(p)) {
			if (t != null) {
				sum += t.getValue();
			}
		}
		return sum;
	}
	
	/**
	 * Helper function of public int dealCards().
	 * @param values A list of integers indicating the total values at 
	 *               at the hand of each player.
	 * @return true if there's an unique max value.
	 */
	private boolean hasUniqueMax(List<Integer> values) {
		List<Integer> valuesCopy = new ArrayList<>(values);
		
		Integer maxVal = Collections.max(valuesCopy);
		Integer idx1 = valuesCopy.indexOf(maxVal);
		Collections.reverse(valuesCopy);
		Integer idx2 = valuesCopy.indexOf(maxVal);
		// should be true if idx2 is pointing to the same element 
		// as idx1 do, only in a reverse ordered list
		return (idx1 + idx2) == (valuesCopy.size() - 1);
	}
	
	// ==================== Gaming: make a move ====================
	// ----------------- FirstMove and its sanitaryCheck -----------------
	/**
	 * Let the first user make the first move.
	 */
	public void makeFirstMove() {
		// if when each user has 4 tiles but still duplicate max (very unlikely but still possible)
		// let the first user make the first move 
		if (firstPlayerIdx == null) {
			firstPlayerIdx = 0;
		}
		
		Player firstPlayer = listPlayers.get(firstPlayerIdx);
		
		int theField = tui.askField(firstPlayer);
		Tile theTile = tui.askRotation(firstPlayer);
		
		// if userChoice is legal
		if (sanitaryCheckFirstMove(theField, theTile)) {
			// 1. place the chosen rotation of chosen tile on the chosen field
			putTileOnBoard(theField, theTile);
//			mapPlayers.get(firstPlayer)[userChoice[1]] = null;        // analogous to put off one tile at hand
			// 2. draw another tile to restore to 4 tiles in hand
//			mapPlayers.get(firstPlayer)[userChoice[1]] = drawATile(); // analogous draw a tile to fill the hole
		} else {
			System.out.println("Illegal first move, please try again.");
			// need a mechanism to make user try again.
		}
		
	}
	
	/**
	 * A helper function of makeFirstMove().
	 * @param choices An array of length 3: 
	 * 				  [idxFieldOfChoice, idxOfTilesAtHand, rotationOfTile]
	 * @return true if choice is a legal move by all means.
	 */
	private boolean sanitaryCheckFirstMove(int fieldIdx, Tile chosenTile) {
		// not a bonus field
		boolean cond1 = !Board.isBonusField(fieldIdx);
		// direction of field match rotation of tile
		boolean cond2 = Board.isFacingUp(fieldIdx) == chosenTile.isFacingUp();
		
		return cond1 && cond2;
	}
	
	// ----------------- NormalMove and its sanitaryCheck -----------------
//	private boolean sanitaryCheck() {
//		
//	}
//	
//	private boolean sanitaryField() {
//		return false;
//	}
//	
//	private boolean sanitaryRotation() {
//		return false;
//	}
//	
//	private boolean sanitaryTile() {
//		return false;
//	}
//	
//	public void makeNormalMove() {
//		
//	}

	
	// ================== Gaming: other common functionalities ==================
	// ------------------ that both FirstMove and NormalMove can use ------------------
	/**
	 * Safety measurement before every new game.
	 */
	public void resetBoard() {
		board.resetBoard();
	}
	
	/**
	 * Get a tile from the bag, and nullify the corresponding tile in the tilesCopy, 
	 * and update firstNonNullIdx.
	 * @return The tile being drew from the bag.
	 */
	public Tile drawATile() {
		Tile t = bag.getTiles().get(firstNonNullIdx);
		tilesCopy.set(firstNonNullIdx, null);
		firstNonNullIdx++;
		return t;
	}
	
	
	/**
	 * [to be tested] Show all tiles at one player's hand, it should accommodate the situation
	 * that the player has 4 tiles, or less than 4 tiles.
	 * The latter can happen when the bag is empty, no more tile to draw to restore to 4.
	 */
	public void showtiles() {
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < mapPlayers.values().size(); i++) {
					String template =                     " ʌ\n" +
			                "                              / \\\n" +
			                "                             /  \\\n" +
			                "                            /"+ mapPlayers.get(listPlayers.get(i))[j].getLeft() + "  " + mapPlayers.get(listPlayers.get(i))[j].getValue() + "  " + mapPlayers.get(listPlayers.get(i))[j].getLeft() +"\\\n" +
			                "                           /   " + mapPlayers.get(listPlayers.get(i))[j].getVertical() +"   \\\n" +
			                "                          /---------\\\n" ;
			System.out.print(template + "  ");
			}
		}
		
	}

	/**
	 * Get data from board, ask tui to print them.
	 */
	public void printBoard() {
		tui.printBoardDynamic(board.getValuesOnBoard(), 
							  board.getVerticalOnBoard(), 
							  board.getLeftOnBoard(),
							  board.getRightOnBoard());
	}
	
	public void putTileOnBoard(int idx, Tile t) {
		board.setTile(idx, t);
	}
	
	// ======================== Queries ========================

	/**
	 * A getter of mapPlayers.
	 */
	public Map<Player, Tile[]> getMapPlayers() {
		return mapPlayers; 
	}

	/**
	 * A getter of bag.
	 */
	public Bag getBag() {
		return bag;
	}

	/**
	 * A getter of tilesCopy.
	 * @return
	 */
	public List<Tile> getTilesCopy() {
		return tilesCopy;
	}

	/**
	 * A getter of this.bag.getTiles(); 
	 * Don't change it after getting it!
	 * @return
	 */
	public List<Tile> getTiles(){
		return bag.getTiles();
	}
	
	// ======================== Main ========================
	public static void main(String[] args) {
		
		GameControl shuffled3P = new GameControl( Arrays.asList(new HumanPlayer("A"),
				 new HumanPlayer("B"),
				 new HumanPlayer("C")), 
  true);
		
		shuffled3P.printBoard(); // empty board
		
		Tile t = new Tile(3, "RGB");
		
		// test rotateTileOnce
		shuffled3P.putTileOnBoard(25, t);
		shuffled3P.printBoard();  // idx 25 filled with rotation 0 of the tile
		
		shuffled3P.putTileOnBoard(27, t.rotateTileOnce().rotateTileOnce());
		shuffled3P.printBoard(); // additionally, idx 27 filled with rotation 2 of the tile
		
		shuffled3P.putTileOnBoard(29, t.rotateTileOnce().rotateTileOnce().rotateTileOnce().rotateTileOnce());
		shuffled3P.printBoard(); // additionally, idx 27 filled with rotation 4 of the tile
		
//		System.out.println(t.rotateTileOnce().rotateTileOnce().rotation);
		
		// test rotateTileTwice
		shuffled3P.putTileOnBoard(31, t);
		shuffled3P.printBoard();  // idx 31 filled with rotation 0 of the tile
		
		shuffled3P.putTileOnBoard(33, t.rotateTileTwice());
		shuffled3P.printBoard(); // additionally, idx 33 filled with rotation 2 of the tile
		
		shuffled3P.putTileOnBoard(35, t.rotateTileTwice().rotateTileTwice());
		shuffled3P.printBoard(); // additionally, idx 35 filled with rotation 4 of the tile
		
		// the appearence of 25 and 31, 27 and 33, 29 and 35 should be identical
	}

}
