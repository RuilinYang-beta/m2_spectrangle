package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import players.HumanPlayer;

import java.util.Arrays;
import java.util.List;

import spectranglegame.*;


class GameControlTest {
	
	GameControl shuffled3P;   // A GameControl obj with shuffled Tiles, 3 players
	GameControl unshuffled3P; // A GameControl obj with unshuffled Tiles, 3 players
	GameControl unshuffled4P; // A GameControl obj with unshuffled Tiles, 4 players

	@BeforeEach
	void setUp() throws Exception {
		
		shuffled3P = new GameControl( Arrays.asList(new HumanPlayer("A"),
													 new HumanPlayer("B"),
													 new HumanPlayer("C")), 
									   true);

		unshuffled3P = new GameControl( Arrays.asList(new HumanPlayer("A"),
												 new HumanPlayer("B"),
												 new HumanPlayer("C")), 
								  false);

		unshuffled4P = new GameControl( Arrays.asList(new HumanPlayer("A"),
												 new HumanPlayer("B"),
												 new HumanPlayer("C"),
												 new HumanPlayer("D")),
								  false);
														
	}

	@Test
	void testDealTilesUnshuffled() {
		// an unshu bag with 3 players will not determine the first player
		assertNull(unshuffled3P.dealTiles());
		// we know an unshu bag with 4 players
		// will result in the first player make the first move 
		assertEquals(0, (int) unshuffled4P.dealTiles());
	}
	
	
	/**
	 * Try to come up with a stronger test.
	 */
	@Test
	void testDealTilesShuffled() {
		// should be equal because when GameControl is created ,
		// order of tiles is determined.
		assertEquals(shuffled3P.dealTiles(), shuffled3P.dealTiles());
//		System.out.println(shu3.getTiles());
	}
	
	@Test
	void testDrawAtile() {
		List<Tile> tiles = shuffled3P.getTiles();

		// the nth newly drew tile is the same as the nth tile in tiles
		for (int i = 0; i < 36; i++) {
			Tile t = shuffled3P.drawATile();
			assertTrue(t.stringTile().equals(tiles.get(i).stringTile()));
		}
		
		// after draw 36 tiles, tilesCopy should be a list of 36 null
		List<Tile> tilesCopy = shuffled3P.getTilesCopy();
		for (int i = 0; i < tilesCopy.size(); i++) {
			assertNull(tilesCopy.get(i));
		}
		
	}

}
