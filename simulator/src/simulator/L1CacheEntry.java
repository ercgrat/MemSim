package simulator;

public class L1CacheEntry {
	
	protected int tag;
	protected Block.MSIState state;
	protected int lastCycleUsed;

	public L1CacheEntry() {
		state = Block.MSIState.SHARED;
		lastCycleUsed = 0;
	}
	
	public int getTag() {
		return tag;
	}
	
	public Block.MSIState getState() {
		return state;
	}

}