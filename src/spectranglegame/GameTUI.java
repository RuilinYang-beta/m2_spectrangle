package spectranglegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;
import players.*;

import javax.swing.text.html.HTMLDocument.HTMLReader.CharacterAction;
import java.util.Scanner;

public class GameTUI {
	
	// =========== Static Fields: only for test purpose ===========
	private static final List<Integer> bonuses =       Arrays.asList(1, 1, 3, 1, 1, 1, 1, 1, 1, 1, 2, 4, 1, 4, 2, 1, 1, 1, 1, 1, 4, 1, 1, 1, 1, 1, 3, 1, 1, 1, 2, 1, 1, 1, 3, 1);
    private static List<Integer> values =        Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static List<Character> vertical =    Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static List<Character> left =        Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    private static List<Character> right =       Arrays.asList(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);

    private static final String FIELD = "Field?";
    private static final String FIELD_WRONG = "Wrong Field";
    private static final String TILE = "Tile?";
    private static final String ROTATION = "Rotation?";
    
    // =========== Instance Field ===========
	private GameControl control;
	private Board board;
	private Boolean chosenFieldFacingUp = null;
//	private Boolean chosenFieldFacingUp = true;
	private Map<Player, Tile[]> mapPlayersTiles = null;
	
	// =========== Constructor ===========
	public GameTUI(GameControl gc, Board bd) {
//	public GameTUI(GameControl gc) {
		this.control = gc;
		this.board = bd;
	}

	// ========================= Ask User Input =========================
    /**
     * Ask user to give a valid field, if not valid, keep asking until valid.
     * @param p Ask this player for input.
     * @return The index of the player's choice.
     */
    public Integer askField(Player p, Board b, boolean isFirstMove) {
    	Integer chosenFieldIdx = null;
    	boolean isLegalField = false;
    	
    	// Later TUI will send this board string to PlayerClient.
		// For now TUI temporarily print board at TUI console.
		printBoardDynamic(board);
		
    	do {
    		// later this will be sent to p via socket, and p will parse it 
    		System.out.println(FIELD); 

    		int returnVal = p.chooseFieldIdx();
    		
    		System.out.println(returnVal);
    		
    		// TUI doing the sanitary check, if not sanitary, keep asking until user give legal fieldIdx.
    		boolean cond0 = Board.isLegalIdx(returnVal);
    		boolean cond1 = !Board.isBonusField(returnVal); // applies to first move only
    		boolean cond2 = board.fieldIsEmpty(returnVal);
    		isLegalField = (isFirstMove) ? (cond0 && cond1 && cond2) : (cond0 && cond2);
    		
    		if (isLegalField) {
    			chosenFieldIdx = returnVal;
    			chosenFieldFacingUp = Board.isFacingUp(chosenFieldIdx);
    		} else {
    			// later this will be sent to p via socket, and p will parse it 
    			System.out.println(FIELD_WRONG);
    		}
    	} while (chosenFieldIdx == null);
    	
    	return chosenFieldIdx;
    }
    
    /**
     * Ask user to choose a Tile and its rotation.
     * @param p
     * @return
     */
    public Tile askTileAndRotation(Player p) {
    	Tile baseTile = askTile(p);
    	Tile chosenTileAndRotation = askRotation(p, baseTile);
    	return chosenTileAndRotation;
    }
    
    /**
     * Helper function of askTileAndRotation, if not valid, keep asking until valid.
     * @param p Ask this player for input.
     * @return The Tile (cloned) of the player's choice. 
     */
    private Tile askTile(Player p) {
    	
    	// later this will be sent to p via socket, and p will parse it 
		System.out.println(TILE); 
		
		ArrayList<Tile> nonNulls = getNonNullTiles(p);
		// Later TUI should send this String representation to PlayerClient
		// For now TUI print this String at TUI's console.
		showMultiTilesUp(nonNulls);
		
		int nonNullTileIdx = p.chooseTileIdx(nonNulls.size());
		
		return nonNulls.get(nonNullTileIdx);
    }
    

