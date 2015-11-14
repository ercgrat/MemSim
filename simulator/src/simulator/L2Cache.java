package simulator;

public class L2Cache {

    int p, b, n, a;
    L2CacheEntry[][] cache;

    public L2Cache(int p, int b, int n, int a) {
        this.p = p;
        this.b = b;
        this.n = n;
        this.a = a;

        cache = new L2CacheEntry[(int) Math.pow(2, a)][(int) Math.pow(2, n - a - b)];
    }
    
    public int getOwner(int address) {
        boolean[] ownerArray = this.getOwnerArray(address);
        for(int i = 0; i < ownerArray.length; i++) {
            if(ownerArray[i] == true) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean[] getOwnerArray(int address) {
        return this.getEntry(address).getOwnerArray();
    }
    
    public int setState(int address, Block.MSIState state, int cycle) {
        if(state == Block.MSIState.INVALID) {
            System.out.println("We should never be setting an L2 entry to INVALID.");
            return -1;
        }
        
        L2CacheEntry entry = getEntry(address);
        if(entry == null) { // Block is not in the L2 cache
            int tag = Block.L2tag(address, p, b, n, a);
            L2CacheEntry newEntry = new L2CacheEntry(tag, state, p);
            newEntry.touch(cycle);
            
            int cacheIndex = Block.L2cacheIndex(address, p, b, n, a);
            int minLastCycle = Integer.MAX_VALUE;
            int LRUway = 0;
            
            // Search the ways for empty slot, else evict
            for(int way = 0; way < (int) Math.pow(2, a); way++) { 
                if(cache[way][cacheIndex] == null) { // Empty slot, just add entry here
                    cache[way][cacheIndex] = newEntry;
                    return -1;
                } else { // Keep track of the least recently used block
                    if(cache[way][cacheIndex].getLastCycleUsed() < minLastCycle) {
                        minLastCycle = cache[way][cacheIndex].getLastCycleUsed();
                        LRUway = way;
                    }
                }
            }
            
            // Set was full, evicting the LRU block
            /*
             *
             * TODO: Get address of block being evicted, return it then invalidate all L1 owners of that block
             *
             */
            cache[LRUway][cacheIndex] = newEntry;
        } else { // Block is in the L2 cache, just change the state
            entry.setState(state);
        }
        
        return -1;
    }
    
    public Block.MSIState getState(int address) {
        L2CacheEntry entry = getEntry(address);
        if(entry == null) {
            return Block.MSIState.INVALID;
        } else {
            return entry.getState();
        }
    }
    
    public L2CacheEntry getEntry(int address) {
        int cacheIndex = Block.L2cacheIndex(address, p, b, n, a);
        for (int way = 0; way < (int) Math.pow(2, a); way++) {
            if (cache[way][cacheIndex] != null) {
                L2CacheEntry entry = cache[way][cacheIndex];
                int tag = Block.L2tag(address, p, b, n, a);
                if (entry.getTag() == tag) { // Block is in cache, return it
                    return entry;
                }
            }
        }
        return null; // Not found
    }
}