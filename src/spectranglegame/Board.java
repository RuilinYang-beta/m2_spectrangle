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
	
	public static final List<Integer> BONUSES =       Arrays.asList(1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 2, 4, 1, 4, 2, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 3, 1);
 	private static final Integer[] ACCUMULATEDNUM = {1, 4, 9, 16, 25, 36};    // an helper array of getRCIdex
 	public static final int FIELDSNUM = 36;
 	
	// an array of current tiles on board, null if a field does not has a tile
 	private Tile[] tilesOnBoard = new Tile[36];
	// 4 lists below only cares about the 4 dimension of each of the 36 tiles
	private List<Integer> values;
    private List<Character> vertical;
    private List<Character> left;
    private List<Character> right;
 	
 	// ======================== Constructor ========================
	/**
	 * Generate a board, with no tiles on it.
	 */
	public Board() {
//		Arrays.fill(tilesOnBoard, null);
		values = new ArrayList<>(Collections.nCopies(FIELDSNUM, null));
		vertical = new ArrayList<>(Collections.nCopies(FIELDSNUM, null));
		left = new ArrayList<>(Collections.nCopies(FIELDSNUM, null));
		right = new ArrayList<>(Collections.nCopies(FIELDSNUM, null));
	}
	
	// ======================== Static Methods ========================
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
	 * @param idx The index of the field of your interest.
	 * @return true if this field is a bonus field.
	 */
	public static boolean isBonusField(int idx) {
		return BONUSES.get(idx) != 1;
	}
	
	/**
	 * @param i The index of the field of interest.
	 * @return true if the field is facing up.
	 */
	public static boolean isFacingUp(Integer i) {
		return Board.isFacingUp(Board.getRCIndex(i));
	}
	
	/**
	 * @param rowCol Row-column index of that field.
	 * @return true if the field is facing upward.
	 */
	public static boolean isFacingUp(Integer[] rowCol) {
		return ((rowCol[0] + rowCol[1]) % 2 == 0);
	}
	
	/**
	 * To get the one dimensional index from the row-column index.
	 * @param rowCol The row-column coordinate of a field
	 * @return One dimension index of that field
	 */
 	public static Integer getOneDimIndex(Integer[] rowCol) {
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
	public static Integer[] getRCIndex(Integer i) {
		if (!isLegalIdx(i)) {
			return null;
		}
		
		Integer nthField = i + 1;
		Integer idx = 0;
		Integer[] rowCol = new Integer[2];
		while (nthField - ACCUMULATEDNUM[idx] > 0) {
			idx += 1;
		}
		rowCol[0] = idx;
		rowCol[1] = (idx == 0) ? 0 : nthField - ACCUMULATEDNUM[idx - 1] - (idx+1);
		return rowCol;	
	}
	
	// ======================== Instance Queries ========================
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
	// public Integer[] getSurroundingInfo(Integer i) {
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
		
		boolean facingUp = isFacingUp(i);
		if (facingUp) {
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
		// [isFacingUp, 
		//  vertical_boarder_color, 
		//  left_boarder_color, 
		//  right_boarder_color ]
		String[] surroundings = new String[4];
		// can be read to boolean using Boolean.parseBoolean(surroundings[0]);
		surroundings[0] = (facingUp)? "true" : "false";
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
	public boolean fieldIsEmpty(int i) {
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
	public boolean boardIsFull() {
		return this.getEmptyFields().size() == 0;
	}
	
	/**
	 * @param i The index of the tile.
	 * @return The tile of that index. 
	 */
	public Tile getTile(int i) {
		return tilesOnBoard[i];
	}
	
	protected List<Integer> getValuesOnBoard(){
		return values;
	}
	
	protected List<Character> getVerticalOnBoard(){
		return vertical;
	}
	
	protected List<Character> getLeftOnBoard(){
		return left;
	}
	
	protected List<Character> getRightOnBoard(){
		return right;
	}
	
	// ======================== Instance Commands ========================
	/**
	 * Put a tile t on the field with index i, 
	 * update Tile[] and also 4 arrayLists.
	 * @param i The index you want to put a tile on.
	 * @param t The tile object.
	 */
	public void setTile(int i, Tile t) {
		tilesOnBoard[i] = t;
		values.set(i, t.getValue());
		vertical.set(i, t.getVertical());
		left.set(i, t.getLeft());
		right.set(i, t.getRight());
	}
	
	public void resetBoard() {
		Arrays.fill(tilesOnBoard, null);
	}
	
}
