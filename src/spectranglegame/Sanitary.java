package spectranglegame;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import players.Player;
import players.HumanPlayer;

public class Sanitary {
	
	
	/**
	 * Generate a map of all possible move: Map<String, Set<Integer>>:
	 * String: the tile encoding (including rotation)
	 * Set<Integer>: all the possible fields that the tile (with rotation) can be placed on.
	 * @requires 
	 */
	public static Map<String, Set<Integer>> generateAllPossibleMoves(Board b, Player p){
		Map<String, Set<Integer>> allPM = new HashMap<>();
		
		List<Tile> allTiles = p.getNonNullTiles();
		List<Tile> allRotations = allRotationsFromAllTiles(allTiles);
		
		// when there's already some Tile on board, and Player has Tile in hand 
		// can accommodate user's Tile is Joker, or NeighborTile on board is Joker.
		ArrayList<Integer> emptyWNT = b.getEmptyFieldsWithNeighborTile();
		
		for (Tile r : allRotations) {
			
			Set<Integer> matchFields = new HashSet<>();
			
			for (Integer idx : emptyWNT) {
				if (matches(b, idx, r)) {
					matchFields.add(idx);
				}
			}
			
			if (!matchFields.isEmpty()) {
				allPM.put(r.stringTile(), matchFields);	
			}
			
		}
		return allPM;
	}
	
	private static List<Tile> allRotationsFromAllTiles(List<Tile> tiles){
		ArrayList<Tile> result = new ArrayList<>();
		
		for (Tile t : tiles) {
			result.addAll(Arrays.asList(t,                   // rotation 0
							t.rotateTileOnce(), 			 // rotation 1
							t.rotateTileTwice(), 			 // rotation 2
							t.rotateTileOnce().rotateTileTwice(),   // rotation 3
							t.rotateTileFourTimes(),				// rotation 4
							t.rotateTileOnce().rotateTileFourTimes()));  // rotation 5
		}
		
		return result;
		
	}
	
	/**
	 * For one empty field with at least one neighboring tile, and a specific Tile, 
	 * Return true if the Tile can be put on the Field.
	 * @param b The board of concern.
	 * @param t The Tile you want to place on the empty field, can be any rotation
	 * @return true If the tile match the surroundings.
	 */
	/*
	 * @requires (srd[1] != null) || (srd[2] != null) || (srd[3] != null);
	 */
	private static boolean matches(Board b, Integer i, Tile t) {
		
		// Joker can be placed anywhere as long as there's a neighboring Tile
		if (t.isJoker()) {
			return true;
		}
		
		Character[] srd = b.getSurroundingInfo(i);
		
		// direction mismatch, return false;
		if ( ((srd[0] == 'U') && (!t.isFacingUp())) || ((srd[0] == 'D') && (t.isFacingUp())) ) {
			return false;
		}
		
		// (has a vertical neighbor) && (vertical neighborTile is not Joker) && (vertical color mismatch)
		if ((srd[1] != null) && (srd[1] != 'W') && (t.getVertical() != srd[1])) {
			return false;
		}
		
		// (has a left neighbor) && (vertical neighborTile is not Joker) && (left color mismatch)
		if ((srd[2] != null) && (srd[2] != 'W') && (t.getLeft() != srd[2])) {
			return false;
		}
		
		// (has a right neighbor) && (vertical neighborTile is not Joker) && (right color mismatch)
		if ((srd[3] != null) && (srd[3] != 'W') && (t.getRight() != srd[3])) {
			return false;
		}
		
		return true;
	}
	
	public static Map<String, Set<Integer>> optionsForBaseTile(Map<String, Set<Integer>> all, Tile t){
		String base = t.stringTile().substring(1);
		Map<String, Set<Integer>> forTile = new HashMap<String, Set<Integer>>();
		
		for (Map.Entry<String, Set<Integer>> entry : all.entrySet()) {
			if (entry.getKey().contains(base)) {
				forTile.put(entry.getKey(), entry.getValue());
			}
		}
		
		return forTile;
	}

