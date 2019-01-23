package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spectranglegame.Bag;
import spectranglegame.Tile;

class BagTest {
	
	Bag t;
	Tile tile;
	@BeforeEach
	void setUp() throws Exception {
		 t = new Bag(true);
	}

	@Test
	void testRemove() {
		for(int i = 0; i < 7; i ++) {
//			tile = t.randomTile();
			t.removeTile(tile);
		}
//			tile = t.randomTile();
//			t.removeTile(tile);
//			tile = t.randomTile();
//			t.removeTile(tile);
//			tile = t.randomTile();
//			t.removeTile(tile);
			t.showtiles();
	}
}
