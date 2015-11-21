package simulator;

import java.util.*;

public class Tile {

    private final long p, b, n1, n2, a1, a2, d, d1, C;
    private final long MEMORY_TILE = 0;
    private long totalDelay;
    private ArrayList<Tile> tiles;
    private ArrayList<Access> ops;
    private L1Cache L1;
    private L2Cache L2;
    private int numAccesses;
    private Statistics stats;

    public Tile(ArrayList<Tile> tiles, long p, long b, long n1, long n2, long a1, long a2, long d, long d1, long C, Statistics stats) {
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
	this.stats = stats;
	totalDelay = 0;
	numAccesses = 0;
	ops = new ArrayList<Access>();
	L1 = new L1Cache(p, b, n1, a1);
	L2 = new L2Cache(p, b, n2, a2);
    }

    public void addAccess(Access access) {
	ops.add(access);
    }

    public boolean cycle(long cycle, int tileNum, boolean verbose) {

	if (ops.size() == 0) {
	    return false;
	}

	Access access = ops.get(0);
	access.setState();
	String readWrite = access.accessType() ? "read" : "write to";
	String hexAddress = Long.toHexString(access.getAddress());
	//System.out.println("Tile " + tileNum + ", access cycle " + access.getCycle() + ", on cycle " + cycle + " with delay " + totalDelay);
	if (access.getCycle() <= (cycle - totalDelay)) {
	    numAccesses++;
	    stats.incrementL1Accesses(tileNum);
	    if (!L1.hit(access.getAddress(), cycle, access.accessType())) { // L1 cache miss, all the logic kicks in
		stats.incrementL1Misses(tileNum);
		if(verbose)
		    System.out.println("L1 miss in tile: " + tileNum + " at cycle: " + cycle + " when trying to " + readWrite +" address 0x" +hexAddress);
		int homeTile = (int) Block.page(access.getAddress(), p);
		stats.incrementL2Accesses(homeTile);
		Block.MSIState homeState = tiles.get(homeTile).getL2State(access.getAddress());
		if(verbose)
		    System.out.println("Block state in home tile " + homeTile + " is " + homeState);
		stats.incrementControlMsg();
		boolean[] ownerArray = new boolean[(int) Math.pow(2, p)];
		if (homeState == Block.MSIState.MODIFIED || homeState == Block.MSIState.SHARED) {
			boolean[] existingOwners = tiles.get(homeTile).getOwnerArray(access.getAddress());
			for(int i = 0; i < existingOwners.length; i++) {
				ownerArray[i] = existingOwners[i];
			}
		}
		else
		    stats.incrementL2Misses(homeTile);

		long delay = calculateDelay(access, tileNum, homeTile, homeState);
		stats.addDelay(delay, tileNum);
		totalDelay += delay;
		if(verbose)
		    System.out.println("Total delay for this access would be " + delay);
		if(access.accessType() || !(L1.getState(access.getAddress())==Block.MSIState.SHARED))
		    stats.incrementDataMsg();
		else
		    stats.incrementControlMsg();
		
		long evictAddress = L1.setState(access.getAddress(), access.getState(), cycle, true); //Set own L1 state
		if (evictAddress != -1) {
		    int evictHomeTile = (int) Block.page(evictAddress, p);
		    if(tiles.get(evictHomeTile).getL2State(evictAddress)==Block.MSIState.MODIFIED)
			stats.incrementDataMsg();
		    tiles.get(evictHomeTile).removeFromOwnersInL2(evictAddress, tileNum);
		    stats.incrementControlMsg();
		}
		
		L2CacheEntry L2evictedEntry = tiles.get(homeTile).setL2State(access.getAddress(), access.getState(), cycle, tileNum);
		if (L2evictedEntry != null) {
		    long L2evictAddress = L2evictedEntry.getAddress();
		    boolean[] L2evicteeOwnerArray = L2evictedEntry.getOwnerArray();
		    for (int i = 0; i < (int) Math.pow(2, p); i++) {
			if (L2evicteeOwnerArray[i]) {
			    stats.incrementControlMsg();
			    tiles.get(i).setL1State(L2evictAddress, Block.MSIState.INVALID, cycle, false);
			}
		    }
		}
		
		for (int i = 0; i < (int) Math.pow(2, p); i++) {
		    if (i != tileNum && ownerArray[i]) {
			if (access.read && homeState == Block.MSIState.MODIFIED) {
			    tiles.get(i).setL1State(access.getAddress(), Block.MSIState.SHARED, cycle, false);
			    stats.incrementControlMsg();
			} else if(!access.read) {
			    tiles.get(i).setL1State(access.getAddress(), Block.MSIState.INVALID, cycle, false);
			    stats.incrementControlMsg();
			}
		    }
		}
		if(verbose){
		    if(L2evictedEntry != null){
			System.out.println("Block with address: 0x" +  Long.toHexString(L2evictedEntry.getAddress()) + " in L2  of home tile " + homeTile + " is evicted.");
			boolean[] L2evicteeOwnerArray = L2evictedEntry.getOwnerArray();
			for(int i = 0;i < (int) Math.pow(2, p); i++){
			    if (L2evicteeOwnerArray[i]) {
				System.out.println("	This also invalidates it in L1 of tile : " + i);
			    }
			}
		    }
			
		    System.out.println("State of requested block with address: 0x" + hexAddress + " in L2 of home tile " + homeTile + " is now set to " + access.getState());
		    for (int i = 0; i < (int) Math.pow(2, p); i++) {
			if (i != tileNum && ownerArray[i]) {
			    if (access.read && homeState == Block.MSIState.MODIFIED) {
				System.out.println("The requested block with address: 0x" + hexAddress + "is now set to SHARED in L1 of tile " + i);
			    } else if(!access.read) {
				System.out.println("The requested block with address: 0x" + hexAddress + "is now INVALIDATED in L1 of tile " + i);
			    }
			}
		    }
		    if(evictAddress != -1){
			int evictHomeTile = (int) Block.page(evictAddress, p);
			System.out.println("Block with address: 0x" + Long.toHexString(evictAddress) + " is evicted from L1 of the requesting tile " + tileNum + " to make way for the requested block.");
			System.out.println("The requesting tile " + tileNum + " is removed from the list of owners/sharers of this evicted block with address: 0x" + Long.toHexString(evictAddress) + " in the L2 of this block's home tile " + evictHomeTile);
		    }
		    System.out.println("Finally, state of the requested block with address: 0x" + hexAddress + " is now set to " + access.getState() +" in L1 of requesting tile " + tileNum);
		}

	    }
	    else{
		if(verbose){
			
		    System.out.println("L1 hit in tile: " + tileNum + " at cycle: " + cycle + " when trying to " + readWrite +" address 0x" +hexAddress);
		    System.out.println("The state in L1 was already " + L1.getState(access.getAddress()));
		    System.out.println("No changes in state required for this access.");
		}
	    }
	    ops.remove(0);

	}

	return true;
    }

