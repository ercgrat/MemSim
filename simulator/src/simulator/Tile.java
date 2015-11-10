package simulator;

import java.util.*;

public class Tile {

    private final int p, b, n1, n2, a1, a2, d, d1, C;
    private int totalDelay;
    private ArrayList<Tile> tiles;
    private ArrayList<Access> ops;
    private ArrayList<Access> responses;
    private ArrayList<Access> requests;
    private L1Cache L1;
    private L2Cache L2;
    private boolean waiting;

    public Tile(ArrayList<Tile> tiles, int p, int b, int n1, int n2, int a1, int a2, int d, int d1, int C) {
	this.tiles = tiles;
	this.p = p;
	this.b = b;
	this.n1 = n1;
	this.n2 = n2;
	this.a1 = a1;
	this.a2 = a2;
	this.d = d;
	this.d1 = d1;
	this.C = C;

	waiting = false;
	totalDelay = 0;
	ops = new ArrayList<Access>();
	requests = new ArrayList<Access>();
	responses = new ArrayList<Access>();
	L1 = new L1Cache(p, b, n1, a1);
	L2 = new L2Cache(p, b, n2, a2);
    }

    public void addAccess(Access access) {
	ops.add(access);
    }

    public void setRequest(int futureCycle, Access access, int tileNum) {
	Access request = new Access(futureCycle, access.getAddress(), access.accessType(), tileNum);
	requests.add(request);
    }

    public void cycle(int cycle, int tileNum) {
	L2cycle(cycle, tileNum);
	if (!waiting) {
	    if (ops.size() == 0) {
		return;
	    }
	    Access access = ops.get(0);
	    if (access.getCycle() == cycle - totalDelay) {
		if (L1.hit(access.getAddress(), cycle)) { // L1 cache hit, just delete the access
		    ops.remove(0);
		} else {
		    waiting = true;
		    int homeTile = Block.page(access.getAddress(), p);
		    int futureCycle = cycle + d + C * (Block.manhattanDistance(tileNum, homeTile, p) + 1);
		    tiles.get(homeTile).setRequest(futureCycle, access, tileNum);
		}
	    }
	} else {
	    if (responses.size() > 0) {
		int rcvSize = responses.size();
		int correctRcv = -1;
		for (int i = 0; i < rcvSize; i++) {
		    if (tileNum != responses.get(i).getRequester() && cycle == responses.get(i).getCycle()) {
			correctRcv = i;
			break;
		    }
		}
		if (correctRcv != -1) {
		    L1.setEntry(responses.get(correctRcv).getAddress(), cycle, responses.get(correctRcv).state);
		    ops.remove(0);
		    responses.remove(correctRcv);
		    waiting = false;
		} else {
		    totalDelay += 1;
		}
	    }
	}
    }

    private void L2cycle(int cycle, int tileNum) {
    }
}