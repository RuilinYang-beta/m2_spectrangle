package spectranglegame;

// only to resolve the com
public class MineTile {
	
	private int value;
	private String vColor;
	private String lColor;
	private String rColor;
	
	// constructor to make a bonus field
	public MineTile(int v, String vC, String lC, String rC) {
		this.value = v;
		this.vColor = vC;
		this.lColor = lC;
		this.rColor = rC;
	}
	
	public int getValue() {
		return value;
	}

	public String getvColor() {
		return vColor;
	}

	public String getlColor() {
		return lColor;
	}

	public String getrColor() {
		return rColor;
	}
	
	
}
