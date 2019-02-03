package tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import spectranglegame.*;


class BoardTest {
	
	private Board b;
	
	@BeforeEach
	void setUp() throws Exception {
		b = new Board();
	}

	@Test
	void testGetOneDimIdx() {
		Integer[] corner1 = {0, 0};
		Integer[] corner2 = {5, -5};
		Integer[] corner3 = {5, 5};
		assertTrue(Board.getOneDimIndex(corner1).equals(0));
		assertTrue(Board.getOneDimIndex(corner2).equals(25));
		assertTrue(Board.getOneDimIndex(corner3).equals(35));
		
		Integer[] edge1 = {2, -2};
		Integer[] edge2 = {3, 3};
		Integer[] edge3 = {5, -1};
		assertTrue(Board.getOneDimIndex(edge1).equals(4));
		assertTrue(Board.getOneDimIndex(edge2).equals(15));
		assertTrue(Board.getOneDimIndex(edge3).equals(29));
		
		Integer[] insider1 = {3, -1};
		Integer[] insider2 = {4, 2};
		Integer[] insider3 = {5, -4};
		assertTrue(Board.getOneDimIndex(insider1).equals(11));
		assertTrue(Board.getOneDimIndex(insider2).equals(22));
		assertTrue(Board.getOneDimIndex(insider3).equals(26));	
	}
	
	@Test
	void testGetRCIdx() {
		Integer[] arr1 = {1, 0}; // 2
		Integer[] arr2 = {4, 1}; // 21
		assertTrue(Arrays.equals(Board.getRCIndex(2), arr1));
		assertTrue(Arrays.equals(Board.getRCIndex(21), arr2));
		
		int i;
		for (int j = 0; j < 10; j++) {
			i = (int) (Math.random() * ( 35 ));
			assertTrue(Board.getOneDimIndex(Board.getRCIndex(i)).equals(i));
		}
	}
	
	@Test
	void testIsLegalIdx() {
		int[] legals = {0, 3, 9, 18, 27, 35};
		for (int i = 0; i < legals.length; i++) {
			assertTrue(Board.isLegalIdx(legals[i]));
			assertTrue(Board.isLegalIdx(Board.getRCIndex(legals[i])));
		}
		
		int[] illegals = {99, 563, 187, 36, -1, -10};
		for (int i = 0; i < illegals.length; i++) {
			assertFalse(Board.isLegalIdx(illegals[i]));
			assertNull(Board.getRCIndex(illegals[i]));
		}
	}

	@Test
	void testGetSurroundingInfoAllNeighbors() {
		Tile t0 = new Tile(1, "TVT"); // pseudo colors only for test
		Tile t1 = new Tile(1, "LTT");
		Tile t3 = new Tile(1, "TTR");
		
		b.setTile(0, t0);
		b.setTile(1, t1);
		b.setTile(3, t3);
		
		Character[] srd = b.getSurroundingInfo(2); // surroundings of idx 2
		
		assertTrue(srd[0].equals('D'));
		assertTrue(srd[1].equals(t0.getVertical())); // vertical boarder color
		assertTrue(srd[2].equals(t1.getRight()));  // left boarder color
		assertTrue(srd[3].equals(t3.getLeft()));  // right board color
	}
	
	@Test
	void testGetSurroundingInfoWithNullNeighbor() {
		Tile t0 = new Tile(1, "TVT"); // pseudo colors only for test
		Tile t1 = new Tile(1, "LTT");
		
		b.setTile(0, t0);
		b.setTile(1, t1);
		
		Character[] srd = b.getSurroundingInfo(2); // surroundings of idx 2
		
		assertTrue(srd[0].equals('D'));
		assertTrue(srd[1].equals(t0.getVertical())); // vertical boarder color
		assertTrue(srd[2].equals(t1.getRight()));  // left boarder color
		assertNull(srd[3]);  // right board color
	}
	
	@Test
	void testGetSurroundingInfoAllNullNeighbor() {
		Character[] srd = b.getSurroundingInfo(0);
		
		assertTrue(srd[0].equals('U'));
		assertNull(srd[1]); // vertical boarder color
		assertNull(srd[2]);  // left boarder color
		assertNull(srd[3]);  // right board color
	}
	
	@Test
	void testIsEmpty() {
		b.resetBoard();
		int i = (int) (Math.random() * ( 35 ));
		assertTrue(b.fieldIsEmpty(i));
	}
	
	@Test
	void testBonusField() {
		assertTrue(Board.isBonusField(2));
		Tile t = new Tile(3,"RGB");
		int i = (int) (Math.random() * ( 35 ));
		b.setTile(i, t, "radu");
	}
	
	@Test
	void testBoardisFull() {
		Tile t = new Tile(3,"RGB");
		for (int i = 0; i < 36; i++) {
			b.setTile(i, t);
		}
		assertTrue(b.boardIsFull());
	}
}
