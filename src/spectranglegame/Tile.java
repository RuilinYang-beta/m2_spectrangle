package spectranglegame;

public class Tile {
	
	private int value;
	private char vertical;
	private char left;
	private char right;
	private int rotation;
	
	// =================== Constructors ===================
	/** Constructs the tile object
	 * @param value gives the value of the tile
	 * @param colors gives the string of the colors in the order right, vertical, left
	 */
	/* @requires value >=0 && value < 7;
	 * @requires colors != null;
	 * */
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
	
	// =================== Setters ===================
	// ------------ I would suggest not to use them ------------
	/**
	 * @param v gives the new value of the tile
	 */
	private void putVertical(char v) {
		this.vertical = v;
	}
	
	/**
	 * @param v gives the new left color of the tile
	 */
	private void putLeft(char l) {
		this.left = l;
	}
	
	/**
	 * @param v gives the new right color of the tile
	 */
	private void putRight(char r) {
		this.right = r;
	}

	// =================== Rotation Related ===================
	
    /**
     *  Function that rotates a tile with the tile given as a parameter
	 * @param tile indicates the tile that will be rotated 
	 */
	public void rotateTile() {
		if (this.getRotation() % 2 == 0) {
			char v = getVertical();
			char l = getLeft();
			putLeft(v);
			putVertical(l);
//			this.rotate(this.getRotation() + 1);
		} else {
			char v = getVertical();
			char l = getLeft();
			char r = getRight();
			putLeft(l);
			putRight(v);
			putVertical(r);
//			if (this.getRotation() == 5) {
//				this.rotate(0);
//			} else {
//				this.rotate(this.getRotation() + 1);
//			}
		}
		rotation =  (rotation + 1) % 6;
	} 
	
	/**
	 * @return A new Tile generated from this tile, after rotate once.
	 */
	public Tile rotateTileOnce() {
		Tile t;
		
		if (this.isFacingUp()) {
			// constructor order (t.val, t.vertical, t.left, t.right, t.rotation)
			// when this.isFacingUp, color changes as follows: 
			// this.left     -->  t.vertical
			// this.vertical -->  t.left
			// this.right    -->  t.right
			t = new Tile(value, left, vertical, right, 
					     (rotation + 1) % 6);
		} else {
			// constructor order (t.val, t.vertical, t.left, t.right, t.rotation)
			// when !this.isFacingUp, color changes as follows: 
			// this.left     -->  t.left
			// this.vertical -->  t.right
			// this.right    -->  t.vertical
			t = new Tile(value, right, left, vertical, 
						 (rotation + 1) % 6);
		}
		return t;
	}
	
	public Tile rotateTileTwice() {
		// when rotating twice, no matter original is facing up or down:
		// this.right    -->  t.vertical
		// this.vertical -->  t.left
		// this.left     -->  t.right
		Tile t = new Tile(value, right, vertical, left, 
						  (rotation + 2) % 6);
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
	/*
	 * @ requires this.rotation % 2 == 0;
	 */
	public void showTile() {
		String template =" " +
                "        / \\\n" +
                "        / " + value + " \\\n" +
                "       /"+ left+ " "  + " " +right +" \\\n" +
                "      /   " + vertical +"   \\\n" +
                "     /---------\\\n" ;
		System.out.print(template);
	}
	
	public void showTiledown() {
		String template = 
			     "   \\---------/\n" +
			     "     \\ " + "  " + vertical + "   " +"/ \n" +
                 "      \\" + left + " "  + " " + right +  " "+ "/\n" +
                 "       \\ " + value + " "+ "/\n" +
			     "        \\ / \n " ;
		System.out.print(template);
	}
	
	
	public void showTileRotations() {
		if (this.getRotation() % 2 == 0) {
			for (int i = 0; i < 3; i++) {
				this.showTile();
				this.rotateTile();
			}
		} else {
			for (int i = 0; i < 3; i++) {
				this.showTiledown();
				this.rotateTile();
			}
		}
	}
	
	// =================== Main ===================
	public static void main(String[] args) {
		Tile t = new Tile(3,"RGB");
		Tile t1 = new Tile(3, "RGB");
		//System.out.println(t.stringTile().equals(t1.stringTile()));
		//t1.showTileRotations();
		t1.rotateTile();
		t1.showTileRotations();
//		t1.showTiledown();
//		t1.showTile();
//		String s = t.stringTile();
//		System.out.println(s);
//		System.out.println(t.toString());
	}

}
