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
    
    public Tile(ArrayList<Tile> tiles, long p, long b, long n1, long n2, long a1, long a2, long d, long d1, long C) {
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
		
		while(ops.size() > 0) {
			Access access = ops.get(0);
			access.setState();
			System.out.println("Tile " + tileNum + ", access cycle " + access.getCycle() + ", on cycle " + cycle + " with delay " + totalDelay);
			if (access.getCycle() == cycle - totalDelay) {
				numAccesses++;
				System.out.println("Requesting tile: " + tileNum + ", access number " + numAccesses + ", access cycle " + access.getCycle());
				if (!L1.hit(access.getAddress(), cycle, access.accessType())) { // L1 cache miss, all the logic kicks in
					int homeTile = (int)Block.page(access.getAddress(), p);
					System.out.println("Address: " + access.getAddress() + ", requesting tile: " + tileNum + ", home tile: " + homeTile + ", cycle: " + cycle);
					Block.MSIState homeState = tiles.get(homeTile).getL2State(access.getAddress());
					boolean[] ownerArray = new boolean[(int)Math.pow(2, p)];
					if(homeState == Block.MSIState.MODIFIED || homeState == Block.MSIState.SHARED) {
						ownerArray = tiles.get(homeTile).getOwnerArray(access.getAddress());
					}
					
					long delay = calculateDelay(access, tileNum, homeTile, homeState);
					totalDelay += delay;
					System.out.println("adding delay... " + delay);
					
					long evictAddress = L1.setState(access.getAddress(), access.getState(), cycle, true); //Set own L1 state
					if(evictAddress != -1) {
						int evictHomeTile = (int)Block.page(evictAddress, p);
						tiles.get(evictHomeTile).removeFromOwnersInL2(evictAddress, tileNum);
					}
					
					L2CacheEntry L2evictedEntry = tiles.get(homeTile).setL2State(access.getAddress(), access.getState(), cycle, tileNum);
					if(L2evictedEntry != null){
						long L2evictAddress = L2evictedEntry.getAddress();
						boolean[] L2evicteeOwnerArray = L2evictedEntry.getOwnerArray();
						for(int i = 0; i < (int)Math.pow(2, p); i++) {
							if(L2evicteeOwnerArray[i]) {
								tiles.get(i).setL1State(L2evictAddress, Block.MSIState.INVALID, cycle, false);
							}
						}
					}
					
					for(int i = 0; i < (int)Math.pow(2, p); i++) {
						if(i != tileNum && ownerArray[i]){
							if(access.read){
								tiles.get(i).setL1State(access.getAddress(), Block.MSIState.SHARED, cycle, false);
							} else {
								tiles.get(i).setL1State(access.getAddress(), Block.MSIState.INVALID, cycle, false);
							}
						}
					}
					
				}
				ops.remove(0);
			} else {
				break;
			}
		}
		
		return true;
    }
    
    public long setL1State(long address, Block.MSIState state, long cycle, boolean own){
        return L1.setState(address, state, cycle, own);
    }

    public Block.MSIState getL2State(long address){ //Get state of block in home tile
        return L2.getState(address);
    }
    
    public L2CacheEntry setL2State(long address, Block.MSIState state, long cycle, int tileNum){
        return L2.setState(address, state, cycle, tileNum);
    }
    
    public boolean[] getOwnerArray(long address){ //return owner array, number of entries depends on output of getL2State
        return L2.getOwnerArray(address);
    }
    
    public void removeFromOwnersInL2(long address, int tileNum){
        L2.removeFromOwners(address, tileNum);
    }
    
    public long calculateDelay(Access access, int tile, long home, Block.MSIState homeState) {
        long delay = d + Block.manhattanDistance((long)tile, home, p) * 2 * C;
        if(access.accessType()) { // Reading, L1 state is invalid
            if(homeState == Block.MSIState.INVALID) { // L2 miss, need to contact memory controller and load block
                delay += d1 + Block.manhattanDistance(home, MEMORY_TILE, p) * 2 * C;
            } else if(homeState == Block.MSIState.SHARED) { // L2 hit, no additional penalty
                // :)
            } else if(homeState == Block.MSIState.MODIFIED) { // L2 miss, need to contact the current owner
                long owner = L2.getOwner(access.getAddress());
                delay += Block.manhattanDistance(home, owner, p) * 2 * C;
            }
        } else { // Writing, L1 state is shared or invalid
            if(homeState == Block.MSIState.INVALID) { // L2 miss, need to contact memory controller and load block
                delay += d1 + Block.manhattanDistance(home, MEMORY_TILE, p) * 2 * C;
            } else if(homeState == Block.MSIState.SHARED) { // L2 hit, no additional penalty
                // :)
            } else if(homeState == Block.MSIState.MODIFIED) { // L2 miss, need to invalidate owners, but no penalty
                // :)
            }
        }
        return delay;
    }
    
}