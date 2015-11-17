package simulator;

public class L2Cache {

    long p, b, n, a;
    L2CacheEntry[][] cache;

    public L2Cache(long p, long b, long n, long a) {
        this.p = p;
        this.b = b;
        this.n = n;
        this.a = a;

        cache = new L2CacheEntry[(int) Math.pow(2, a)][(int) Math.pow(2, n - a - b)];
    }
    
    public long getOwner(long address) {
        boolean[] ownerArray = this.getOwnerArray(address);
        for(int i = 0; i < ownerArray.length; i++) {
            if(ownerArray[i] == true) {
                return i;
            }
        }
        return -1;
    }
    
    public boolean[] getOwnerArray(long address) {
        return this.getEntry(address).getOwnerArray();
    }
    
    public L2CacheEntry setState(long address, Block.MSIState state, long cycle, int tileNum) {
        if(state == Block.MSIState.INVALID) {
            System.out.println("We should never be setting an L2 entry to INVALID.");
            return null;
        }
        
        L2CacheEntry entry = getEntry(address);
        if(entry == null) { // Block is not in the L2 cache
            //System.out.println("Address is 0x" + Long.toHexString(address));
	    long tag = Block.L2tag(address, p, b, n, a);
	    //System.out.println("Tag is 0x" + Long.toHexString(tag));
            L2CacheEntry newEntry = new L2CacheEntry(tag, state, p, address);
            newEntry.touch(cycle);
            newEntry.addToOwners(tileNum);
            int cacheIndex = (int)Block.L2cacheIndex(address, p, b, n, a);
	    //System.out.println("Cache index is 0x" + Integer.toHexString(cacheIndex));
            long minLastCycle = Integer.MAX_VALUE;
            int LRUway = 0;
            
            // Search the ways for empty slot, else evict
            for(int way = 0; way < (int)Math.pow(2, a); way++) { 
                if(cache[way][cacheIndex] == null || cache[way][cacheIndex].getState() == Block.MSIState.INVALID) { // Empty slot, just add entry here
                    cache[way][cacheIndex] = newEntry;
                    return null;
                } else { // Keep track of the least recently used block
                    if(cache[way][cacheIndex].getLastCycleUsed() < minLastCycle) {
                        minLastCycle = cache[way][cacheIndex].getLastCycleUsed();
                        LRUway = way;
                    }
                }
            }
            
            // Set was full, evicting the LRU block
            L2CacheEntry evictedEntry = cache[LRUway][cacheIndex];
            cache[LRUway][cacheIndex] = newEntry;
            return evictedEntry;
        } else { // Block is in the L2 cache, just change the state
            entry.setState(state);
            entry.touch(cycle);
	    if(state == Block.MSIState.MODIFIED)
		entry.resetOwners(p);
            entry.addToOwners(tileNum);
        }
        
        return null;
    }
    
    public Block.MSIState getState(long address) {
        L2CacheEntry entry = getEntry(address);
        if(entry == null) {
            return Block.MSIState.INVALID;
        } else {
            return entry.getState();
        }
    }
    
    public void removeFromOwners(long address, int tileNum){
        L2CacheEntry entry = getEntry(address);
        entry.removeFromOwners(tileNum);
        if(entry.getState() == Block.MSIState.MODIFIED) {
            entry.setState(Block.MSIState.SHARED);
        }
    }
    
    public L2CacheEntry getEntry(long address) {
        int cacheIndex = (int)Block.L2cacheIndex(address, p, b, n, a);
        //System.out.println("Cache index is 0x" + Integer.toHexString(cacheIndex));
	for (int way = 0; way < (int)Math.pow(2, a); way++) {
            if (cache[way][cacheIndex] != null) {
                L2CacheEntry entry = cache[way][cacheIndex];
                long tag = Block.L2tag(address, p, b, n, a);
                if (entry.getTag() == tag && entry.getState() != Block.MSIState.INVALID) { // Block is in cache, return it
                    //System.out.println("Tag is 0x" + Long.toHexString(tag));
		    return entry;
                }
            }
        }
        return null; // Not found
    }
    
    public void printCache(){
	for(int ind = 0 ; ind < (int)Math.pow(2, n - a - b); ind++){
	    //System.out.println("Following are the states of the blocks at index " + ind);
	    String printLine = "Index " + ind;
	    for(int assoc = 0; assoc < (int) Math.pow(2, a) ; assoc++ ){
		String printState="";
		String printAddress="";
		String ownerTiles = "";
		if(cache[assoc][ind]==null || cache[assoc][ind].getState() == Block.MSIState.INVALID)
		    printState = "INVALID.";
		else if(cache[assoc][ind].getState() == Block.MSIState.SHARED){
		    printState = "SHARED.";
		    printAddress = " Address: 0x" + Long.toHexString(cache[assoc][ind].getAddress()) + ".";
		    boolean[] ownerArray = cache[assoc][ind].getOwnerArray();
		    ownerTiles = " Sharer tile numbers :";
		    for(int i = 0; i<(int)Math.pow(2,p);i++){
			if(ownerArray[i])
			    ownerTiles += " "+i+",";
		    }
		}
		else if(cache[assoc][ind].getState() == Block.MSIState.MODIFIED){
		    printState = "MODIFIED.";
		    printAddress = " Address: 0x" + Long.toHexString(cache[assoc][ind].getAddress()) + ".";
		    boolean[] ownerArray = cache[assoc][ind].getOwnerArray();
		    ownerTiles = " Owner tile :";
		    for(int i = 0; i<(int)Math.pow(2,p);i++){
			if(ownerArray[i])
			    ownerTiles += " "+i+" ";
		    }
		}
		printLine += " Column " + assoc + ": " + printState + printAddress + ownerTiles;
		//System.out.println("Column " + assoc + ": " + printState + printAddress + ownerTiles);
	    }
	    System.out.println(printLine);
	}
    }
}