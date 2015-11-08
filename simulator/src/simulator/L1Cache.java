package simulator;

public class L1Cache {

	int p, b, n, a;
	L1CacheEntry[][] cache;
	
	public L1Cache(int p, int b, int n, int a) {
		this.p = p;
		this.b = b;
		this.n = n;
		this.a = a;
		
		cache = new L1CacheEntry[(int)Math.pow(2, a)][(int)Math.pow(2, n - a)];
	}
	
	public boolean hit(int address) {
		int cacheIndex = Block.cacheIndex(address, p, b, n, a);
		for(int way = 0; way < a; way++) {
			if(cache[way][cacheIndex] != null) {
				L1CacheEntry entry = cache[way][cacheIndex];
				int tag = Block.tag(address, p, b, n, a);
				if(entry.getTag() == tag && entry.getState() == Block.MSIState.SHARED) {
					return true;
				}
			}
		}
		return false;
	}

}