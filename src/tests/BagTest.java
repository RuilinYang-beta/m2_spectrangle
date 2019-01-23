
package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spectranglegame.Bag;
import spectranglegame.Tile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

class BagTest {
	
	Bag t;
	Tile tile;
	@BeforeEach
	void setUp() throws Exception {
		 t = new Bag(true);
		 tile = new Tile(4, "PBP");
	}

	@Test
	void testGetIndex() {
		assertTrue(t.getIndex(tile) >= 0 && t.getIndex(tile) < 36);
	}
	
	@Test
	void testGetTile() {
		assertTrue(t.getTile(0) instanceof Tile);
		assertTrue(t.isValidTile(tile));
		tile = new Tile(6, "RGB");
		assertFalse(t.isValidTile(tile));
	}
}

