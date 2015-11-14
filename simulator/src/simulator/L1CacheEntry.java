package simulator;

public class L1CacheEntry {

    protected int tag;
    protected Block.MSIState state;
    protected int lastCycleUsed;
    private int address;

    public L1CacheEntry(int tag, Block.MSIState state, int address) {
        this.tag = tag;
        this.state = state;
        lastCycleUsed = 0;
	this.address = address;
    }

    public int getTag() {
        return tag;
    }

    public int getAddress(){
	return address;
    }
    
    public void setTag(int tag) {
        this.tag = tag;
    }

    public void setState(Block.MSIState state) {
        this.state = state;
    }

    public Block.MSIState getState() {
        return state;
    }

    public void touch(int cycle) {
        lastCycleUsed = cycle;
    }
    
    public int getLastCycleUsed() {
        return lastCycleUsed;
    }
}