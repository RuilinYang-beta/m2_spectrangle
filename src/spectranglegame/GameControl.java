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
	private Bag bag;
	private List<Tile> tilesCopy;
	private int firstNonNullIdx = 0;
	// used to store player and its tiles
	private Map<Player, Tile[]> mapPlayers;
	// used to store an ordered list of players (to determine play order)
	private List<Player> listPlayers;
	private int numPlayers;
	private Integer firstPlayerIdx = null;
	
	// ======================== Constructor ========================
	public GameControl(List<Player> lp, boolean shuffle) {
		this.board = new Board();
		this.bag = new Bag(shuffle);
		// a clone of bag.getTiles()
		this.tilesCopy = new ArrayList<>(bag.getTiles());
		this.mapPlayers = new HashMap<>();
		this.listPlayers = lp;
		this.numPlayers = lp.size();
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
		
		// An array of length 3: [idxFieldOfChoice, idxOfTilesAtHand, rotationOfTile]
		int[] userChoice = firstPlayer.makeMove(board);
		
		// if userChoice is legal
		if (sanitaryCheckFirstMove(userChoice)) {
			// 1. place the chosen rotation of chosen tile on the chosen field
				// to-do
			// 2. draw another tile to replace mapPlayers.get(firstPlayer)[userChoice[1])
			mapPlayers.get(firstPlayer)[userChoice[1]] = drawATile();
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
	private boolean sanitaryCheckFirstMove(int[] choices) {
		// not a bonus field
		boolean cond1 = !board.isBonusField(choices[0]);
		// direction of field match rotation of tile
		boolean cond2 = board.isFacingUp(choices[0]) == (choices[2] % 2 == 0);
		
		return cond1 && cond2;
	}
	
	/**
	 * Get a tile from the bag, nullify the corresponding tile in the tilesCopy, 
	 * and update firstNonNullIdx.
	 * @return The tile being drew from the bag.
	 */
	public Tile drawATile() {
		Tile t = bag.getTiles().get(firstNonNullIdx);
		tilesCopy.set(firstNonNullIdx, null);
		firstNonNullIdx++;
		return t;
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

	
	// ================== Gaming: other functionalities ==================
		/**
		 * Safety measurement before every new game.
		 */
		public void resetBoard() {
			board.resetBoard();
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

	public void showtile(Tile t) {
		String template =                     " \n" +
                "                              / \\\n" +
                "                             /   \\\n" +
                "                            /"+ t.getLeft()+" " + t.getValue() + " " +t.getLeft() +"\\\n" +
                "                           /   " + t.getVertical() +"   \\\n" +
                "                          /---------\\\n" ;
		System.out.print(template + "  ");
	}

	/**
	 * A getter of this.bag.getTiles(); 
	 * Don't change it after getting it!
	 * @return
	 */
	public List<Tile> getTiles(){
		return bag.getTiles();
	}
	
//	 public static void main(String[] args) {
//		 List<Player> lp = new ArrayList<>();
//		 boolean shuffle = true;
//		 Tile t = new Tile(3, "RGB");
//		 GameControl g = new GameControl(lp, shuffle);
//		 g.showtile(t);
//		 
//		 
//	 }
//	
	// ======================== Main ========================
	public static void main(String[] args) {
		
		GameControl shu3 = new GameControl( Arrays.asList(new HumanPlayer("A"),
															 new HumanPlayer("B"),
															 new HumanPlayer("C")), 
											  true);
		shu3.dealTiles();
		System.out.println(shu3.getTilesCopy());
		
	}

}
