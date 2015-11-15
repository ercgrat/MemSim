package simulator;

public class L1Cache {

    long p, b, n, a;
    L1CacheEntry[][] cache;

    public L1Cache(long p, long b, long n, long a) {
        this.p = p;
        this.b = b;
        this.n = n;
        this.a = a;

        cache = new L1CacheEntry[(int) Math.pow(2, a)][(int) Math.pow(2, n - a - b)];
    }
    
    public boolean hit(long address, long cycle, boolean read) {
        Block.MSIState state = getState(address);
        if((read && (state == Block.MSIState.SHARED)) || state == Block.MSIState.MODIFIED) {
            L1CacheEntry entry = getEntry(address);
            entry.touch(cycle);
            return true;
        } else {
            return false;
        }
    }
    
    public Block.MSIState getState(long address) {
        L1CacheEntry entry = getEntry(address);
        if(entry == null) {
            return Block.MSIState.INVALID;
        } else {
            return entry.getState();
        }
    }
    
    public long setState(long address, Block.MSIState state, long cycle, boolean own) {
        L1CacheEntry entry = getEntry(address);
        if(!own) {
            entry.setState(state);
        } else if(entry == null && state != Block.MSIState.INVALID) { // Block is not in the L1 cache
            long tag = Block.L1tag(address, p, b, n, a);
            L1CacheEntry newEntry = new L1CacheEntry(tag, state, address);
            newEntry.touch(cycle);
            
            int cacheIndex = (int) Block.L1cacheIndex(address, b, n, a);
            long minLastCycle = Integer.MAX_VALUE;
            int LRUway = 0;
            
            // Search the ways for empty slot, else evict
            for(int way = 0; way < (int) Math.pow(2, a); way++) { 
                if(cache[way][cacheIndex] == null || cache[way][cacheIndex].getState() == Block.MSIState.INVALID) { // Empty slot, just add entry here
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
            long evictAddress = cache[LRUway][cacheIndex].getAddress();
            cache[LRUway][cacheIndex] = newEntry;
            return evictAddress;
        } else { // Block is in the L1 cache, just change the state
            entry.setState(state);
            entry.touch(cycle);
        }
        
        return -1;
    }
    
    public L1CacheEntry getEntry(long address) {
        int cacheIndex = (int) Block.L1cacheIndex(address, b, n, a);
        for (int way = 0; way < (int) Math.pow(2, a); way++) {
            if (cache[way][cacheIndex] != null) {
                L1CacheEntry entry = cache[way][cacheIndex];
                long tag = Block.L1tag(address, p, b, n, a);
                if (entry.getTag() == tag && entry.getState() != Block.MSIState.INVALID) { // Block is in cache, return it
                    return entry;
                }
            }
        }
        return null; // Not found
    }
}