package spectranglegame;

public class Tile {

	private int value;
	private char vertical;
	private char left;
	private char right;
	private int rotation;

	// =================== Constructors ===================
	/**
	 * Constructs the tile object
	 * 
	 * @param value  gives the value of the tile
	 * @param colors gives the string of the colors in the order right, vertical,
	 *               left
	 */
	/*
	 * @requires value >=0 && value < 7;
	 * 
	 * @requires colors != null;
	 */
	public Tile(int value, String colors) {
		this.value = value;
		right = colors.charAt(0);
		vertical = colors.charAt(1);
		left = colors.charAt(2);
		rotation = 0;
	}

	/*
	 * A public constructor that builds Tile with rotation 0;
	 */
	public Tile(int val, char vColor, char lColor, char rColor) {
		this(val, vColor, lColor, rColor, 0);
	}

	/*
	 * A private constructor that can build Tile with any rotation;
	 */
	private Tile(int val, char vColor, char lColor, char rColor, int rot) {
		this.value = val;
		this.vertical = vColor;
		this.left = lColor;
		this.right = rColor;
		this.rotation = rot;
	}

	// =================== Queries ===================
	public int getValue() {
		return this.value;
	}

	public char getVertical() {
		return this.vertical;
	}

	public char getRight() {
		return this.right;
	}

	public char getLeft() {
		return this.left;
	}

	public int getRotation() {
		return this.rotation;
	}

	public boolean isFacingUp() {
		return (rotation % 2 == 0);
	}

	public boolean isJoker() {
		return this.toString().equals("WWW1");
	}

	// =================== Rotation Related ===================
	/**
	 * @return A new Tile generated from this tile, after rotate once.
	 */
	public Tile rotateTileOnce() {
		Tile t;

		if (this.isFacingUp()) {
			// constructor order (t.val, t.vertical, t.left, t.right, t.rotation)
			// when this.isFacingUp, rotate once, color changes as follows:
			// this.left --> t.vertical
			// this.vertical --> t.left
			// this.right --> t.right
			t = new Tile(value, left, vertical, right, (rotation + 1) % 6);
		} else {
			// when !this.isFacingUp, rotate once, color changes as follows:
			// this.left --> t.left
			// this.vertical --> t.right
			// this.right --> t.vertical
			t = new Tile(value, right, left, vertical, (rotation + 1) % 6);
		}
		return t;
	}

	/**
	 * @return A new Tile generated from this tile, after rotate twice.
	 */
	public Tile rotateTileTwice() {
		Tile t;

		if (this.isFacingUp()) {
			// constructor order (t.val, t.vertical, t.left, t.right, t.rotation)
			// when this.isFacingUp, rotate twice, color changes as follows:
			// this.left --> t.right
			// this.vertical --> t.left
			// this.right --> t.vertical
			t = new Tile(value, right, vertical, left, (rotation + 2) % 6);
		} else {
			// constructor order (t.val, t.vertical, t.left, t.right, t.rotation)
			// when !this.isFacingUp, rotate twice, color changes as follows:
			// this.left --> t.vertical
			// this.vertical --> t.right
			// this.right --> t.left
			t = new Tile(value, left, right, vertical, (rotation + 2) % 6);
		}
		return t;
	}

	/**
	 * @return A new Tile generated from this tile, after rotate four times.
	 */
	public Tile rotateTileFourTimes() {
		Tile t;
		
		if (this.isFacingUp()) {
			// constructor order (t.val, t.vertical, t.left, t.right, t.rotation)
			// when this.isFacingUp, rotate four times, color changes as follows: 
			// this.left     -->  t.vertical
			// this.vertical -->  t.right
			// this.right    -->  t.left
			t = new Tile(value, left, right, vertical, 
					     (rotation + 4) % 6);
		} else {
			// constructor order (t.val, t.vertical, t.left, t.right, t.rotation)
			// when !this.isFacingUp, rotate four times, color changes as follows: 
			// this.left     -->  t.right
			// this.vertical -->  t.left
			// this.right    -->  t.vertical
			t = new Tile(value, right, vertical, left, 
						 (rotation + 4) % 6);
		}
		return t;
	}
	
	// =================== String Representations ===================
	/**
	 * @return String representation of the tile in the next format: rotation + String representing the colors + value;
	 */
	public String stringTile() {
		switch (this.rotation) {
		case 0:
			return "" + rotation + right + vertical + left + value;
		case 1:
			return "" + rotation + right + left + vertical + value;
		case 2:
			return "" + rotation + vertical + left + right + value;
		case 3:
			return "" + rotation + left + vertical + right + value;
		case 4:
			return "" + rotation + left + right + vertical + value;
		case 5:
			return "" + rotation + vertical + right + left + value;
		}
		return null;
	}

	public String toString() {
	    return "" + stringTile().charAt(1) + stringTile().charAt(2) + stringTile().charAt(3) + stringTile().charAt(4);
	 }
	 

	// =================== Visual Representations ===================
	/**
	 * Show a visual representation of a Tile.
	 */
	public void showTile() {
		if (isFacingUp()) {
			showTileUp();
		} else {
			showTileDown();
		}
	}
	 
	/**
	 * Helper function of showTile(). 
	 */
	private void showTileUp() {
		String template =
				// make sure each line is of the same total length
				// for GameTUI to display 3 tiles horizontally
                "    / \\    \n" +
                "   / " + value + " \\   \n" +
                "  / " + left + " " + right + " \\  \n" +
                " /   " + vertical + "   \\ \n" +
                " --------- \n"  ;
		
		System.out.println(template);
	}
	
	/**
	 * Helper function of showTile().
	 */
	private void showTileDown() {
		String template = 
				// make sure each line is of the same total length
				// for GameTUI to display 3 tiles horizontally
			     " --------- \n" +
			     " \\   " + vertical + "   / \n" +
                 "  \\ " + left + " " + right + " /  \n" +
                 "   \\ " + value + " /   \n" +
			     "    \\ /    \n" ;
		System.out.print(template);
	}
	
	// =================== Other ===================
	public Tile deepCopy() {
		return new Tile(value, vertical, left, right, rotation);
	}
	
	// =================== Main ===================
	public static void main(String[] args) {
		Tile t0 = new Tile(3,"RGB");
		Tile t1 = t0.rotateTileOnce();
		Tile t2 = t1.rotateTileOnce();
//		Tile t3 = t2.rotateTileOnce();
//		Tile t4 = t3.rotateTileOnce();
	//	Tile t5 = t4.rotateTileOnce();
		
		System.out.println(t0);
		System.out.println(t1);
		System.out.println(t2);
//		t1.showTile();
//		t3.showTile();
//		t5.showTile();

//		List<Tile> tiles = Arrays.asList(new Tile(1, "RRR"), 
//										 new Tile(2, "GGG"), 
//										 new Tile(3, "BBB"));
//		
//		Tile t = tiles.get(0);
//		tiles.set(0, null);
//		System.out.println(t);
//		System.out.println(tiles);
	}

}
