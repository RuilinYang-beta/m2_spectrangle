package spectranglegame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.*;
import java.util.Random;
import java.util.Set;
import java.util.Observer;
import java.util.Observable;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import players.*;
import utils.*;

public class GameControl  extends Thread implements Observer{

	// ======================== Fields ========================
	// ---------- old ones ----------

	private PlayerClient tui;

	// ---------- networking ----------
	private List<Socket> sockets = new ArrayList<>();     // sockets.size() == listPlayers.size()
	private List<BufferedReader> ins = new ArrayList<>();
	private List<BufferedWriter> outs = new ArrayList<>();
	
	// ---------- Models ----------
	private List<Player> listPlayers = new ArrayList<>(); // to have a order in players
	private int numPlayers; 		  // auxiliary to listPlayers
	private Integer firstPlayerIdx;   // auxiliary to listPlayers, not determined until dealTiles
	private Integer currentPlayerIdx; // auxiliary to listPlayers

	private Bag bag;
	private int firstNonNullIdx; // auxiliary to bag.getTiles
	private Board board;

	// ======================== Constructor ========================
	public GameControl(List<Player> lp, boolean shuffle) {
		this.board = new Board();

		this.bag = new Bag(shuffle);
		firstNonNullIdx = 0;

		this.numPlayers = lp.size();
		this.listPlayers = lp;
		firstPlayerIdx = null;

		this.tui = new PlayerClient(this, this.board, lp, this.bag);
	}

	// only for test purpose
	public GameControl(List<Player> lp, boolean shuffle, Board bd) {
		this.board = bd;

		this.bag = new Bag(shuffle);
		firstNonNullIdx = 0;

		this.numPlayers = lp.size();
		this.listPlayers = lp;
		firstPlayerIdx = null;

		this.tui = new PlayerClient(this, this.board, lp, this.bag);
	}
	
	public GameControl(Map<Socket, String> map) {
		// service side Model
		this.board = new Board(); 		this.board.addObserver(this);
		this.bag = new Bag(true); 		firstNonNullIdx = 0;
		
		for (Map.Entry<Socket, String> e : map.entrySet()) {
			this.sockets.add(e.getKey());
			this.listPlayers.add( new HumanPlayer(e.getValue()) );
		}
		
		for (Socket s: sockets) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
				
				this.ins.add(br);
				this.outs.add(bw);
			} catch (IOException e) {
				System.out.println("IOException in GameControl constuctor");
			}
		}

		for (Player p : listPlayers) {
			p.addObserver(this);
		}
		
		this.numPlayers = listPlayers.size();

