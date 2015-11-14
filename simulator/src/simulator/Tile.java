package simulator;

import java.util.*;

public class Tile {

    private final int p, b, n1, n2, a1, a2, d, d1, C;
    private final int MEMORY_TILE = 0;
    private int totalDelay;
    private ArrayList<Tile> tiles;
    private ArrayList<Access> ops;
    private L1Cache L1;
    private L2Cache L2;
    
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

        totalDelay = 0;
        ops = new ArrayList<Access>();
        L1 = new L1Cache(p, b, n1, a1);
        L2 = new L2Cache(p, b, n2, a2);
    }

    public void addAccess(Access access) {
        ops.add(access);
    }


    public void cycle(int cycle, int tileNum) {
        if (ops.size() == 0) {
            return;
        }
        Access access = ops.get(0);
        access.setState();
        if (access.getCycle() == cycle - totalDelay) {
            if (!L1.hit(access.getAddress(), cycle, access.accessType())) { // L1 cache miss, all the logic kicks in
                int homeTile = Block.page(access.getAddress(), p);
                Block.MSIState homeState = tiles.get(homeTile).getL2State(access.getAddress());
                boolean[] ownerArray = new boolean[(int)Math.pow(2, p)];
                if(homeState == Block.MSIState.MODIFIED || homeState == Block.MSIState.SHARED)
                    ownerArray = tiles.get(homeTile).getOwnerArray(access.getAddress());
                
                int delay = calculateDelay(access, tileNum, homeTile, homeState);
                totalDelay += delay;
                
                int evictAddress = L1.setState(access.getAddress(), access.state, cycle, true); //Set own L1 state
                if(evictAddress != -1){
                    int evictHomeTile = Block.page(evictAddress, p);
                    tiles.get(evictHomeTile).removeFromOwnersInL2(evictAddress, tileNum);
                }
                
                int L2evictAddress = tiles.get(homeTile).setL2State(access.getAddress(), access.state, cycle, tileNum);
                if(L2evictAddress != -1){
                    boolean[] L2evicteeOwnerArray = new boolean[(int)Math.pow(2, p)];
                    L2evicteeOwnerArray = tiles.get(homeTile).getOwnerArray(L2evictAddress);
                    for(int i = 0; i < (int)Math.pow(2, p); i++)
                    if(L2evicteeOwnerArray[i])
                        tiles.get(i).setL1State(L2evictAddress, Block.MSIState.INVALID, cycle, false);
                }
                
                for(int i = 0; i < (int)Math.pow(2, p); i++){
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
        }
    }
    
    public int setL1State(int address, Block.MSIState state, int cycle, boolean own){
        return L1.setState(address, state, cycle, own);
    }

    public Block.MSIState getL2State(int address){ //Get state of block in home tile
        return L2.getState(address);
    }
    
    public int setL2State(int address, Block.MSIState state, int cycle, int tileNum){
        return L2.setState(address, state, cycle, tileNum);
    }
    
    public boolean[] getOwnerArray(int address){ //return owner array, number of entries depends on output of getL2State
        return L2.getOwnerArray(address);
    }
    
    public void removeFromOwnersInL2(int address, int tileNum){
        L2.removeFromOwners(address, tileNum);
    }
    
    public int calculateDelay(Access access, int tile, int home, Block.MSIState homeState) {
        int delay = d + Block.manhattanDistance(tile, home, p) * 2 * C;
        if(access.accessType()) { // Reading, L1 state is invalid
            if(homeState == Block.MSIState.INVALID) { // L2 miss, need to contact memory controller and load block
                delay += d1 + Block.manhattanDistance(home, MEMORY_TILE, p) * 2 * C;
            } else if(homeState == Block.MSIState.SHARED) { // L2 hit, no additional penalty
                // :)
            } else if(homeState == Block.MSIState.MODIFIED) { // L2 miss, need to contact the current owner
                int owner = L2.getOwner(access.getAddress());
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