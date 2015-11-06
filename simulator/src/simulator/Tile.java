package simulator;

import java.util.*;

public class Tile {
	
	private ArrayList<Access> ops;
	private Directory L1;
	public L2Directory L2;

	public Tile() {
		ops = new ArrayList<Access>();
		L1 = new Directory();
		L2 = new L2Directory(0); // fill in with p later
	}
	
	public void addAccess(Access access) {
		ops.add(access);
	}

}