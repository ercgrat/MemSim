package simulator;

public class Directory {
	
	public enum MSIState {
		Modified, Shared, Invalid
	}
	
	MSIState state;
	int lastCycleUsed;

	public Directory() {
		state = MSIState.Shared;
		lastCycleUsed = 0;
	}

}