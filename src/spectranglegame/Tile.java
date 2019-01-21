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
	 * 
	 */
	public void rotate(int rotation) {
		if(rotation >= 0 && rotation < 6) {
			this.rotation = rotation;
		}
	}
	
	/**
	 * @return String representation of the tile in the next format: rotation + String representing the colors + value;
	 */
	public String stringTile() {
		switch (this.rotation) {
		case 0:
			return "" + rotation + right + vertical + left + value;
		case 1:
			return "" + rotation + left + vertical + right + value;
		case 2:
			return "" + rotation  + left + right + vertical + value;
		case 3:
			return "" + rotation + right + vertical + left  + value;
		case 4:
			return "" + rotation + vertical + left + right  + value;
		case 5:
			return "" + rotation + left + right + vertical + value;
		}
		return null;
	}
	
	 public String toString(Tile tile) {
	    	return "" + tile.stringTile().charAt(1) + tile.stringTile().charAt(2) + tile.stringTile().charAt(3) + tile.stringTile().charAt(4);
	 }
	 
	public static void main(String[] args) {
		Tile t = new Tile(3,"RGB");
		String s = t.stringTile();
		System.out.println(s);
	}

}