    /**
     * Helper function of askTileAndRotation(Player p).
     * Generate 3 different rotation from the base Tile, ask user to choose one rotation.
     * @param p The player to ask
     * @param t The tile with rotation 0, as the base Tile
     * @return
     */
    private Tile askRotation(Player p, Tile baseT) {
    	// Generate 3 different rotation from the base Tile,
    	ArrayList<Tile> allRotation = new ArrayList<>();

    	if (chosenFieldFacingUp) {
    		// fill 3 rotation facing up
    		allRotation.addAll( Arrays.asList(baseT, 
    										  baseT.rotateTileTwice(), 
    										  baseT.rotateTileFourTimes()
    										  )
    				);
    		
    		// Later TUI should send this String representation to PlayerClient
    		// For now TUI print this String at TUI's console.
    		showMultiTilesUp(allRotation);
    		
    		// then, set chosenFieldFacingUp back to null;
    		chosenFieldFacingUp = null;
    	} else if (!chosenFieldFacingUp) {
    		// fill 3 rotation facing down
    		System.out.println("showMultiTilesDown not implemented yet.");
    		
    		// then, set chosenFieldFacingUp back to null;
    		chosenFieldFacingUp = null;
    	} else {
    		System.out.println("Error! chosen field direction unknow!");
    	}
    	
    	int rotationIdx = p.chooseRotationIdx();
    	
    	return allRotation.get(rotationIdx);
    }

    
    // ========================= Display Tiles Related =========================
    private ArrayList<Tile> getNonNullTiles(Player p){
    	Tile[] tilesArray =  p.getTiles();
    	
    	// get non-null Tile and store them in a ArrayList (such that there's no null in between)
		ArrayList<Tile> nonNullTiles = new ArrayList<>();
		for (int i = 0; i < tilesArray.length; i++) {
			if (tilesArray[i] != null) {
				nonNullTiles.add(tilesArray[i]);
			}
		}
		return nonNullTiles;
    }
    
    private void showMultiTilesUp(ArrayList<Tile> nonNullTiles) {
    	// use different showing function, based on different number of non-null tile
		int numNonNullTile = nonNullTiles.size();
		switch (numNonNullTile) { 
			case 0:
				System.out.println("Player has 4 null in Tile[].");
				break;
			case 1:
				showOneTileUp(nonNullTiles);
				break;
			case 2: 
				showTwoTilesUp(nonNullTiles);
				break;
			case 3:
				showThreeTilesUp(nonNullTiles);
				break;
			case 4:
				showFourTilesUp(nonNullTiles);
				break;
			default: 
				System.out.println("Something wrong, you shouldn't reach here.");
		}
		
		
    }
       
	private void showOneTileUp(List<Tile> tiles) {
		Tile t0 = tiles.get(0);
		String val0 = "" + t0.getValue();
		String lef0 = "" + t0.getLeft();
		String rig0 = "" + t0.getRight();
		String ver0 = "" + t0.getVertical();
		
		String template =
                "    / \\    \n" +
                "   / " + val0 + " \\   \n" +
                "  / " + lef0 + " " + rig0 + " \\  \n" +
                " /   " + ver0 + "   \\ \n" +
                " --------- \n"  ;
		
		System.out.println(template);
	}
	
	private void showTwoTilesUp(List<Tile> tiles) {
		Tile t0 = tiles.get(0);
		Tile t1 = tiles.get(1);
		
		String val0 = "" + t0.getValue();		String val1 = "" + t1.getValue();
		String lef0 = "" + t0.getLeft();		String lef1 = "" + t1.getLeft();
		String rig0 = "" + t0.getRight();		String rig1 = "" + t1.getRight();
		String ver0 = "" + t0.getVertical();	String ver1 = "" + t1.getVertical();
		
		String template2 =
                "    / \\    " + "    / \\    \n" +
                "   / " + val0 + " \\   " +"   / " + val1 + " \\   \n" +
                "  / " + lef0 + " " + rig0 + " \\  " + "  / " + lef1 + " " + rig1 + " \\  \n" +
                " /   " + ver0 + "   \\ " + " /   " + ver1 + "   \\ \n" +
                " --------- " + " --------- \n"  ;
		System.out.println(template2);
		
	}
	
	private void showThreeTilesUp(List<Tile> tiles) {
		Tile t0 = tiles.get(0);
		Tile t1 = tiles.get(1);
		Tile t2 = tiles.get(2);
		
		String val0 = "" + t0.getValue();		String val1 = "" + t1.getValue();
		String lef0 = "" + t0.getLeft();		String lef1 = "" + t1.getLeft();
		String rig0 = "" + t0.getRight();		String rig1 = "" + t1.getRight();
		String ver0 = "" + t0.getVertical();	String ver1 = "" + t1.getVertical();
		
		String val2 = "" + t2.getValue();
		String lef2 = "" + t2.getLeft();
		String rig2 = "" + t2.getRight();
		String ver2 = "" + t2.getVertical();
		
		String template3 =
				// make sure each line is of the same total length
				// for GameTUI to display 3 tiles horizontally
                "    / \\    " + "    / \\    " + "    / \\    \n" +
                "   / " + val0 + " \\   " +"   / " + val1 + " \\   " + "   / " + val2 + " \\   \n" +
                "  / " + lef0 + " " + rig0 + " \\  " + "  / " + lef1 + " " + rig1 + " \\  " + "  / " + lef2 + " " + rig2 + " \\  \n" +
                " /   " + ver0 + "   \\ " + " /   " + ver1 + "   \\ " + " /   " + ver2 + "   \\ \n" +
                " --------- " + " --------- " + " --------- \n"  ;
		System.out.println(template3);
		
	}
	
