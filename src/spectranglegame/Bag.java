package spectranglegame;

import java.util.*;
import java.util.List;
import java.util.Arrays;

/**
 * The bag of tiles;
 */
public class Bag {

	private static List<Integer> values = Arrays.asList(6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4,
			4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1);
	private static List<Character> vertical = Arrays.asList('R', 'B', 'G', 'Y', 'P', 'R', 'R', 'B', 'B', 'G', 'G', 'Y',
			'Y', 'P', 'P', 'R', 'R', 'B', 'B', 'G', 'G', 'Y', 'Y', 'P', 'P', 'B', 'G', 'G', 'R', 'R', 'P', 'P', 'R',
			'Y', 'Y', 'W');
	private static List<Character> left = Arrays.asList('R', 'B', 'G', 'Y', 'P', 'Y', 'P', 'R', 'P', 'R', 'B', 'G', 'B',
			'Y', 'G', 'B', 'G', 'G', 'Y', 'Y', 'P', 'R', 'P', 'R', 'B', 'P', 'Y', 'P', 'B', 'P', 'R', 'G', 'P', 'G',
			'B', 'W');
	private static List<Character> right = Arrays.asList('R', 'B', 'G', 'Y', 'P', 'R', 'R', 'B', 'B', 'G', 'G', 'Y','Y', 'P', 'P', 'R', 'R', 'B', 'B', 'G', 'G', 'Y', 'Y', 'P', 'P', 'Y', 'R', 'B', 'G', 'B', 'Y', 'Y', 'G','B', 'R', 'W');
	private List<Tile> tiles = new LinkedList<>();
	private Tile tile;


	/**
	 * An alternative constructor that populates List<Tile> tiles with Tiles object.
	 */
	public Bag(boolean shuffle) {
		for (int i = 0; i < 36; i++) {

			tiles.add(new Tile(values.get(i), "" + vertical.get(i) + left.get(i) + right.get(i)));
		}

		if (shuffle) {
			// shuffle the list of tiles in place
			// so every distinct instance of bag maintains a tiles list in diff order
			Collections.shuffle(tiles);
		}
	}

	/**
	 * A getter of List<Tile> tiles
	 * 
	 * @return this.tiles.
	 */
	public List<Tile> getTiles() {
		return tiles;
	}

//	/**
//	 * A getter of List<String> tilesString.
//	 * @return
//	 */
//	public List<String> getTilesString() {
//		return tilesString;
//	}
//	

	/*
	 * @requires i >= 0 && i < 36;
	 */
	/**
	 * @param i index of the tile
	 * @return A tile based on index
	 */
	public Tile getTile(int i) {
		if (i >= 0 && i < tiles.size()) {
			tile = new Tile(values.get(i),
					"" + tiles.get(i).getVertical() + tiles.get(i).getLeft() + tiles.get(i).getRight());
			return tile;
		}
		return null;
	}

	/*
	 * @ensures \result >= 0 && \result < 36;
	 */
	/**
	 * Returns the index of a tile
	 * 
	 * @param tile
	 * @return index i of the tile
	 */
	public int getIndex(Tile tile) {
		for (int i = 0; i < tiles.size(); i++) {
			if (tiles.get(i).getLeft() == tile.getLeft() && tiles.get(i).getRight() == tile.getRight()
					&& tiles.get(i).getLeft() == tile.getLeft() && tiles.get(i).getValue() == tile.getValue()) {
				return i;
			}
		}
		return -1;
	}


 
	/*
	 * @ensures \result > 0 && \result < 7;
	 */
	/**
	 * @param i i index of the tile
	 * @return The value of the tile
	 */
	public int getValue(int i) {
		return values.get(i);
	}

	/**
	 * Prints all the tiles from the bag
	 * 
	 */
	public void showtiles() {
		for (int i = 0; i < tiles.size() && tiles.get(i) != null; i++) {
			System.out.println(i + " " + tiles.get(i));
		}
	}

	/*
	 * @ensures \result == true => s.getLeft() == tile.getLeft() && s.getRight() ==
	 * tile.getRight() && s.getLeft() == tile.getLeft() && s.getValue() ==
	 * tile.getValue()
	 */
	/**
	 * @param tile is the tile that is checked
	 * @return true if the tile is a valid tile from the bag, or false in case it is
	 *         not
	 */
	public boolean isValidTile(Tile tile) {
		for (Tile s : tiles) {
			if (s.getLeft() == tile.getLeft() && s.getRight() == tile.getRight() && s.getLeft() == tile.getLeft()
					&& s.getValue() == tile.getValue()) {
				return true;
			}
		}
		return false;
	}

}
