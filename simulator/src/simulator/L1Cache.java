package simulator;

public class L1Cache {

	int p, b, n, a;
	L1CacheEntry[][] cache;
	
	public L1Cache(int p, int b, int n, int a) {
		this.p = p;
		this.b = b;
		this.n = n;
		this.a = a;
		
		cache = new L1CacheEntry[(int)Math.pow(2, a)][(int)Math.pow(2, n - a - b)];
	}
	
	public void setEntry(int address, int cycle, Block.MSIState state){
		int cacheIndex = Block.L1cacheIndex(address, p, b, n, a);
		L1CacheEntry entry = new L1CacheEntry();
		entry.touch(cycle);
		entry.setState(state);
		int tag = Block.L1tag(address, p, b, n, a);
		entry.setTag(tag);
		for(int way = 0; way < (int)Math.pow(2,a); way++) {
			if(cache[way][cacheIndex] == null || Block.MSIState.INVALID == cache[way][cacheIndex].getState()) {
				cache[way][cacheIndex] = entry;
				return;
	}
		}
		evict(address, entry);
	}
	
	public void evict(int address, L1CacheEntry entry){
		//TODO
	}
        
	public boolean hit(int address, int cycle) {
		int cacheIndex = Block.L1cacheIndex(address, p, b, n, a);
		for(int way = 0; way < (int)Math.pow(2,a); way++) {
			if(cache[way][cacheIndex] != null) {
				L1CacheEntry entry = cache[way][cacheIndex];
				int tag = Block.L1tag(address, p, b, n, a);
				if(entry.getTag() == tag && entry.getState() == Block.MSIState.SHARED) {
					cache[way][cacheIndex].touch(cycle);
					return true;
				}
			}
		}
		return false;
	}

}