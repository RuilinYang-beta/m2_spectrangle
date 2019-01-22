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
		assertTrue(b.getOneDimIndex(corner1).equals(0));
		assertTrue(b.getOneDimIndex(corner2).equals(25));
		assertTrue(b.getOneDimIndex(corner3).equals(35));
		
		Integer[] edge1 = {2, -2};
		Integer[] edge2 = {3, 3};
		Integer[] edge3 = {5, -1};
		assertTrue(b.getOneDimIndex(edge1).equals(4));
		assertTrue(b.getOneDimIndex(edge2).equals(15));
		assertTrue(b.getOneDimIndex(edge3).equals(29));
		
		Integer[] insider1 = {3, -1};
		Integer[] insider2 = {4, 2};
		Integer[] insider3 = {5, -4};
		assertTrue(b.getOneDimIndex(insider1).equals(11));
		assertTrue(b.getOneDimIndex(insider2).equals(22));
		assertTrue(b.getOneDimIndex(insider3).equals(26));	
	}
	
	@Test
	void testGetRCIdx() {
		Integer[] arr1 = {1, 0}; // 2
		Integer[] arr2 = {4, 1}; // 21
//		System.out.println(Arrays.deepToString(b.getRCIndex(2)));
//		System.out.println(Arrays.deepToString(b.getRCIndex(21)));
		assertTrue(Arrays.equals(b.getRCIndex(2), arr1));
		assertTrue(Arrays.equals(b.getRCIndex(21), arr2));
		
		int i;
		for (int j = 0; j < 10; j++) {
			i = (int) (Math.random() * ( 35 ));
			assertTrue(b.getOneDimIndex(b.getRCIndex(i)).equals(i));
		}
	}
	
	@Test
	void testIsLegalIdx() {
		int[] legals = {0, 3, 9, 18, 27, 35};
		for (int i = 0; i < legals.length; i++) {
			assertTrue(Board.isLegalIdx(legals[i]));
			assertTrue(Board.isLegalIdx(b.getRCIndex(legals[i])));
		}
		
		int[] illegals = {99, 563, 187, 36, -1, -10};
		for (int i = 0; i < illegals.length; i++) {
			assertFalse(Board.isLegalIdx(illegals[i]));
			assertNull(b.getRCIndex(illegals[i]));
		}
	}

	
}