    public long setL1State(long address, Block.MSIState state, long cycle, boolean own) {
	return L1.setState(address, state, cycle, own);
    }

    public void printL1Cache(){
	L1.printCache();
    }
    
    public void printL2Cache(){
	L2.printCache();
    }
    
    public Block.MSIState getL2State(long address) { //Get state of block in home tile
	return L2.getState(address);
    }

    public L2CacheEntry setL2State(long address, Block.MSIState state, long cycle, int tileNum) {
	return L2.setState(address, state, cycle, tileNum);
    }

    public boolean[] getOwnerArray(long address) { //return owner array, number of entries depends on output of getL2State
	return L2.getOwnerArray(address);
    }
	
	public long getOwner(long address) {
		return L2.getOwner(address);
	}

    public void removeFromOwnersInL2(long address, int tileNum) {
	L2.removeFromOwners(address, tileNum);
    }

    public long calculateDelay(Access access, int tile, int home, Block.MSIState homeState) {
	long delay = d + Block.manhattanDistance((long) tile, home, p) * 2 * C;
	if (access.accessType()) { // Reading, L1 state is invalid
	    if (homeState == Block.MSIState.INVALID) { // L2 miss, need to contact memory controller and load block
		stats.incrementControlMsg();
		stats.incrementDataMsg();
		delay += d1 + Block.manhattanDistance(home, MEMORY_TILE, p) * 2 * C;
	    } else if (homeState == Block.MSIState.SHARED) { // L2 hit, no additional penalty
		// :)
	    } else if (homeState == Block.MSIState.MODIFIED) { // L2 miss, need to contact the current owner
		long owner = tiles.get(home).getOwner(access.getAddress());
		delay += Block.manhattanDistance(home, owner, p) * 2 * C;
		//stats.incrementControlMsg();
		stats.incrementDataMsg();
	    }
	} else { // Writing, L1 state is shared or invalid
	    if (homeState == Block.MSIState.INVALID) { // L2 miss, need to contact memory controller and load block
		delay += d1 + Block.manhattanDistance(home, MEMORY_TILE, p) * 2 * C;
		stats.incrementControlMsg();
		stats.incrementDataMsg();
	    } else if (homeState == Block.MSIState.SHARED) { // L2 hit, no additional penalty
		// :)
		stats.incrementControlMsg();
	    } else if (homeState == Block.MSIState.MODIFIED) { // L2 miss, need to invalidate owners, but no penalty
		// :)
		stats.incrementDataMsg();
	    }
	}
	return delay;
    }
}