//		this.tui = new PlayerTUI("player", null, this); // later will maintain a list of GameTUI, one for each Player
	}

	// ============ Preparation: deal the initial tiles ============
	/**
	 * Deal tiles so that each user has 4 Tiles, stored in mapPlayers. Also return
	 * the index of the player to make the first move.
	 * 
	 * @return The index of player that should take the first move. Return null if
	 *         this index is still undetermined. (which is very unlikely for
	 *         shuffled bag)
	 */
	public Integer dealTiles() {

		// for each of the 4 slot in Tile[4]
		for (int j = 0; j < 4; j++) {
			// give each user one tile and remove the tile from the bag
			for (int i = 0; i < numPlayers; i++) {
				dealATileToPlayer(listPlayers.get(i));
			}

			// if the player to make the first move is undetermined, try to determine
			if (firstPlayerIdx == null) {
				// get a list of total values at hand of each player
				// and collect the results in an List
				List<Integer> valAtHand = listPlayers.stream().map(p -> getValuesAtHand(p))
						.collect(Collectors.toList());

				// if there's a unique max value, firstPlayerIdx is determined
				if (hasUniqueMax(valAtHand)) {
					firstPlayerIdx = valAtHand.indexOf(Collections.max(valAtHand));
				}
			}
		}
		return firstPlayerIdx;
	}

	/**
	 * Helper function of public int dealCards().
	 * 
	 * @param p A Player object in listPlayers.
	 * @return An integer indicating the total value of the tiles at the hand of
	 *         this specific player.
	 */
	private Integer getValuesAtHand(Player p) {
		Integer sum = 0;
		Tile[] tilesAtHand = p.getTiles();
		for (Tile t : tilesAtHand) {
			if (t != null) {
				sum += t.getValue();
			}
		}
		return sum;
	}

	/**
	 * Helper function of public int dealCards().
	 * 
	 * @param values A list of integers indicating the total values at at the hand
	 *               of each player.
	 * @return true if there's an unique max value.
	 */
	private boolean hasUniqueMax(List<Integer> values) {
		List<Integer> valuesCopy = new ArrayList<>(values);

		Integer maxVal = Collections.max(valuesCopy);
		Integer idx1 = valuesCopy.indexOf(maxVal);
		Collections.reverse(valuesCopy);
		Integer idx2 = valuesCopy.indexOf(maxVal);
		// should be true if idx2 is pointing to the same element
		// as idx1 do, only in a reverse ordered list
		return (idx1 + idx2) == (valuesCopy.size() - 1);
	}

	// ==================== Gaming: make a move ====================
	public void promptFirstTurn() {
		if (firstPlayerIdx == null) {
			firstPlayerIdx = 0;
		}
		
		String order = "";
		Integer idx = firstPlayerIdx;
		for (int i = 0; i < numPlayers; i++) {
			order += listPlayers.get(idx).getName() + " ";
			idx = (idx + 1) % numPlayers;
		}
		toAll(Commands.ORDER, order);

		Player firstPlayer = listPlayers.get(firstPlayerIdx);
		toAll(Commands.TURN, firstPlayer.getName());

		parseFromClient(firstPlayerIdx);
		
		currentPlayerIdx = (firstPlayerIdx + 1) % numPlayers;
	}

	public void promptNormalTurn() {
		Player currentPlayer = listPlayers.get(currentPlayerIdx);
		toAll(Commands.TURN, currentPlayer.getName());
		
		Map<String, Set<Integer>> allMove = Sanitary.generateAllPossibleMoves(board, currentPlayer);

		parseFromClient(currentPlayerIdx);

//		// if in the form: Move <index> <tile encoding>, or : Skip [tile encodings]
//		while (true) {
//			String theChoice = tui.promptChoice(currentPlayer);
//			String[] words = theChoice.split("\\s");
//
//			if (words[0].equals("Move")) {
//				int theField = Integer.parseInt(words[1]);
//				Tile theTile = new Tile(words[2]);
//
//				if (allMove.get(theTile.stringTile()).contains(theField)) {
//					// 1. place the chosen rotation of chosen tile on the chosen field
//					putTileOnBoard(theField, theTile, currentPlayer.getName());
//					// 2. nullify the chosen Tile at Player's hand
//					nullifyChosenTile(currentPlayer, theTile);
//					// 3. deal one tile to player, to restore to 4 tiles in hand
//					dealATileToPlayer(currentPlayer);
//
//					break;
//				}
//
//				else {
//					// later: S -> C: Error <reason>
//					System.out.println("Illegal move, try again.");
//				}
//
//			} else if ((words[0].equals("Skip")) && (words.length == 2)) {
//				// user want to swap a tile
//				Tile fromPlayer = new Tile(words[1]);
//				Tile fromBag = swapRandomTileInBag(fromPlayer);
//
//				toAll(Commands.SKIP, currentPlayer.getName() + " " + fromPlayer.stringTile() + " " + fromBag.stringTile());
//
//				nullifyChosenTile(currentPlayer, fromPlayer);
//				dealATileToPlayer(currentPlayer, fromBag);
//
//			} else if ((words[0].equals("Skip")) && (words.length == 1)) {
//				// user want to skip turn
//				toAll(Commands.SKIP, currentPlayer.getName());
//
//			} else {
//				System.out.println("You shouldn't fall here.");
//			}
//		}
		currentPlayerIdx = (currentPlayerIdx + 1) % numPlayers;
	}

	// ==================== Observer-Observable pattern ====================

	public void update(Observable obs, Object arg) {
		// Player and Board will send readily-made command 
		toAll((String) arg);

	}


	// ================== Networking: common functionalities ==================
	public void toAll(String cmd, String other) {
		for (BufferedWriter bw : outs) {
			try {
				bw.write(cmd + " " + other);
				bw.newLine();
				bw.flush();
			} catch (IOException e) {
				System.out.println("ERROR: unable to communicate to server");
	            e.printStackTrace();
			}
		}
	}
	
	public void toAll(String whole) {
		// only to be used in update()
		toAll(whole, "");
	}
	
	public void parseFromClient(Integer idx) {
		BufferedReader br = ins.get(idx);
		Player currentPlayer = listPlayers.get(idx);
		
		String s = null;
		try {
			s = br.readLine();
		} catch (IOException e) {
			System.out.println("IOException in parsing info from server.");
		}
		
        String[] info = null;
        if (s != null) {
        	info = s.split("\\s");
        } else {
        	System.out.println("Server sends null");
        }
        
        switch(info[0]) {
        	case Commands.MOVE:
        		putTileOnBoard(info, currentPlayer.getName());
        		nullifyChosenTile(info[2], currentPlayer);
        		dealATileToPlayer(currentPlayer);
        		break;
        	case Commands.SKIP:
        		System.out.println("In skip, Game Server has received: " + s);
        		if (info.length == 1) {
        			toAll(Commands.SKIP, currentPlayer.getName());
        			break; 
        		}
        		if (info.length == 2) {
        			Tile fromPlayer = new Tile(info[1]);
    				Tile fromBag = swapRandomTileInBag(fromPlayer);
    				toAll(Commands.SKIP, currentPlayer.getName() + " " + fromPlayer.stringTile() + " " + fromBag.stringTile());
        			
        			nullifyChosenTile(fromPlayer.stringTile(), currentPlayer);
        			dealATileToPlayer(currentPlayer, fromBag);
        		}
        		break;
        }
	}

	// ================== Gaming: other common functionalities ==================
	// ------------------ that both FirstMove and NormalMove can use
	// ------------------
	public void putTileOnBoard(int idx, Tile t, String playerName) {
		board.setTile(idx, t, playerName);
	}
	
	public void putTileOnBoard(String[] infos, String playerName) {
		int theField = Integer.parseInt(infos[1]);
		Tile theTile = new Tile(infos[2]);
		board.setTile(theField, theTile, playerName);
	}

	private void nullifyChosenTile(Player p, Tile chosenTile) {
		for (int i = 0; i < 4; i++) {
			Tile t = p.getTiles()[i];
			// you don't want to call toString on a null object.
			if (t != null) {
				if (t.toString().equals(chosenTile.toString())) {
					p.getTiles()[i] = null;
					System.out.println("Server side model player chosen tile nullified.");
				}
			}
		}
	}
	
	private void nullifyChosenTile(String encoding, Player p) {
		for (int i = 0; i < 4; i++) {
			Tile t = p.getTiles()[i];
			// you don't want to call toString on a null object.
			if (t != null) {
				if (t.toString().equals(encoding.substring(1))) {
					p.getTiles()[i] = null;
				}
			}

		}
	}

	/**
	 * Draw the first non-null Tile, and set its position as null, and update
	 * firstNonNullIdx.
	 * 
	 * @return The tile being drew from the bag.
	 */
	public Tile drawATile() {
		if (firstNonNullIdx < 36) {
			Tile t = bag.getTiles().get(firstNonNullIdx);
			bag.getTiles().set(firstNonNullIdx, null);
			firstNonNullIdx++;
			return t;
		} else {
			return null;
		}

	}

	/**
	 * Deal the first non-null Tile in bag to Player p, p will fill the first null
	 * position with this tile.
	 */
	public void dealATileToPlayer(Player p) {
		Tile t = drawATile();
		p.takeTheTile(t);
	}

	/**
	 * Deal a specific Tile to p, p will fill the first null position with this
	 * tile.
	 */
	public void dealATileToPlayer(Player p, Tile t) {
		p.takeTheTile(t);
	}

	/**
	 * Choose a random non-null Tile in bag, replace it with the Tile t, and return
	 * the replaced Tile.
	 */
	public Tile swapRandomTileInBag(Tile t) {
		Random r = new Random();
		// generate a random int within [firstNonNullIdx, 35] (inclusive)
		int randNonNullIdx = r.nextInt(36 - firstNonNullIdx) + firstNonNullIdx;
		Tile getFromBag = bag.getTile(randNonNullIdx).deepCopy();
		bag.getTiles().set(randNonNullIdx, t);
		return getFromBag;
	}

	/**
	 * Safety measurement before every new game.
	 */
	public void resetBoard() {
		board.resetBoard();
	}

	// ======================== Queries ========================
	// only for test purposes
	public Board getBoard() {
		return this.board;
	}

	public List<Player> getListPlayers() {
		return this.listPlayers;
	}

	/**
	 * A getter of this.bag.getTiles(); Don't change it after getting it!
	 * 
	 * @return
	 */
	public List<Tile> getTiles() {
		return bag.getTiles();
	}

	public int getFirstNonNullIdx() {
		return this.firstNonNullIdx;
	}

	public boolean noOneCanPlay() {
		for (Player p : listPlayers) {
			if (Sanitary.generateAllPossibleMoves(board, p).size() > 0) {
				return false;
			}
		}
		return true;
	}

	public boolean bagIsEmpty() {
		return firstNonNullIdx > 35;
	}

	// ======================== Main || Run ========================
	public static void main(String[] args) {
//		GameControl g = new GameControl(null, Arrays.asList("A", "B", "C", "D"));
//
//		System.out.println("First player is at index: " + g.dealTiles());
//
//		g.promptFirstTurn();
//
//		while (!((g.bagIsEmpty()) && (g.noOneCanPlay()))) {
//			g.promptNormalTurn();
//		}
//
//		System.out.println("Congrats! You've reached the end of one game!");
	}
	
	public void run() {
		System.out.println("You are in GameControl.run()");
		
		System.out.println("First player is at index: " + dealTiles());

		promptFirstTurn();

		while (!((bagIsEmpty()) && (noOneCanPlay()))) {
			promptNormalTurn();
		}
		
		toAll(Commands.END, " BigCongrats!");

		System.out.println("Congrats! You've reached the end of one game!");
	}

}
