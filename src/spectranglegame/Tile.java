package spectranglegame;

public class Tile {
	
	int value;
	char vertical;
	char left;
	char right;
	int rotation;
	
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
	
	/**
	 * An alternative constructor. 
	 * @param value
	 * @param vColor
	 * @param lColor
	 * @param rColor
	 */
	public Tile(int v, char vColor, char lColor, char rColor) {
		this.value = v;
		this.vertical = vColor;
		this.left = lColor;
		this.right = rColor;
		this.rotation = 0;
	}
	
	/*
	 * @ensures \result == this.vertical;
	 */
	/**
	 * @return this.vertical
	 */
	public char getVertical() {
		return this.vertical;
	}
	
	/*
	 * @ensures this.getVertical() == v;
	 */
	/**
	 * @param v gives the new value of the tile
	 */
	public void putVertical(char v) {
		this.vertical = v;
	}
	
    /*
     * @ensures (tile.getRotation() == \old.tile.getRotation() + 1 || tile.getRotation() == 0) ;
     * @ensures \old.tile.getVertical() == tile.getRight() && \old.tile.getLeft() == tile.getVertical() && \old.tile.getRight() == tile.getLeft();
     */
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
	 * @return A new Tile of this tile, after rotate once.
	 */
	public Tile rotateTileOnce() {
		Tile t = new Tile(this.value, "BBB"); // this color doesn't really exist, only to fill constructor
		
		if (this.getRotation() % 2 == 0) {
			t.putRight(right);
			t.putVertical(left);
			t.putLeft(vertical);
		} else {
			t.putLeft(left);
			t.putRight(vertical);
			t.putVertical(right);
		}
		t.rotation =  (rotation + 1) % 6;
		return t;
	}
	
	public Tile rotateTileTwice() {
		Tile t = new Tile(this.value, "BBB"); // this color doesn't really exist, only to fill constructor
		
		t.putVertical(right);
		t.putRight(left);
		t.putLeft(vertical);
		
		t.rotation =  (rotation + 2) % 6;
		return t;
	}
	
	
	


//	public void rotate(int rotation) {
//		if(rotation >= 0 && rotation < 6) {
//			this.rotation = rotation;
//		}
//	}
	
	/*
	 * @ensures \result == this.rotation;
	 */
	/**
	 * @return this.rotation 
	 */
	public int getRotation() {
		return this.rotation;
	}
	
	/*
	 * @ensures \result == this.left;
	 */
	/**
	 * @return this.left
	 */
	public char getLeft() {
		return this.left;
	}
	
	/*
	 * @ensures this.getLeft() == l;
	 */
	/**
	 * @param v gives the new left color of the tile
	 */
	public void putLeft(char l) {
		this.left = l;
	}
	
	/*
	 * @ensures \result == this.right;
	 */
	/**
	 * @return this.right.
	 */
	public char getRight() {
		return this.right;
	}
	
	/*
	 * @ensures this.getRight() == r;
	 */
	/**
	 * @param v gives the new right color of the tile
	 */
	public void putRight(char r) {
		this.right = r;
	}
	
	/*
	 * @ensures \result == this.value;
	 */
	/**
	 * @return this.value. 
	 */
	public int getValue() {
		return this.value;
	}
	

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
	 
	 /**
	  * Prints the TUI representation of a tile poining up
	  */
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
	
	/**
	 * Prints the TUI representation of a tile poining down
	 */
	/*
	 * @requires this.rotation % 2 == 1;
	 */
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
