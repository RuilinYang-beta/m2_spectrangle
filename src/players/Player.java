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
	
	// This function is only meant for interaction between Player and TUI
	// Return an intermediate product that GameControl do not need.
	public abstract Tile chooseTile();
	
	// the Tile t is the returned object of chooseTile()
	public abstract Tile chooseRotation(Tile t);
	
	
}
