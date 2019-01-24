package players;

import java.util.Scanner;

import spectranglegame.*;

public abstract class Player {
	protected String name;
	protected Tile[] tiles;

	/*
	 * @requires tiles != null;
	 * @requires n != null;
	 * @ensures this.getName() == n; 
	 * @ensures this.getTiles() == tiles
	 */
	/**
	 * @param n represents the name of the player
	 * @param tiles represents the tiles that a player will have in hand
	 */
	public Player(String n, Tile[] tiles) {
		this.name = n;
		this.tiles = tiles;
	}
	
	public String getName() {
		return name;
	}
	

	/*
	 * @requires tiles != null;
	 * @ensures this.getTiles() == tiles;
	 */
	/*
	 * Returns the tiles in the hand of ther player
	 */
	public Tile[] getTiles() {
		return tiles;
	}
	
//	public abstract int determineMove(Board b);

	public abstract int chooseField(Board b);

	// choose a Tile, return the Tile
	// and accordingly set the Tile in TileAtHand to null
//	public Tile chooseTile(){
//	
//		
//	}
	
	public Tile chooseRotation(Tile[] allRotation) {
		
		
		return null;
	}
	
	
	/*
	 * Put the Tile at the first null position of Tiles[].
	 * @ requires There exists at least one null value in Tiles[] .
	 */
	public void takeTheTile(Tile t) {
		
	}
	
	
}
