package simulator;

import java.util.*;

public class Tile {
	
	private ArrayList<Access> ops;

	public Tile() {
		ops = new ArrayList<Access>();
	}
	
	public void addAccess(Access access) {
		ops.add(access);
	}

}