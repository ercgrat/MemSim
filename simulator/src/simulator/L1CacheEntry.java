package simulator;

public class L1CacheEntry {

    protected long tag;
    protected Block.MSIState state;
    protected long lastCycleUsed;
    private long address;

    public L1CacheEntry(long tag, Block.MSIState state, long address) {
        this.tag = tag;
        this.state = state;
        lastCycleUsed = 0;
        this.address = address;
    }

    public long getTag() {
        return tag;
    }

    public long getAddress(){
        return address;
    }
    
    public void setTag(long tag) {
        this.tag = tag;
    }

    public void setState(Block.MSIState state) {
        this.state = state;
    }

    public Block.MSIState getState() {
        return state;
    }

    public void touch(long cycle) {
        lastCycleUsed = cycle;
    }
    
    public long getLastCycleUsed() {
        return lastCycleUsed;
    }
}