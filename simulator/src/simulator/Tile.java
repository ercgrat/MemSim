package simulator;

import java.util.*;

public class Tile {
	
	private final int p, b, n1, n2, a1, a2, d, d1;
	
	private int totalDelay;
	private ArrayList<Tile> tiles;
	private ArrayList<Access> ops;
	private L1Cache L1;
	private L2Cache L2;

	public Tile(ArrayList<Tile> tiles, int p, int b, int n1, int n2, int a1, int a2, int d, int d1) {
		this.tiles = tiles;
		this.p = p;
		this.b = b;
		this.n1 = n1;
		this.n2 = n2;
		this.a1 = a1;
		this.a2 = a2;
		this.d = d;
		this.d1 = d1;
		
		totalDelay = 0;
		ops = new ArrayList<Access>();
		L1 = new L1Cache(p, b, n1, a1);
		L2 = new L2Cache(p, b, n2, a2);
	}
	
	public void addAccess(Access access) {
		ops.add(access);
	}
	
	public void cycle(int cycle) {
		if(ops.size() == 0) {
			return;
		}
		Access access = ops.get(0);
		if(access.getCycle() == cycle - totalDelay) {
			if(L1.hit(access.getAddress())) { // L1 cache hit, just delete the access
				ops.remove(0);
			} else {
				/*
				* Here's where everything complicated goes!
				*
				*/
			}
		}
	}

}