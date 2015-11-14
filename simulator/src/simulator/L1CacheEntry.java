package simulator;

public class L1CacheEntry {

    protected int tag;
    protected Block.MSIState state;
    protected int lastCycleUsed;

    public L1CacheEntry(int tag, Block.MSIState state) {
        this.tag = tag;
        this.state = state;
        lastCycleUsed = 0;
    }

    public int getTag() {
        return tag;
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