package players;

import spectranglegame.*;

public abstract class Player {
	protected String name;
	
	public Player(String n) {
		this.name = n;
	}
	
	public String getName() {
		return name;
	}
	
//	public abstract int determineMove(Board b);
	
	/**
	 * @param b A board instance of the game the player is on.
	 * @return An array of length 3: [idxFieldOfChoice, idxOfTilesAtHand, rotationOfTile]
	 */
	public abstract int[] makeMove(Board b) ;
}
