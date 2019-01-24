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
	 */
	/**
	 * @param tiles represents the tiles that the player will receive and have in hand
	 */
	public void getTiles(Tile[] tiles) {
		this.tiles = tiles;
	}
	
//	public abstract int determineMove(Board b);
	
	/**
	 * @param b A board instance of the game the player is on.
	 * @return An array of length 3: [idxFieldOfChoice, idxOfTilesAtHand, rotationOfTile]
	 */
	public abstract int[] makeMove(Board b) ;
	
	
//	public int determineField(Board b) {
//		;
//	}
	
	public int chooseField(Board b) {
		Scanner in = new Scanner(System.in);
		int num = in.nextInt();
		return num;
	}
	
	public Tile chooseTile() {
		return null;
	}
	
	
}