	public static Map<String, Set<Integer>> optionsForBaseTileAndField(Map<String, Set<Integer>> all, Tile t, Integer idx){
		String base = t.stringTile().substring(1);
		Map<String, Set<Integer>> forTileField = new HashMap<String, Set<Integer>>();
		
		for (Map.Entry<String, Set<Integer>> entry : all.entrySet()) {
			if ((entry.getKey().contains(base)) && (entry.getValue().contains(idx))) {
				forTileField.put(entry.getKey(), entry.getValue());
			}
		}
		
		return forTileField;
	}
	
	public static void main(String[] args) {
		// testing
		Board b = new Board();
		b.setTile(0, new Tile(1, "RGR")); 
		b.setTile(1, new Tile(1, "BYB"));    b.setTile(3, new Tile(1, "YPR"));
		b.setTile(4, new Tile(1, "GGG"));    b.setTile(6, new Tile(1, "GYG"));     b.setTile(8, new Tile(1, "BBR"));
		b.setTile(10, new Tile(1, "BGP"));   b.setTile(14, new Tile(1, "YBR"));
//		b.setTile(17, new Tile(1, "RGR"));   b.setTile(18, new Tile(1, "ZZZ"));    b.setTile(19, new Tile(1, "GYP"));  b.setTile(20, new Tile(1, "ZZZ"));  b.setTile(21, new Tile(1, "YGY"));  b.setTile(22, new Tile(1, "ZZZ"));  b.setTile(23, new Tile(1, "PPP"));
//		b.setTile(26, new Tile(1, "PBB"));   b.setTile(34, new Tile(1, "YYY"));
		for (int i = 16; i < 36; i++) {
			b.setTile(i, new Tile(1, "ZZZ"));
		}
		
		// Empty fields: [2, 5, 7, 9, 11, 12, 13, 15]
		// with 3 non-null neighbor Tile
			// 2: new Tile(1, "BGR"); 5: new Tile(1, "GYG"); 7: new Tile(1, "GPR")
		// with 2 neighbor Tile
			// 9: new Tile(1, "PZX"), 11: new Tile(1, "XZB"), 13: new Tile(1, "RZX"), 15: new Tile(1, "XZY")
		// with 1 neighbor Tile
			// 12: new Tile(1, "XYX")
		
		Tile[] tiles1 = {new Tile(3, "BGR"), new Tile(3, "GYG"), new Tile(3, "GPR"), null};
//		Tile[] tiles2 = {new Tile(3, "RGB"), null, new Tile(1, "HEY"), null};
//		Tile[] tiles3 = {new Tile(3, "RGB"), new Tile(1, "HEY"), null, new Tile(2, "ABC")};
//		Tile[] tiles4 = {new Tile(3, "RGB"), new Tile(1, "HEY"), new Tile(2, "OOP"), new Tile(4, "WHO")};
		
		// Test of dealTiles
		Player A = new HumanPlayer("A", tiles1);
		Player B = new HumanPlayer("B");
		Player C = new HumanPlayer("C");
		Player D = new HumanPlayer("D");
		
		Display.printBoardDynamic(b);
		
		Map<String, Set<Integer>> all = generateAllPossibleMoves(b, A);
		for(Map.Entry<String, Set<Integer>> e: all.entrySet()) {
			System.out.print(e.getKey() + " : ");
			System.out.println(e.getValue());
		}
		System.out.println(generateAllPossibleMoves(b, A).size());
		
		Map<String, Set<Integer>> forABaseT = optionsForBaseTile(all, new Tile("0BGZ3"));
		for(Map.Entry<String, Set<Integer>> e: forABaseT.entrySet()) {
			System.out.print(e.getKey() + " : ");
			System.out.println(e.getValue());
		}
		System.out.println(forABaseT.size());
		
	}
}
