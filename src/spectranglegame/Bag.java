package spectranglegame;


import java.util.*;
import java.util.List;
import java.util.Arrays;
/*
 * @author Radu
 * The bag of tiles;
 */
public class Bag {

	private static List<Integer> values =        Arrays.asList(6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 3, 3, 3, 3, 2, 2, 2, 1, 1, 1, 1);
    private static List<Character> vertical =    Arrays.asList('R', 'B', 'G', 'Y', 'P', 'R', 'R', 'B', 'B', 'G', 'G', 'Y', 'Y', 'P', 'P', 'R', 'R', 'B', 'B', 'G', 'G', 'Y', 'Y', 'P', 'P', 'B', 'G', 'G', 'R', 'R', 'P', 'P', 'R', 'Y', 'Y', 'W');
    private static List<Character> left =        Arrays.asList('R', 'B', 'G', 'Y', 'P', 'Y', 'P', 'R', 'P', 'R', 'B', 'G', 'B', 'Y', 'G', 'B', 'G', 'G', 'Y', 'Y', 'P', 'R', 'P', 'R', 'B', 'P', 'Y', 'P', 'B', 'P', 'R', 'G', 'P', 'G', 'B', 'W');
    private static List<Character> right =       Arrays.asList('R', 'B', 'G', 'Y', 'P', 'R', 'R', 'B', 'B', 'G', 'G', 'Y', 'Y', 'P', 'P', 'R', 'R', 'B', 'B', 'G', 'G', 'Y', 'Y', 'P', 'P', 'Y', 'R', 'B', 'G', 'B', 'Y', 'Y', 'G', 'B', 'R', 'W');
//	private String[] tiles = new String[36];
	private List<String> tiles = new LinkedList<String>();
	private MineTile tile;
    
	/**
	 * Constructs the bag of tiles
	 */
    public Bag() {
    	for(int i = 0; i < 36; i++) {
   // 		tiles[i] = vertical.get(i).toString() + left.get(i).toString() + right.get(i).toString();
    		tiles.add(vertical.get(i).toString() + left.get(i).toString() + right.get(i).toString() + values.get(i));
      	}
    }
    
    /*
     * Rotates the tile with the given input
     * @param i is the index of the tile that needs to be rotated
     * 
     * @ ensures old.vertical.get(i) == left.get(i) && old.left.get(i) == vertical.get(i) && old.right.get(i) == left.get(i);
     */
//	public void rotateTile(int i) {
//		if (i >= 0 && i < 36) {
//			char v = vertical.get(i);
//			char l = left.get(i);
//			char r = right.get(i);
//			tiles[i] = "" + r + v + l;
//		}
//	}
//	
    /*
     * @ensures (tile.getRotation() == \old.tile.getRotation() + 1 || tile.getRotation() == 0) ;
     * @ensures \old.tile.getVertical() == tile.getRight() && \old.tile.getLeft() == tile.getVertical() && \old.tile.getRight() == tile.getLeft();
     */
    /**
     *  Function that rotates a tile with the tile given as a parameter
	 * @param tile indicates the tile that will be rotated 
	 */
	public void rotate(MineTile tile) {
		if(tile.getRotation() == 5) {
			tile.rotate(0);
		}else {
			tile.rotate(tile.getRotation() +1);
		}
		char v = tile.getVertical();
		char l = tile.getLeft();
		char r = tile.getRight();
		tile.putLeft(v);
		tile.putRight(l);
		tile.putVertical(r);
	}
	
	/*
	 * @requires i >= 0 && i < 36;
	 */
     /** 
	 * @param i index of the tile
	 * @return A tile based on index
	 */
	public MineTile getTile(int i) {
		if (i >= 0 && i < tiles.size()) {
	//		tile = new Tile(values.get(i), tiles[i]);
			tile = new MineTile(values.get(i), tiles.get(i));
			return tile;
		}
		return null;
	}
	
	/*
	 * @ensures \result >= 0 && \result < 36;
	 */
     /**
      * Returns the index of a tile
     * @param tile the string representing the tile
     * @return index i of the tile
     */
    public int getIndex(String tile) {
    	for(int i = 0; i < tiles.size(); i++) {
    		if(tiles.get(i).equals(tile)) {
    			return i;
    		}
    	}
    	return 0;
    }
    
    /*
     * @ensures \result > 0 && \result < 7;
     */
     /**
     * @param i i index of the tile
     * @return The value of the tile 
     */
    public int getValue(int i){
    	return values.get(i);
    }
    
    /**
     * @param tiles represents the string that creates the tile
     * @return object of the String
     */
    public MineTile makeTile(String tiles) {
    	int i = getIndex(tiles);
    	MineTile t = new MineTile(values.get(i), tiles);
    	return t;
    }
    
    /*
     * @ensures \result instanceof Tile;
     */
    /**
     * Generates a random value for the tile
     * @return a random tile from the bag
     */
    public MineTile randomTile() {
    	Random rand = new Random();
    	int n = rand.nextInt(35) + 1;
    	return makeTile(this.tiles.get(n));
    }
    
    /**
     * Prints all the tiles from the bag
     * 
     */
    public void showtiles() {
    	for(int i = 0; i < tiles.size(); i++) {
    		System.out.println(tiles.get(i));
    	}
    }
    
    /**
     * @param tile the tile that will be removed from the bag
     */
    /*
     * @ensures tiles.size() == \old.tiles.size() - 1;
     */
    public void removeTile(MineTile tile) {
    	boolean ok = true;
    	for(String s : tiles) {
    		if(s.equals(tile.toString())) {
    			tiles.remove(s);
    			ok = false;
    		}
    	}
    	if(ok) {
    		System.out.println("Invalid tile or already removed");
    	}
    }
    
    /**
     * @param tile is the tile that is checked
     * @return true if the tile is a valid tile from the bag, or false in case it is not
     */
    public boolean isValidTile(MineTile tile) {
    	for(String s : tiles) {
    		if(s.equals(tile.toString())) {
    			return true;
    		}
    	}
    	return false;
    }
    
    
	public static void main(String[] args) {
		Bag t = new Bag();
		MineTile t1 = new MineTile(3, "RGB");
		t.showtiles();
		t.rotate(t1);
//		t.rotate(t1);
//		t.rotate(t1);
//		t.rotate(t1);
//		t.rotate(t1);
//		t.rotate(t1);
//		System.out.println(t1.stringTile());
	}
}
