package spectranglegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.*;

public class GameControl {
	
	private Board board;
	private Bag bag;
	// used to store player and its tiles
	private Map<Player, Tile[]> mapPlayers;
	// used to store an ordered list of players (to determine play order)
	private List<Player> listPlayers;
	private int numPlayers;
	
	// constructor
	public GameControl(List<Player> lp, boolean shuffle) {
		this.board = new Board();
		this.bag = new Bag(shuffle);
		this.mapPlayers = new HashMap<>();
		this.listPlayers = lp;
		this.numPlayers = lp.size();
	}
	
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
		
		Integer firstPlayerIdx = null;
		
		// for each of the 4 slot in Tile[4]
		for (int j = 0; j < 4; j++) {
			// give each user one tile
			for (int i = 0; i < numPlayers; i++) {
				mapPlayers.get(listPlayers.get(i))[j] = this.bag.getTiles().get(j * numPlayers + i);
			}
			
			// if the player to make the first move is undetermined
			// try to determine
			if (firstPlayerIdx == null) {
				// for each player, get a list of total values at hand
				// and collect the results in a single list
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
	/**
	 * A getter of mapPlayers.
	 */
	public Map<Player, Tile[]> getMapPlayers() {
		return mapPlayers;
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
	
	
	 public static void main(String[] args) {
		 List<Player> lp = new ArrayList<>();
		 boolean shuffle = true;
		 Tile t = new Tile(3, "RGB");
		 GameControl g = new GameControl(lp, shuffle);
		 g.showtile(t);
		 
		 
	 }
	

}
