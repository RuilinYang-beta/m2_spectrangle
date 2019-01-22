package tests;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import players.HumanPlayer;

import java.util.Arrays;

import spectranglegame.*;


class GameControlTest {
	
	GameControl shu3;   // A GameControl obj with shuffled Tiles, 3 players
	GameControl unshu3; // A GameControl obj with unshuffled Tiles, 3 players
	GameControl unshu4; // A GameControl obj with unshuffled Tiles, 4 players

	@BeforeEach
	void setUp() throws Exception {
		
		shu3 = new GameControl( Arrays.asList(new HumanPlayer("A"),
													 new HumanPlayer("B"),
													 new HumanPlayer("C")), 
									   true);

		unshu3 = new GameControl( Arrays.asList(new HumanPlayer("A"),
												 new HumanPlayer("B"),
												 new HumanPlayer("C")), 
								  false);

		unshu4 = new GameControl( Arrays.asList(new HumanPlayer("A"),
												 new HumanPlayer("B"),
												 new HumanPlayer("C"),
												 new HumanPlayer("D")),
								  false);
														
	}

	@Test
	void testDealTilesUnshuffled() {
		// an unshu bag with 3 players will not determine the first player
		assertNull(unshu3.dealTiles());
		// we know an unshu bag with 4 players
		// will result in the first player make the first move 
		assertEquals(0, (int) unshu4.dealTiles());
	}
	
	
	/**
	 * Try to come up with a stronger test.
	 */
	@Test
	void testDealTilesShuffled() {
		// should be equal because when GameControl is created ,
		// order of tiles is determined.
		assertEquals(shu3.dealTiles(), shu3.dealTiles());
//		System.out.println(shu3.getTiles());
	}

}
