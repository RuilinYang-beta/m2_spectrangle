package players;

import java.util.Scanner;

import spectranglegame.*;

public abstract class Player {
	protected String name;
	
	public Player(String n) {
		this.name = n;
	}
	
	public String getName() {
		return name;
	}
	
	public abstract int chooseField(Board b);
	
	public Tile chooseTile(){
		// choose a Tile, return the Tile
		// and accordingly set the Tile in TileAtHand to null;
		
		return null;
	}
	
	public Tile chooseRotation(Tile[] allRotation) {
		
		// return the Tile of the chosen rotation
		return null;
	}
	
	
}