	private void showFourTilesUp(List<Tile> tiles) {
		Tile t0 = tiles.get(0);
		Tile t1 = tiles.get(1);
		Tile t2 = tiles.get(2);
		Tile t3 = tiles.get(3);
		
		String val0 = "" + t0.getValue();		String val1 = "" + t1.getValue();
		String lef0 = "" + t0.getLeft();		String lef1 = "" + t1.getLeft();
		String rig0 = "" + t0.getRight();		String rig1 = "" + t1.getRight();
		String ver0 = "" + t0.getVertical();	String ver1 = "" + t1.getVertical();
		
		String val2 = "" + t2.getValue();		String val3 = "" + t3.getValue();
		String lef2 = "" + t2.getLeft();		String lef3 = "" + t3.getLeft();
		String rig2 = "" + t2.getRight();		String rig3 = "" + t3.getRight();
		String ver2 = "" + t2.getVertical();	String ver3 = "" + t3.getVertical();
		
		String template4 =
                "    / \\    " + "    / \\    " + "    / \\    " + "    / \\    \n" +
                "   / " + val0 + " \\   " +"   / " + val1 + " \\   " + "   / " + val2 + " \\   " + "   / " + val3 + " \\   \n" +
                "  / " + lef0 + " " + rig0 + " \\  " + "  / " + lef1 + " " + rig1 + " \\  " + "  / " + lef2 + " " + rig2 + " \\  " + "  / " + lef3 + " " + rig3 + " \\  \n" +
                " /   " + ver0 + "   \\ " + " /   " + ver1 + "   \\ " + " /   " + ver2 + "   \\ " + " /   " + ver3 + "   \\ \n" +
                " --------- " + " --------- " + " --------- " + " --------- \n";
		System.out.println(template4);
	}
	
