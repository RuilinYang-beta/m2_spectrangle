package players;

import java.util.Scanner;

import spectranglegame.*;

public abstract class Player {
	protected String name;
	protected Tile[] tiles;
	
	/*
	 * @requires n != null;
	 * @ensures this.getName() == n; 
	 */
	/**
	 * @param n represents the name of the player
	 */
	public Player(String n) {
		this.name = n;
	}
	
	public String getName() {
		return name;
	}
	

	/*
	 * @requires tiles != null;
	 * @ensures this.getTiles() == tiles;
	 */
	/**
	 * @param tiles represents the tiles that the player will receive and have in hand
	 */
	public void TilesinHand(Tile[] tiles) {
		this.tiles = tiles;
	}
	
	/*
	 * Returns the tiles in the hand of ther player
	 */
	public Tile[] getTiles() {
		return tiles;
	}
	
//	public abstract int determineMove(Board b);

	public abstract int chooseField(Board b);

	
	public Tile chooseTile(){
		// choose a Tile, return the Tile
		// and accordingly set the Tile in TileAtHand to null
		
		return null;
	}
	
	public Tile chooseRotation(Tile[] allRotation) {
		
		// return the Tile of the chosen rotation
		return null;
	}
	
	
}
