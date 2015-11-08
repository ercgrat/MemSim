package simulator;

public class L1CacheEntry {
	
	public enum MSIState {
		Modified, Shared, Invalid
	}
	
	protected int tag;
	protected MSIState state;
	protected int lastCycleUsed;

	public L1CacheEntry() {
		state = MSIState.Shared;
		lastCycleUsed = 0;
	}
	
	public int getTag() {
		return tag;
	}

}