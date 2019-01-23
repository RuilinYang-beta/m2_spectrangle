package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import spectranglegame.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Tiletest1 {

	Tile t = new Tile(3,"RGB");

	@BeforeEach
	void setUp() throws Exception {
		t = new Tile(3,"RGB");
	}

	@Test
	public void testConstructor() {
		assertEquals("0RGB3", t.stringTile());
	}
	
	@Test 
	public void testRotate() {
		t.rotateTile();
		assertEquals("1RGB3", t.stringTile());
		assertEquals("R","" + t.getRight());
		assertEquals("B","" + t.getVertical());
		assertEquals("G","" + t.getLeft());
		t.rotateTile();
		assertEquals("2RGB3", t.stringTile());
		assertEquals("B","" + t.getRight());
		assertEquals("R","" + t.getVertical());
		assertEquals("G","" + t.getLeft());
		t.rotateTile();
		assertEquals("3RGB3", t.stringTile());
		assertEquals("BGR","" + t.getRight() + t.getVertical() +  t.getLeft());
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
	public void testShowTileAllUpRotation() {
		t.showTile();    // show rotation 0
		
		t.rotateTile();
		t.rotateTile();
		t.showTile();    // show rotation 2
		
		t.rotateTile();
		t.rotateTile();
		t.showTile();    // show rotation 4
		
	}
	
	@Test
	public void testShowTileAllDownRotation() {
		;
	}

}
