package simulator;

public class L2CacheEntry extends L1CacheEntry {
	
	boolean[] ownerArray;
	
	public L2CacheEntry(int p) {
		super();
		ownerArray = new boolean[p];
	}

}