package players;

import java.util.ArrayList;
import java.util.Scanner;

import spectranglegame.*;

public abstract class Player {
	protected String name;
	protected Tile[] tilesAtHand;
	protected int score;
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
		this.tilesAtHand = tiles;
		score = 0;
	}
	
	public Player(String n) {
		this.name = n;
		this.tilesAtHand = new Tile[4];
	}
	
	public String getName() {
		return name;
	}
	
	/*
	 * Returns the tiles in the hand of ther player
	 */
	public Tile[] getTiles() {
		return tilesAtHand;
	}

	

	// choose a Tile, return the Tile
	// and accordingly set the Tile in TileAtHand to null
	public abstract int chooseTileIdx(int numOfTile);
	
	public abstract Integer chooseSkipOrSwap(int numOfTile) ;
	
	public abstract int chooseTileIdxToSwap(int numOfTile);
	
	public abstract void toSkip();
	
	public abstract int chooseFieldIdx();
	
	public abstract int chooseRotationIdx() ;
	
	public abstract ArrayList<Tile> getNonNullTiles();
	
	/**
	 * Put the Tile at the first null position of Tiles[].
	 * @requires There exists at least one null value in Tiles[] .
	 */
	public void takeTheTile(Tile t) {
		for (int i = 0; i < tilesAtHand.length; i++) {
			if (tilesAtHand[i] == null) {
//				System.out.println("Here's a null Tile slot.");
				tilesAtHand[i] = t;
				break;
			}
		}
	}
	
	
}
