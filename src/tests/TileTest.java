package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import spectranglegame.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TileTest {

	Tile t;

	@BeforeEach
	void setUp() throws Exception {
		t = new Tile(3,"RGB");
	}

	@Test
	public void testConstructor() {
		assertEquals("0RGB3", t.stringTile());
	}
	
	@Test
	public void testValue() {
		assertEquals(3,t.getValue());
	}
	
	@Test
	public void testToString() {
		assertEquals("RGB3", t.toString());
	}
	
	@Test
	void isJoker() {
		Tile t = new Tile(1,"WWW");
		assertTrue(t.isJoker());
	}
	@Test
	public void testShowTile() {
		// You have to examine the console visually.
		t.showTile();						// rotation 0
		t.rotateTileOnce().showTile();		// rotation 1
		t.rotateTileTwice().showTile();		// rotation 2
		t.rotateTileOnce().rotateTileTwice().showTile();  		// rotation 3
		t.rotateTileFourTimes().showTile();;					// rotation 4
		t.rotateTileOnce().rotateTileFourTimes().showTile();    // rotation 5
	}

}
