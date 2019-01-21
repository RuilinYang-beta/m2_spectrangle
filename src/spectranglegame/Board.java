package spectranglegame;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;

/**
 * @author RuilinYang
 *
 */
public class Board {
	
	private static final List<Integer> bonuses =       Arrays.asList(1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 2, 4, 1, 4, 2, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 3, 1);
    // better to remove these fields, Board only maintain Tile[]. for better encapsulation.
//	private static List<Integer> values =        Arrays.asList(5,   null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
//    private static List<Character> vertical =    Arrays.asList('R', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
//    private static List<Character> left =        Arrays.asList('G', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
//    private static List<Character> right =       Arrays.asList('B', null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    
    // an array of current tiles on board, null if a field does not has a tile
 	private Tile[] tilesOnBoard = new Tile[36];
 	// an helper array of getRCIdex
 	private Integer[] accumlatedNum = {1, 4, 9, 16, 25, 36};
 	
	
	/**
	 * Generate a board, with no tiles on it.
	 */
	public Board() {
		Arrays.fill(tilesOnBoard, null);
	}
	
	/**
	 * @param i The one dimensional index.
	 * @return True if this index is legal.
	 */
	public static boolean isLegalIdx(Integer i) {
		return (0 <= i) && (i <= 35);
	}
	
	/**
	 * @param rowCol Row-column index of that field.
	 * @return True if this index is legal.
	 */
	public static boolean isLegalIdx(Integer[] rowCol) {
		Integer row = rowCol[0];
		Integer col = rowCol[1];
		boolean cond1 = (0 <= row) && (row <= 5);
		boolean cond2 = (-row <= col) && (col <= row);
		return cond1 && cond2;
	}
	
	/**
	 * To get the one dimensional index from the row-column index.
	 * @param rowCol The row-column coordinate of a field
	 * @return One dimension index of that field
	 */
	public Integer getOneDimIndex(Integer[] rowCol) {
		if (!isLegalIdx(rowCol)) {
			return null;
		}
		
		Integer r = rowCol[0];
		Integer c = rowCol[1];
		return r * r + r + c;
	}

	/**
	 * To get the row-column index from a one dimensional index.
	 * @param i The one dimensional index.
	 * @return An array of length two, 
	 * 		   of which first element is row index, second element is column index.
	 */
	public Integer[] getRCIndex(Integer i) {
		if (!isLegalIdx(i)) {
			return null;
		}
		
		Integer nthField = i + 1;
		Integer idx = 0;
		Integer[] rowCol = new Integer[2];
		while (nthField - accumlatedNum[idx] > 0) {
			idx += 1;
		}
		rowCol[0] = idx;
		rowCol[1] = (idx == 0) ? 0 : nthField - accumlatedNum[idx - 1] - (idx+1);
		return rowCol;	
	}
	
	/**
	 * Input a one-dimensional index, get the corresponding field's surrounding informations, see return.
	 * @param i An one-dimension index of a field
	 * @return An array of length 4.
	 *         The first is a string of boolean indicating whether the field of interest is facing upward.
	 *         The second is a string abbr. of vertical boarder surrounding color.
	 *         The third is a string abbr. of left boarder surrounding color.
	 *         The forth is a string abbr. of right boarder surrounding color.
	 * 		   If one specific neighbor does not exist, then the corresponding surrounding color is null.
	 */
//	public Integer[] getSurroundingInfo(Integer i) {
	public String[] getSurroundingInfo(Integer i) {
		if (!isLegalIdx(i)) {
			return null;
		}
		
		Integer[] rowColIdx = getRCIndex(i);
		
		// get a stub row-col index of the left/right/vertical neighbor
		// they can be illegal so later there's a sanity check
		Integer[] lNeighborIdx = rowColIdx.clone(); lNeighborIdx[1] -= 1;
		Integer[] rNeighborIdx = rowColIdx.clone(); rNeighborIdx[1] += 1;
		Integer[] vNeighborIdx = rowColIdx.clone();
		
		boolean isFacingUp = ((rowColIdx[0] + rowColIdx[1]) % 2 == 0);
		if (isFacingUp) {
			vNeighborIdx[0] += 1;
		} else {
			vNeighborIdx[0] -= 1;
		}	
		
		// test run without tile: return neighbor index
//		Integer[] neighbors = new Integer[3];
//		neighbors[0] = (isLegalIdx(vNeighborIdx)) ? getOneDimIndex(vNeighborIdx) : null;
//		neighbors[1] = (isLegalIdx(lNeighborIdx)) ? getOneDimIndex(lNeighborIdx) : null;
//		neighbors[2] = (isLegalIdx(rNeighborIdx)) ? getOneDimIndex(rNeighborIdx) : null;
		
		// test run with tile: return an array of length 4:
		// [this_tile_isFacingUp, 
		//  this_tile's_vertical_boarder_color, 
		//  this_tile's_left_boarder_color, 
		//  this_tile's_right_boarder_color ]
		String[] surroundings = new String[4];
		// can be read to boolean using Boolean.parseBoolean(surroundings[0]);
		surroundings[0] = (isFacingUp)? "true" : "false";
		surroundings[1] = (isLegalIdx(vNeighborIdx)) ? "" + getTile(getOneDimIndex(vNeighborIdx)).getVertical() : null;
		// the surrounding color of left boarder should be the **right** color of the left neighbor
		surroundings[2] = (isLegalIdx(lNeighborIdx)) ? "" + getTile(getOneDimIndex(lNeighborIdx)).getRight() : null;
		// the surrounding color of right boarder should be the **left** color of the left neighbor
		surroundings[3] = (isLegalIdx(rNeighborIdx)) ? "" + getTile(getOneDimIndex(rNeighborIdx)).getLeft() : null;
		
//		return neighbors;
		return surroundings;
	}
	
	/**
	 * To see if a field is empty.
	 * @param i The index of the field.
	 * @return True if the field is empty.
	 */
	public boolean isEmpty(int i) {
		return (this.tilesOnBoard[i] == null);
	}
	
	/**
	 * @return An array list of empty fields.
	 */
	public ArrayList<Integer> getEmptyFields() {
		ArrayList<Integer> emptyFields = new ArrayList<>();
		for (int i = 0; i < tilesOnBoard.length; i++) {
			if (tilesOnBoard[i] == null) {
				emptyFields.add(i);
			}
		}
		return emptyFields;
	}
	
	/**
	 * @return True if the board is full.
	 */
	public boolean isFull() {
		return this.getEmptyFields().size() == 0;
	}
	
	/**
	 * @param i The index of the tile.
	 * @return The tile of that index. 
	 */
	public Tile getTile(int i) {
		return tilesOnBoard[i];
	}
	
	/**
	 * @param i The index you want to put a tile on.
	 * @param t The tile object.
	 */
	public void setTile(int i, Tile t) {
		this.tilesOnBoard[i] = t;
	}
	
	public void resetBoard() {
		Arrays.fill(tilesOnBoard, null);
	}
	
}
