package spectranglegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.stream.*;
import java.util.Random;

import players.*;

public class GameControl {
	
	// ======================== Fields ========================
	private Board board;
	private GameTUI tui;
	
	private Bag bag;
	private int firstNonNullIdx;   // auxiliary to bag.getTiles
	
	
	private List<Player> listPlayers; // to have a order in players
	private final int numPlayers;     // auxiliary to listPlayers
	private Integer firstPlayerIdx;   // auxiliary to listPlayers, not determined until dealTiles
	private Integer currentPlayerIdx; // auxiliary to listPlayers
	
	// Map<Player, Tile[]> mapPlayers will be deprecated
//	private Map<Player, Tile[]> mapPlayers; // to store player and its tiles
	
	
	// ======================== Constructor ========================
	public GameControl(List<Player> lp, boolean shuffle) {
		this.board = new Board();
		
		this.bag = new Bag(shuffle);
		firstNonNullIdx = 0;
		
		this.numPlayers = lp.size();
		this.listPlayers = lp;
		firstPlayerIdx = null;
		
		this.tui = new GameTUI(this, this.board, lp, this.bag);
	}

	
	// only for test purpose
	public GameControl(List<Player> lp, boolean shuffle, Board bd) {
		this.board = bd;
		
		this.bag = new Bag(shuffle);
		firstNonNullIdx = 0;
		
		this.numPlayers = lp.size();
		this.listPlayers = lp;
		firstPlayerIdx = null;
		
		this.tui = new GameTUI(this, this.board, lp, this.bag);
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
		// this is achieved by newly initialized Player
//		for (Player p : this.listPlayers) {
//			Tile[] tiles = new Tile[4];
//			mapPlayers.put(p, tiles);
//		}
		
		// for each of the 4 slot in Tile[4]
		for (int j = 0; j < 4; j++) {
			// give each user one tile and remove the tile from the bag
			for (int i = 0; i < numPlayers; i++) {
//				mapPlayers.get(listPlayers.get(i))[j] = this.bag.getTiles().get(j * numPlayers + i);
//				bag.getTiles().set(j * numPlayers + i, null); 
//				firstNonNullIdx++;
				dealATileToPlayer(listPlayers.get(i));
//				mapPlayers.get(listPlayers.get(i))[j] = drawATile();
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
		Tile[] tilesAtHand = p.getTiles();
		for (Tile t : tilesAtHand) {
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
	// ----------------- FirstMove -----------------
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
		
		tui.showInfoToPlayer(firstPlayer);
//		Tile theBaseTile = tui.askTile(firstPlayer, true);
		Tile theBaseTile = tui.askTile(firstPlayer);
		
		int theField = tui.askField(firstPlayer, board);
		Tile theTile = tui.askRotation(firstPlayer, theBaseTile);
		
		// 1. place the chosen rotation of chosen tile on the chosen field
		putTileOnBoard(theField, theTile);
		// 2. nullify the chosen Tile at Player's hand
		nullifyChosenTile(firstPlayer, theTile);
		// 3. deal one tile to player, to restore to 4 tiles in hand
		dealATileToPlayer(firstPlayer);

		currentPlayerIdx = (firstPlayerIdx + 1) % numPlayers;
	}
	
	// ----------------- NormalMove -----------------
	public void makeNormalMove() {
		Player currentPlayer = listPlayers.get(currentPlayerIdx);
		
		tui.showInfoToPlayer(currentPlayer);
		
		Tile theBaseTile = tui.askTile(currentPlayer);
		
		if (theBaseTile != null) { // that is, user did chose a Tile
			int theField = tui.askField(currentPlayer, board);
			Tile theTile = tui.askRotation(currentPlayer, theBaseTile);
			
			// 1. place the chosen rotation of chosen tile on the chosen field
			putTileOnBoard(theField, theTile);
			// 2. nullify the chosen Tile at Player's hand
			nullifyChosenTile(currentPlayer, theTile);
			// 3. deal one tile to player, to restore to 4 tiles in hand
			dealATileToPlayer(currentPlayer);
		}
		
		currentPlayerIdx = (currentPlayerIdx + 1) % numPlayers;
	}
		
	
	// ================== Gaming: other common functionalities ==================
	// ------------------ that both FirstMove and NormalMove can use ------------------
	public void putTileOnBoard(int idx, Tile t) {
		board.setTile(idx, t);
	}
	
	private void nullifyChosenTile(Player p, Tile chosenTile) {
		for (int i = 0; i < 4; i++) {
			Tile t = p.getTiles()[i];
			// you don't want to call toString on a null object.
			if (t != null) {
			
				if (t.toString().equals(chosenTile.toString())) {
					p.getTiles()[i] = null;
				}
			}
			
		}
	}
	
	/**
	 * Draw the first non-null Tile, and set its position as null, and update firstNonNullIdx.
	 * @return The tile being drew from the bag.
	 */
	public Tile drawATile() {
		if (firstNonNullIdx < 36) {
			Tile t = bag.getTiles().get(firstNonNullIdx);
			bag.getTiles().set(firstNonNullIdx, null);
			firstNonNullIdx++;
			return t;
		} else {
			return null;
		}
		
	}
	
	public void dealATileToPlayer(Player p) {
		Tile t = drawATile();
		p.takeTheTile(t);
	}
	
	/** 
	 * Choose a random non-null Tile in bag, replace it with the Tile t, 
	 * and return the replaced Tile.
	 */
	public Tile swapRandomTileInBag(Tile t) {
		Random r = new Random();
		// generate a random int within [firstNonNullIdx, 35] (inclusive)
		int randNonNullIdx = r.nextInt(36 - firstNonNullIdx) + firstNonNullIdx;
		Tile getFromBag = bag.getTile(randNonNullIdx).deepCopy();
		bag.getTiles().set(randNonNullIdx, t);
		return getFromBag;
	}
	
	/**
	 * Get data from board, ask tui to print them.
	 */
	public void printBoard() {
		tui.printBoardDynamic(board);
	}

	/**
	 * Safety measurement before every new game.
	 */
	public void resetBoard() {
		board.resetBoard();
	}
	
	
	// ======================== Queries ========================

	public List<Player> getListPlayers(){
		return this.listPlayers;
	}

	/**
	 * A getter of this.bag.getTiles(); 
	 * Don't change it after getting it!
	 * @return
	 */
	public List<Tile> getTiles(){
		return bag.getTiles();
	}
	
	public int getFirstNonNullIdx() {
		return this.firstNonNullIdx;
	}
	
	/**
	 * Return true if Player p has a tile that can be placed on board.
	 */
//	public boolean canPlay(Player p) {
//		// if player doesn't have non null Tile, canPlay is false
//		if (p.getNonNullTiles().size() == 0) {
//			return false;
//		}
//		
//		// all boarders open to be matched
//		ArrayList<Character> openToMatch = new ArrayList<>();
//		
//		for (Integer idx : Board.hasVN) {
//			// if there's a Tile on it, and it's vertical boarder color is null
//			// then this Tile's vertical color is open to be matched
//			if ((!board.fieldIsEmpty(idx)) && (board.getVerticalBoarderColor(idx) == null)) {
//				openToMatch.add(board.getTile(idx).getVertical());
//			}
//		}
//		
//		for (Integer idx : Board.hasLN) {
//			// if there's a Tile on it, and it's left boarder color is null
//			// then this Tile's left color is open to be matched
//			if ((!board.fieldIsEmpty(idx)) && (board.getLeftBoarderColor(idx) == null)) {
//				openToMatch.add(board.getTile(idx).getLeft());
//			}
//		}
//		
//		for (Integer idx : Board.hasRN) {
//			// if there's a Tile on it, and it's right boarder color is null
//			// then this Tile's right color is open to be matched
//			if ((!board.fieldIsEmpty(idx)) && (board.getRightBoarderColor(idx) == null)) {
//				openToMatch.add(board.getTile(idx).getRight());
//			}
//		}
//		
//		ArrayList<Character> atHand = new ArrayList<>();
//		for (Tile t : p.getNonNullTiles()) {
//			atHand.add(t.getVertical());
//			atHand.add(t.getLeft());
//			atHand.add(t.getRight());
//		}
//		
//		// only leave the intersection of atHand and openToMath in atHand 
//		atHand.retainAll(openToMatch); 
//		
//		return atHand.size() > 0;
//	}

	public boolean currentCanPlay() {
		return tui.canPlay(listPlayers.get(currentPlayerIdx));
	}
	
	public boolean noOneCanPlay() {
		for (Player p : listPlayers) {
			if (tui.canPlay(p)) {
				return false;
			}
		}
		return true;
	}
	
	public boolean bagIsEmpty() {
		return firstNonNullIdx > 35;
	}
	
	// ======================== Main ========================
	public static void main(String[] args) {
//		Tile[] tiles1 = {new Tile(3, "RGB"), null, null, null};
//		Tile[] tiles2 = {new Tile(3, "RGB"), null, new Tile(1, "HEY"), null};
//		Tile[] tiles3 = {new Tile(3, "RGB"), new Tile(1, "HEY"), null, new Tile(2, "ABC")};
//		Tile[] tiles4 = {new Tile(3, "RGB"), new Tile(1, "HEY"), new Tile(2, "OOP"), new Tile(4, "WHO")};
		
		// Test of dealTiles
		Player A = new HumanPlayer("A");
		Player B = new HumanPlayer("B");
		Player C = new HumanPlayer("C");
		Player D = new HumanPlayer("D");
		
		GameControl shuffled3P = new GameControl( Arrays.asList(A, B, C, D), 
												  true);
		
//		shuffled3P.putTileOnBoard(2, new Tile(0, "XZZ")); // A can
//		shuffled3P.putTileOnBoard(20, new Tile(0, "XXZ")); // B can't
//		System.out.println(shuffled3P.canPlay(A));
//		System.out.println(shuffled3P.canPlay(B));
//		System.out.println(shuffled3P.canPlay(C));
//		System.out.println(shuffled3P.canPlay(D));
//		System.out.println(shuffled3P.noOneCanPlay());

		int firstIdx = shuffled3P.dealTiles();
		System.out.println("First player is Player at index: " + firstIdx);

		System.out.println("First non null tile index in bag is : " + shuffled3P.firstNonNullIdx);
		
		shuffled3P.makeFirstMove();
		
		while (!(shuffled3P.bagIsEmpty() && shuffled3P.noOneCanPlay())) {
			shuffled3P.makeNormalMove();
		}
		
		System.out.println("Congrats! You have run the whole game!");
		
	}

}
