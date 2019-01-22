package spectranglegame;

public class HumanPlayer implements Player{
	
	private String name;
	
	public HumanPlayer(String n) {
		this.name = n;
	}
	
	public String getName() {
		return this.name;
	}

}
