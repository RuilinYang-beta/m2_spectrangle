package spectranglegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import players.*;

public class Board { 
	
	public static final List<Integer> BONUSES =       Arrays.asList(1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 2, 4, 1, 4, 2, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 3, 1);
 	private static final Integer[] ACCUMULATEDNUM = {1, 4, 9, 16, 25, 36};    // an helper array of getRCIdex
 	private static final int FIELDSNUM = 36;
 	// index of fields that have a vertical neighbor, left neighbor, and right neighbor
 	public static final List<Integer> hasVN = new ArrayList<Integer>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 26, 28, 30, 32, 34));
 	public static final List<Integer> hasLN = new ArrayList<Integer>(Arrays.asList(2, 3, 5, 6, 7, 8, 10, 11, 12, 13, 14, 15, 17, 18, 19, 20, 21, 22, 23, 24, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35));
 	public static final List<Integer> hasRN = new ArrayList<Integer>(Arrays.asList(1, 2, 4, 5, 6, 7, 9, 10, 11, 12, 13, 14, 16, 17, 18, 19, 20, 21, 22, 23, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34));
 	
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
	 * 
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
	 * 
	 * @param i The one dimensional index.
	 * @return An array of length two, of which first element is row index, second
	 *         element is column index.
	 */
	public static Integer[] getRCIndex(Integer i) {
		if (!Board.isLegalIdx(i)) {
			return null;
		}

		Integer nthField = i + 1;
		Integer idx = 0;
		Integer[] rowCol = new Integer[2];
		while (nthField - ACCUMULATEDNUM[idx] > 0) {
			idx += 1;
		}
		rowCol[0] = idx;
		rowCol[1] = (idx == 0) ? 0 : nthField - ACCUMULATEDNUM[idx - 1] - (idx + 1);
		return rowCol;
	}

	// ======================== Instance Queries ========================
	/**
	 * Input a one-dimensional index, get the corresponding field's surrounding
	 * informations, see return.
	 * 
	 * @param i An one-dimension index of a field
	 * @return An array of length 4: [fieldDirection, verticalBoarder, leftBoarder,
	 *         rightBoarder] (verticalBoarder != null) ==> (field has a vertical
	 *         neighbor field) && (there's a tile on vertical neighbor field);
	 */
	// public Integer[] getSurroundingInfo(Integer i) {
	public Character[] getSurroundingInfo(Integer i) {
		if (!isLegalIdx(i)) {
			return null;
		}

		Integer[] rowColIdx = getRCIndex(i);

		// get a stub row-col index of the left/right/vertical neighbor
		// they can be illegal so later there's a sanity check
		Integer[] lNeighborIdx = rowColIdx.clone();
		lNeighborIdx[1] -= 1;
		Integer[] rNeighborIdx = rowColIdx.clone();
		rNeighborIdx[1] += 1;
		Integer[] vNeighborIdx = rowColIdx.clone();

		boolean facingUp = isFacingUp(i);
		if (facingUp) {
			vNeighborIdx[0] += 1;
		} else {
			vNeighborIdx[0] -= 1;
		}	

		Character[] surroundings = new Character[4];
		// U for up, D for down;
		surroundings[0] = (facingUp) ? 'U' : 'D';
		surroundings[1] = (isLegalIdx(vNeighborIdx))
				? ((getTile(getOneDimIndex(vNeighborIdx)) != null) ? getTile(getOneDimIndex(vNeighborIdx)).getVertical()
						: null)
				: null;
		// the surrounding color of left boarder should be the **right** color of the
		// left neighbor
		surroundings[2] = (isLegalIdx(lNeighborIdx))
				? ((getTile(getOneDimIndex(lNeighborIdx)) != null) ? getTile(getOneDimIndex(lNeighborIdx)).getRight()
						: null)
				: null;
		// the surrounding color of right boarder should be the **left** color of the
		// left neighbor
		surroundings[3] = (isLegalIdx(rNeighborIdx))
				? ((getTile(getOneDimIndex(rNeighborIdx)) != null) ? getTile(getOneDimIndex(rNeighborIdx)).getLeft()
						: null)
				: null;

//		return neighbors;
		return surroundings;
	}

	/**
	 * Get the vertical boarder color of a field, null if there's no tile on the vertical boarder field.
	 * @requires isLegalIdx(idx) && hasVN.contains(idx)
	 */
	public Character getVerticalBoarderColor(Integer idx) {
		Integer[] rowColIdx = getRCIndex(idx);
		
		Integer[] vNeighborIdx = rowColIdx.clone();
		
		boolean facingUp = isFacingUp(idx);
		if (facingUp) {
			vNeighborIdx[0] += 1;
		} else {
			vNeighborIdx[0] -= 1;
		}
		// if there's a Tile on vertical neighbor field, return the vNeighbor's v color; else return null;
		Tile vNeighbor = getTile(getOneDimIndex(vNeighborIdx));
		return (vNeighbor != null)? vNeighbor.getVertical() : null;
	}
	
	/**
	 * Get the left boarder color of a field, null if there's no tile on the left boarder field.
	 * @requires isLegalIdx(idx) && hasLN.contains(idx)
	 */
	public Character getLeftBoarderColor(Integer idx) {
		Integer[] rowColIdx = getRCIndex(idx);
		
		Integer[] lNeighborIdx = rowColIdx.clone(); lNeighborIdx[1] -= 1;
		
		// If there's a Tile on the left neighbor field, return the left neighbor's **right** color; else return null;
		Tile lNeighbor = getTile(getOneDimIndex(lNeighborIdx));
		return (lNeighbor != null)? lNeighbor.getRight() : null;
	}
	
	/**
	 * Get the right boarder color of a field, null if there's no tile on the right boarder field.
	 * @requires isLegalIdx(idx) && hasRN.contains(idx)
	 */
	public Character getRightBoarderColor(Integer idx) {
		Integer[] rowColIdx = getRCIndex(idx);
		
		Integer[] rNeighborIdx = rowColIdx.clone(); rNeighborIdx[1] += 1;
		
		// If there's a Tile on the right neighbor field, return the right neighbor's **left** color; else return null;
		Tile rNeighbor = getTile(getOneDimIndex(rNeighborIdx));
		return ( rNeighbor != null)? rNeighbor.getLeft() : null;
		
	}
	
	/**
	 * To see if a field is empty.
	 * 
	 * @param i The index of the field.
	 * @return True if the field is empty.
	 */
	public boolean fieldIsEmpty(int i) {
		return (this.tilesOnBoard[i] == null);
	}

	/**
	 * @return An ArrayList of index of empty fields.
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
	 * @return An ArrayList of index of empty fields that has at least a neighboring Tile.
	 */
	public ArrayList<Integer> getEmptyFieldsWithNeighborTile(){
		ArrayList<Integer> emptyFieldsWNT = new ArrayList<>();
		for (Integer emptyIdx: getEmptyFields()) {
			if (fieldHasNeighborTile(emptyIdx)) {
				emptyFieldsWNT.add(emptyIdx);
			}
		}
		return emptyFieldsWNT;
	}

	public boolean fieldHasNeighborTile(Integer idx) {
		Character[] srd = getSurroundingInfo(idx);
		return (srd[1] != null) || (srd[2] != null) || (srd[3] != null);
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

	/**
	 * A getter of List<Integer> values;
	 */
	protected List<Integer> getValuesOnBoard() {
		return values;
	}

	/**
	 * A getter of List<Character> vertical;
	 */
	protected List<Character> getVerticalOnBoard() {
		return vertical;
	}

	/**
	 * A getter of List<Character> left;
	 */
	protected List<Character> getLeftOnBoard() {
		return left;
	}

	/**
	 * A getter of List<Character> right;
	 */
	protected List<Character> getRightOnBoard() {
		return right;
	}

	// ======================== Instance Commands ========================

	/**
	 * Put a tile t on the field with index i, update Tile[] and also 4 arrayLists
	 * accordingly.
	 * 
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

	/**
	 * 
	 */
	public void resetBoard() {
		values = new ArrayList<>(Collections.nCopies(FIELDSNUM, null));
		vertical = new ArrayList<>(Collections.nCopies(FIELDSNUM, null));
		left = new ArrayList<>(Collections.nCopies(FIELDSNUM, null));
		right = new ArrayList<>(Collections.nCopies(FIELDSNUM, null));
	}
	
	// ======================== Temp: Main method ========================
	public static void main(String[] args) {
		// construct a nearly finished board
		Board b = new Board();
		b.setTile(0, new Tile(1, "RGR")); 
		b.setTile(1, new Tile(1, "BYB"));    b.setTile(3, new Tile(1, "YPR"));
		b.setTile(4, new Tile(1, "GGG"));    b.setTile(6, new Tile(1, "GYG"));     b.setTile(8, new Tile(1, "BBR"));
		b.setTile(10, new Tile(1, "BGP"));   b.setTile(14, new Tile(1, "YBR"));
//		b.setTile(17, new Tile(1, "RGR"));   b.setTile(18, new Tile(1, "ZZZ"));    b.setTile(19, new Tile(1, "GYP"));  b.setTile(20, new Tile(1, "ZZZ"));  b.setTile(21, new Tile(1, "YGY"));  b.setTile(22, new Tile(1, "ZZZ"));  b.setTile(23, new Tile(1, "PPP"));
//		b.setTile(26, new Tile(1, "PBB"));   b.setTile(34, new Tile(1, "YYY"));
		for (int i = 16; i < 36; i++) {
			b.setTile(i, new Tile(1, "ZZZ"));
		}
		
		GameTUI tui = new GameTUI(b);
		tui.printBoardDynamic(b);
		
		// Empty fields: [2, 5, 7, 9, 11, 12, 13, 15]
		// with 3 non-null neighbor Tile
			// 2: new Tile(1, "BGR"); 5: new Tile(1, "GYG"); 7: new Tile(1, "GPR")
		// with 2 neighbor Tile
			// 9: new Tile(1, "PZX"), 11: new Tile(1, "XZB"), 13: new Tile(1, "RZX"), 15: new Tile(1, "XZY")
		// with 1 neighbor Tile
			// 12: new Tile(1, "XYX")
		System.out.println(b.getEmptyFields());
		
		Tile[] tiles1 = {null, null, null, new Tile(3, "BZY")};
//		Tile[] tiles2 = {new Tile(3, "RGB"), null, new Tile(1, "HEY"), null};
//		Tile[] tiles3 = {new Tile(3, "RGB"), new Tile(1, "HEY"), null, new Tile(2, "ABC")};
//		Tile[] tiles4 = {new Tile(3, "RGB"), new Tile(1, "HEY"), new Tile(2, "OOP"), new Tile(4, "WHO")};
		
		// Test of dealTiles
		Player A = new HumanPlayer("A", tiles1);
		Player B = new HumanPlayer("B");
		Player C = new HumanPlayer("C");
		Player D = new HumanPlayer("D");
		
		System.out.println(tui.canPlay(A));
		System.out.println(tui.canPlay(B));
		
		
	}
}