    // ========================= Print the board =========================
    public void printBoardDynamic(Board b) {
    	System.out.println(getBoardString(b.getValuesOnBoard(), b.getVerticalOnBoard(), b.getLeftOnBoard(), b.getRightOnBoard()));
    }

    
    public void printBoardStatic() {
    	System.out.println(getBoardString(values, vertical, left, right));
    }
    
	
    /* if a field is empty         =>      its index showed, its bonus (if not 1) showed
     * if a field has a tile on it =>      its index hidden, its bonus (if not 1) showed,
     *  								   its value showed.
     */
    public String getBoardString(List<Integer> values, List<Character> vertical, List<Character> left, List<Character> right){
        // All lists should have exactly 36 items.
        if(!Stream.of(values, vertical, left, right).parallel().map(List::size).allMatch(n -> n == 36)){
            throw new IllegalArgumentException("Input lists should all have 36 items, one for each field on the board.");
        }
        String template = "\n" +
                "                               ^\n" +
                "                              / \\\n" +
                "                             / {f0b} \\\n" +
                "                            /{f00}{f0v} {f01}\\\n" +
                "                           /   {f02}   \\\n" +
                "                          /---------\\\n" +
                "                         / \\   {f22}   / \\\n" +
                "                        / {f1b} \\{f20}{f2v} {f21}/ {f3b} \\\n" +
                "                       /{f10}{f1v} {f11}\\ {f2b} /{f30}{f3v} {f31}\\\n" +
                "                      /   {f12}   \\ /   {f32}   \\\n" +
                "                     /---------X---------\\\n" +
                "                    / \\   {f52}   / \\   {f72}   / \\\n" +
                "                   / {f4b} \\{f50}{f5v} {f51}/ {f6b} \\{f70}{f7v} {f71}/ {f8b} \\\n" +
                "                  /{f40}{f4v} {f41}\\ {f5b} /{f60}{f6v} {f61}\\ {f7b} /{f80}{f8v} {f81}\\\n" +
                "                 /   {f42}   \\ /   {f62}   \\ /   {f82}   \\\n" +
                "                /---------X---------X---------\\\n" +
                "               / \\   {f102}   / \\   {f122}   / \\   {f142}   / \\\n" +
                "              / {f9b} \\{f100}{f10v} {f101}/ {f11b} \\{f120}{f12v} {f121}/ {f13b} \\{f140}{f14v} {f141}/ {f15b} \\\n" +
                "             /{f90}{f9v} {f91}\\ {f10b} /{f110}{f11v} {f111}\\ {f12b} /{f130}{f13v} {f131}\\ {f14b} /{f150}{f15v} {f151}\\\n" +
                "            /   {f92}   \\ /   {f112}   \\ /   {f132}   \\ /   {f152}   \\\n" +
                "           /---------X---------X---------X---------\\\n" +
                "          / \\   {f172}   / \\   {f192}   / \\   {f212}   / \\   {f232}   / \\\n" +
                "         / {f16b} \\{f170}{f17v} {f171}/ {f18b} \\{f190}{f19v} {f191}/ {f20b} \\{f210}{f21v} {f211}/ {f22b} \\{f230}{f23v} {f231}/ {f24b} \\\n" +
                "        /{f160}{f16v} {f161}\\ {f17b} /{f180}{f18v} {f181}\\ {f19b} /{f200}{f20v} {f201}\\ {f21b} /{f220}{f22v} {f221}\\ {f23b} /{f240}{f24v} {f241}\\\n" +
                "       /   {f162}   \\ /   {f182}   \\ /   {f202}   \\ /   {f222}   \\ /   {f242}   \\\n" +
                "      /---------X---------X---------X---------X---------\\\n" +
                "     / \\   {f262}   / \\   {f282}   / \\   {f302}   / \\   {f322}   / \\   {f342}   / \\\n" +
                "    / {f25b} \\{f260}{f26v} {f261}/ {f27b} \\{f280}{f28v} {f281}/ {f29b} \\{f300}{f30v} {f301}/ {f31b} \\{f320}{f32v} {f321}/ {f33b} \\{f340}{f34v} {f341}/ {f35b} \\\n" +
                "   /{f250}{f25v} {f251}\\ {f26b} /{f270}{f27v} {f271}\\ {f28b} /{f290}{f29v} {f291}\\ {f30b} /{f310}{f31v} {f311}\\ {f32b} /{f330}{f33v} {f331}\\ {f34b} /{f350}{f35v} {f351}\\\n" +
                "  /   {f252}   \\ /   {f272}   \\ /   {f292}   \\ /   {f312}   \\ /   {f332}   \\ /   {f352}   \\\n" +
                " /-----------------------------------------------------------\\\n";

        // Fill in bonus values
        template = listToMap(bonuses).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "b}", elem.getValue() != 1 ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        // Fill in values
        template = listToMap(values).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "v}", elem.getValue() != null ? String.format("%2d", elem.getValue()) : String.format("%2d", elem.getKey())), (s, s2) -> s);

        // Fill in left colors
        template = listToMap(left).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "0}", elem.getValue() != null ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        // Fill in right colors
        template = listToMap(right).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "1}", elem.getValue() != null ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        // Fill in vertical colors
        template = listToMap(vertical).entrySet().stream().reduce(template, (prev, elem) -> prev.replace("{f" + elem.getKey() + "2}", elem.getValue() != null ? String.valueOf(elem.getValue()) : " "), (s, s2) -> s);

        return template;
    }

    private <K> Map<Integer, K> listToMap(List<K> inputList){
        Map<Integer, K> indexed_values = new HashMap<>();
        for(int i = 0; i < values.size(); i++){ indexed_values.put(i, inputList.get(i)); }
        return indexed_values;
    }
	
    // ========================= Main =========================
    public static void main(String[] args) {
    	
    	// test of askField, askTileAndRotation:
    	Tile[] tiles = {new Tile(3, "RGB")};
    	List<Player> pl = Arrays.asList(new HumanPlayer("player", tiles));
    	
		GameTUI temp = new GameTUI(new GameControl(pl, true), 
								   new Board());
		
		Tile[] tiles1 = {new Tile(3, "RGB"), null, null, null};
		Tile[] tiles2 = {new Tile(3, "RGB"), null, new Tile(1, "HEY")};
		Tile[] tiles3 = {new Tile(3, "RGB"), new Tile(1, "HEY"), null, new Tile(2, "OOP")};
		Tile[] tiles4 = {new Tile(3, "RGB"), new Tile(1, "HEY"), new Tile(2, "OOP"), new Tile(4, "WHO")};
		
		HumanPlayer p = new HumanPlayer("player", tiles2);
		temp.askField(p, new Board(), false);
		temp.askTileAndRotation(p);
		
		
		
	}

}
