package spectranglegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.*;

import players.Player;

public class GameControl {
	
	// ------------------------ Fields ------------------------
	private Board board;
	private Bag bag;
	private List<Tile> tilesCopy;
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
	
	// ======================== Commands ========================
	// ------------ Preparation: deal the initial tiles ------------
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
	
	/**
	 * Safety measure before every new game.
	 */
	public void resetBoard() {
		board.resetBoard();
	}
	
	public void askFirstMove() {
		// if when each user has 4 tiles but still duplicate max (very unlikely but still possible)
		// let the first user make the first move 
		if (firstPlayerIdx == null) {
			firstPlayerIdx = 0;
		}
		
		// An array of length 3: [idxFieldOfChoice, idxOfTilesAtHand, rotationOfTile]
		int[] userChoice = listPlayers.get(firstPlayerIdx).makeMove(board);
		
		// if userChoice is legal
		if (sanitaryCheckFirstMove(userChoice)) {
			// place the chosen rotation of chosen tile on the chosen field
		} else {
			System.out.println("Illegal first move, please try again.");
			// need a mechanism to make user try again.
		}
		
	}
	
	private boolean sanitaryCheckFirstMove(int[] choices) {
		
	}
	
	private boolean sanitaryCheck() {
		
	}
	
	private boolean sanitaryField() {
		return false;
	}
	
	private boolean sanitaryRotation() {
		return false;
	}
	
	private boolean sanitaryTile() {
		return false;
	}
	
	
	
	// ------------------------ Queries ------------------------

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
	public List<Tile> getTiles() {
		return tilesCopy;
	}
	
	public static void main(String[] args) {
		
	}
	
	
	
	
	
	
	
	

}
